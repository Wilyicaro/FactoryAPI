package wily.factoryapi.base.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.client.UIDefinition;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public class FactoryAPICommand {
    public static class JsonArgument implements ArgumentType<JsonElement> {
        public static final Field JSON_READER_POS = Util.make(() -> {
            try {
                Field field = JsonReader.class.getDeclaredField("pos");
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException var1) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
            }
        });
        public static final Field JSON_READER_LINESTART = Util.make(() -> {
            try {
                Field field = JsonReader.class.getDeclaredField("lineStart");
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException var1) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
            }
        });
        public static int getPos(JsonReader jsonReader) {
            try {
                return JSON_READER_POS.getInt(jsonReader) - JSON_READER_LINESTART.getInt(jsonReader) + 1;
            } catch (IllegalAccessException var2) {
                throw new IllegalStateException("Couldn't read position of JsonReader", var2);
            }
        }
        private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");

        public static JsonElement getJson(CommandContext<CommandSourceStack> commandContext, String string) {
            return commandContext.getArgument(string, JsonElement.class);
        }

        public static JsonArgument json() {
            return new JsonArgument();
        }

        public JsonElement parse(StringReader stringReader) {
            JsonElement element;
            JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
            jsonReader.setLenient(false);

            try {
                element = Streams.parse(jsonReader);
            }finally {
                stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
            }
            return element;
        }

        public Collection<String> getExamples() {
            return EXAMPLES;
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext) {
        commandDispatcher.register(Commands.literal("factoryapi").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).
                then(Commands.literal("display").then(Commands.literal("ui_definition").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("ui_definition", JsonArgument.json()).executes(context -> {
                    CommonNetwork.sendToPlayers(EntityArgument.getPlayers(context, "targets"), new UIDefinitionPayload(JsonArgument.getJson(context, "ui_definition")));
                    return 0;
                }))))));
    }

    public record UIDefinitionPayload(JsonElement uiDefinitionElement) implements CommonNetwork.Payload {
        public static final CommonNetwork.Identifier<UIDefinitionPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("ui_definition_s2c"),UIDefinitionPayload::decode);

        @Override
        public void apply(CommonNetwork.SecureExecutor executor, Supplier<Player> player) {
            executor.execute(()-> FactoryAPIClient.uiDefinitionManager.openScreenAndAddDefinition(UIDefinition.Manager.fromDynamic(ID.toString(), new Dynamic<>(JsonOps.INSTANCE, uiDefinitionElement))));
        }
        public static UIDefinitionPayload decode(CommonNetwork.PlayBuf buf) {
            return new UIDefinitionPayload(JsonParser.parseString(buf.get().readUtf()));
        }
        @Override
        public void encode(CommonNetwork.PlayBuf buf) {
            buf.get().writeUtf(uiDefinitionElement.toString());
        }

        @Override
        public CommonNetwork.Identifier<UIDefinitionPayload> identifier() {
            return ID;
        }
    }
}
