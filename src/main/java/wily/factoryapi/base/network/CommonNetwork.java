package wily.factoryapi.base.network;

//? >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
*///?}
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.network.FriendlyByteBuf;
//? if >1.20.1 {
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.ComponentSerialization;
//?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
//? if fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?} elif forge {
/*import net.minecraftforge.network.NetworkDirection;
//? if >=1.20.5
/^import net.minecraftforge.network.NetworkProtocol;^/
import net.minecraftforge.network.PacketDistributor;
*///?} elif neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CommonNetwork {
    List<UUID> ENABLED_PLAYERS = new ArrayList<>();

    interface Identifier<T extends Payload>{
        ResourceLocation location();
        T decode(/*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/ buf);
        //? >=1.20.5 {
        /*CustomPacketPayload.Type<T> type();
        StreamCodec<RegistryFriendlyByteBuf,T> codec();
        *///?}
        static <T extends Payload> Identifier<T> create(ResourceLocation location, Supplier<T> decoder){
            return create(location,b->decoder.get());
        }
        static <T extends Payload> Identifier<T> create(ResourceLocation location, Function<PlayBuf,T> decoder){
            //? >=1.20.5 {
            /*CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(location);
            StreamCodec<RegistryFriendlyByteBuf,T> codec = StreamCodec.of((b,p)->p.encode(b),b->decoder.apply(()->b));
            *///?}
            return new Identifier<>() {
                @Override
                public ResourceLocation location() {
                    return location;
                }

                @Override
                public T decode(/*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/ buf) {
                    return decoder.apply(()->buf);
                }

                //? >=1.20.5 {
                /*@Override
                public CustomPacketPayload.Type<T> type() {
                    return type;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, T> codec() {
                    return codec;
                }
                *///?}
            };
        }
    }

    interface PlayBuf extends Supplier</*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/>{
        static PlayBuf create() {
            return fromBuf(new FriendlyByteBuf(Unpooled.buffer()));
        }

        static PlayBuf of(/*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/buf) {
            return ()-> buf;
        }

        static PlayBuf fromBuf(FriendlyByteBuf buf) {
            return of(/*? if <1.20.5 {*/buf/*?} else {*//*new RegistryFriendlyByteBuf(buf, FactoryAPIPlatform.getRegistryAccess())*//*?}*/);
        }
    }

    interface Payload /*? if >1.20.1 {*/ extends CustomPacketPayload /*?}*/{
        interface Context {
            SecureExecutor executor();

            Player player();

            default MinecraftServer server(){
                return player().getServer();
            }

            boolean isClient();

            static Context createClientContext(){
                return createContext(FactoryAPIClient.SECURE_EXECUTOR, FactoryAPIClient::getClientPlayer, true);
            }

            static Context createServerContext(Supplier<Player> player){
                return createContext(FactoryAPI.SECURE_EXECUTOR, player, true);
            }

            static Context createContext(SecureExecutor executor, Supplier<Player> playerSupplier, boolean isClient){
                return new Context() {
                    @Override
                    public SecureExecutor executor() {
                        return executor;
                    }

                    @Override
                    public Player player() {
                        return playerSupplier.get();
                    }

                    @Override
                    public boolean isClient() {
                        return isClient;
                    }
                };
            }
        }

        void apply(Context context);

        default void applyClient(){
            apply(Context.createClientContext());
        }

        default void applyServer(Supplier<Player> player){
            apply(Context.createServerContext(player));
        }

        default void applySided(boolean client, Supplier<Player> player){
            if (client) applyClient();
            else applyServer(player);
        }

        Identifier<? extends Payload> identifier();

        default void encode(/*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/ buf){
            encode(()->buf);
        }

        void encode(PlayBuf buf);

        //? >=1.20.5 {
        /*@Override
        default Type<? extends CustomPacketPayload> type(){
            return identifier().type();
        }
        *///?} else if >1.20.1 {

        default void write(FriendlyByteBuf buf){
            encode(buf);
        }

        default ResourceLocation id(){
            return identifier().location();
        }
        //?}
    }

    abstract class EmptyPayload implements Payload{
        private final Identifier<? extends Payload> identifier;

        public EmptyPayload(Identifier<? extends Payload> identifier){
            this.identifier = identifier;
        }

        @Override
        public void encode(PlayBuf buf) {
        }

        @Override
        public Identifier<? extends Payload> identifier() {
            return identifier;
        }
    }

    static void forceEnabledPlayer(ServerPlayer player, Runnable runnable){
        boolean contains = ENABLED_PLAYERS.contains(player.getUUID());
        if (contains){
            runnable.run();
        } else {
            ENABLED_PLAYERS.add(player.getUUID());
            runnable.run();
            ENABLED_PLAYERS.remove(player.getUUID());
        }
    }

    static <T extends CommonNetwork.Payload> void sendToPlayer(ServerPlayer serverPlayer, T packetHandler) {
        if (!ENABLED_PLAYERS.contains(serverPlayer.getUUID())) return;
        //? if fabric {
        //? if <1.20.5 {
        FriendlyByteBuf buf = PacketByteBufs.create();
        packetHandler.encode(buf);
        ServerPlayNetworking.send(serverPlayer,packetHandler.identifier().location(), buf);
        //?} else
        /*ServerPlayNetworking.send(serverPlayer,packetHandler);*/
        //?} elif forge {
        /*//? if <1.20.5 {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetHandler.encode(buf);
        PacketDistributor.PLAYER.with(/^? if <=1.20.1 {^/ /^()->^//^?}^/serverPlayer).send(NetworkDirection.PLAY_TO_CLIENT.buildPacket(/^? if <=1.20.1 {^/ /^Pair.of(buf,0)^//^?} else {^/ buf/^?}^/, packetHandler.identifier().location()).getThis());
        //?} else
        /^PacketDistributor.PLAYER.with(serverPlayer).send(NetworkProtocol.PLAY.buildPacket(PacketFlow.CLIENTBOUND, packetHandler.type().id(), packetHandler::encode));^/
        *///?} elif neoforge {
        /*//? if <1.20.5 {
        PacketDistributor.PLAYER.with(serverPlayer).send(packetHandler);
         //?} else
        /^PacketDistributor.sendToPlayer(serverPlayer, packetHandler);^/
        *///?} else {
        /*throw new AssertionError();
         *///?}
    }

    static <T extends CommonNetwork.Payload> void sendToPlayers(Collection<ServerPlayer> serverPlayer, T packetHandler) {
        serverPlayer.forEach(s->sendToPlayer(s,packetHandler));
    }

    static <T extends CommonNetwork.Payload> void sendToServer(T packetHandler) {
        if (!FactoryAPI.isClient() || !FactoryAPIClient.hasModOnServer) return;
        //? if fabric {
        //? if <1.20.5 {
        FriendlyByteBuf buf = PacketByteBufs.create();
        packetHandler.encode(buf);
        ClientPlayNetworking.send(packetHandler.identifier().location(),buf);
        //?} else
        /*ClientPlayNetworking.send(packetHandler);*/
        //?} elif forge {
        /*//? if <1.20.5 {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetHandler.encode(buf);
        PacketDistributor.SERVER.noArg().send(NetworkDirection.PLAY_TO_SERVER.buildPacket(/^? if <=1.20.1 {^/ /^Pair.of(buf,0)^//^?} else {^/ buf/^?}^/, packetHandler.identifier().location()).getThis());
        //?} else
        /^PacketDistributor.SERVER.noArg().send(NetworkProtocol.PLAY.buildPacket(PacketFlow.SERVERBOUND, packetHandler.type().id(), packetHandler::encode));^/
        *///?} elif neoforge {
        /*//? if <1.20.5 {
        PacketDistributor.SERVER.noArg().send(packetHandler);
         //?} else
        /^PacketDistributor.sendToServer(packetHandler);^/
        *///?} else {
        /*throw new AssertionError();
         *///?}
    }

    static void encodeComponent(PlayBuf buf, Component component){
        /*? if >=1.20.5 {*/ /*ComponentSerialization.STREAM_CODEC.encode(buf.get(),component) *//*?} else {*/ buf.get().writeComponent(component)/*?}*/;
    }
    static Component decodeComponent(PlayBuf buf){
        return /*? if >=1.20.5 {*/ /*ComponentSerialization.STREAM_CODEC.decode(buf.get()) *//*?} else {*/ buf.get().readComponent()/*?}*/;
    }
    static void encodeItemStack(PlayBuf buf, ItemStack stack){
        /*? if >=1.20.5 {*/ /*ItemStack.OPTIONAL_STREAM_CODEC.encode(buf.get(),stack) *//*?} else {*/ buf.get().writeItem(stack)/*?}*/;
    }
    static ItemStack decodeItemStack(PlayBuf buf){
        return /*? if >=1.20.5 {*/ /*ItemStack.OPTIONAL_STREAM_CODEC.decode(buf.get()) *//*?} else {*/ buf.get().readItem()/*?}*/;
    }

    static void encodeBuf(PlayBuf buf, PlayBuf toEncode){
        buf.get().writeVarInt(toEncode.get().readableBytes());
        buf.get().writeBytes(toEncode.get());
    }

    static PlayBuf decodeBuf(PlayBuf buf){
        int readable = buf.get().readVarInt();
        if (readable > 0) return PlayBuf.fromBuf(new FriendlyByteBuf(buf.get().readBytes(readable)));
        else return PlayBuf.create();
    }

}
