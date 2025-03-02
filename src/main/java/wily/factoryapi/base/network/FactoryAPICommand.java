package wily.factoryapi.base.network;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Dynamic;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.UIDefinitionManager;
import wily.factoryapi.base.config.FactoryConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class FactoryAPICommand {
    public static class JsonArgument implements ArgumentType<JsonElement> {
        public static final Map<String, Field> JSON_READER_FIELDS = FactoryAPI.getAccessibleFieldsMap(JsonReader.class,"pos","lineStart");
        public static int getPos(JsonReader jsonReader) {
            try {
                return JSON_READER_FIELDS.get("pos").getInt(jsonReader) - JSON_READER_FIELDS.get("lineStart").getInt(jsonReader) + 1;
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
        var command = Commands.literal("factoryapi").requires(commandSourceStack -> commandSourceStack.hasPermission(2));


        command.then(Commands.literal("display").then(Commands.literal("ui_definition").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("ui_definition", CompoundTagArgument.compoundTag()).executes(context -> {
            CommonNetwork.sendToPlayers(EntityArgument.getPlayers(context, "targets"), new UIDefinitionPayload(Optional.empty(), CompoundTagArgument.getCompoundTag(context, "ui_definition")));
            return 0;
        }).then(Commands.argument("default_screen", ResourceLocationArgument.id()).executes(context -> {
            CommonNetwork.sendToPlayers(EntityArgument.getPlayers(context, "targets"), new UIDefinitionPayload(Optional.of(ResourceLocationArgument.getId(context, "default_screen")), CompoundTagArgument.getCompoundTag(context, "ui_definition")));
            return 0;
        }))))));

        var config = Commands.literal("config");
        FactoryConfig.COMMON_STORAGES.forEach((k,s)->{
            config.then(Commands.literal(k.toString()).then(Commands.literal("reload").executes(c->{
                s.load();
                return 1;
            })).then(Commands.literal("set").then(Commands.argument("value", CompoundTagArgument.compoundTag()).executes(c->{
                s.decodeConfigs(new Dynamic<>(NbtOps.INSTANCE, CompoundTagArgument.getCompoundTag(c, "value")));
                s.sync();
                return 1;
            }))).then(Commands.literal("save").executes(c->{
                s.save();
                return 1;
            })));
        });

        command.then(config);


        commandDispatcher.register(command);
    }

    public record UIDefinitionPayload(Optional<ResourceLocation> defaultScreen, CompoundTag uiDefinitionNbt) implements CommonNetwork.Payload {
        public static final CommonNetwork.Identifier<UIDefinitionPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("ui_definition_s2c"),UIDefinitionPayload::decode);

        @Override
        public void apply(CommonNetwork.SecureExecutor executor, Supplier<Player> player) {
            executor.execute(()-> FactoryAPIClient.uiDefinitionManager.openDefaultScreenAndAddDefinition(defaultScreen, UIDefinitionManager.fromDynamic(ID.toString(), new Dynamic<>(NbtOps.INSTANCE, uiDefinitionNbt))));
        }
        public static UIDefinitionPayload decode(CommonNetwork.PlayBuf buf) {
            return new UIDefinitionPayload(buf.get().readOptional(FriendlyByteBuf::readResourceLocation), buf.get().readNbt());
        }
        @Override
        public void encode(CommonNetwork.PlayBuf buf) {
            buf.get().writeOptional(defaultScreen, FriendlyByteBuf::writeResourceLocation);
            buf.get().writeNbt(uiDefinitionNbt);
        }

        @Override
        public CommonNetwork.Identifier<UIDefinitionPayload> identifier() {
            return ID;
        }
    }
}
