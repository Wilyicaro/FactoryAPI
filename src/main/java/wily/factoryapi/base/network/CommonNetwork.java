package wily.factoryapi.base.network;

//? >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
*///?}
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
//? if >1.20.1 {
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
import net.minecraft.resources.ResourceLocation;
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
import org.apache.commons.lang3.tuple.Pair;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CommonNetwork {
    abstract class SecureExecutor implements Executor {
        public abstract boolean isSecure();
        final Collection<BooleanSupplier> queue = new ConcurrentLinkedQueue<>();
        public void executeAll(){
            queue.removeIf(BooleanSupplier::getAsBoolean);
        }
        public void execute(Runnable runnable){
            executeWhen(()->{
                if (isSecure()) {
                    runnable.run();
                    return true;
                }return false;
            });
        }
        public void executeWhen(BooleanSupplier supplier){
            queue.add(supplier);
        }
        public void clear(){
            queue.clear();
        }

    }

    interface Identifier<T extends Payload>{
        ResourceLocation location();
        T decode(/*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/ buf);
        //? >=1.20.5 {
        /*CustomPacketPayload.Type<T> type();
        StreamCodec<RegistryFriendlyByteBuf,T> codec();
        *///?}
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

    interface PlayBuf extends Supplier</*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*/ /*RegistryFriendlyByteBuf *//*?}*/>{ }

    interface Payload /*? if >1.20.1 {*/ extends CustomPacketPayload /*?}*/{
        void apply(SecureExecutor executor, Supplier<Player> player);

        default void applyClient(){
            apply(FactoryAPIClient.SECURE_EXECUTOR,FactoryAPIClient::getClientPlayer);
        }

        default void applyServer(Supplier<Player> player){
            apply(FactoryAPI.SECURE_EXECUTOR,player);
        }
        default void applySided(Supplier<Player> player){
            if (FactoryAPIPlatform.isClient()) applyClient();
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
    static <T extends CommonNetwork.Payload> void sendToPlayer(ServerPlayer serverPlayer, T packetHandler) {
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
        PacketDistributor.PLAYER.with(/^? if <=1.20.1 {^/ ()->/^?}^/serverPlayer).send(NetworkDirection.PLAY_TO_CLIENT.buildPacket(/^? if <=1.20.1 {^/ Pair.of(buf,0)/^?} else {^/ /^buf^//^?}^/, packetHandler.identifier().location()).getThis());
        //?} else
        /^PacketDistributor.PLAYER.with(serverPlayer).send(NetworkProtocol.PLAY.buildPacket(PacketFlow.CLIENTBOUND,packetHandler.type().id(), packetHandler::encode));^/
        *///?} elif neoforge {
        /*//? if <1.20.5 {
        /^PacketDistributor.PLAYER.with(serverPlayer).send(packetHandler);
         ^///?} else
        PacketDistributor.sendToPlayer(serverPlayer, packetHandler);
        *///?} else {
        /*throw new AssertionError();
         *///?}
    }

    static <T extends CommonNetwork.Payload> void sendToPlayers(Collection<ServerPlayer> serverPlayer, T packetHandler) {
        serverPlayer.forEach(s->sendToPlayer(s,packetHandler));
    }

    static <T extends CommonNetwork.Payload> void sendToServer(T packetHandler) {
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
        PacketDistributor.SERVER.noArg().send(NetworkDirection.PLAY_TO_CLIENT.buildPacket(/^? if <=1.20.1 {^/ Pair.of(buf,0)/^?} else {^/ /^buf^//^?}^/, packetHandler.identifier().location()).getThis());
        //?} else
        /^PacketDistributor.SERVER.noArg().send(NetworkProtocol.PLAY.buildPacket(PacketFlow.SERVERBOUND,packetHandler.type().id(), packetHandler::encode));^/
        *///?} elif neoforge {
        /*//? if <1.20.5 {
        /^PacketDistributor.SERVER.noArg().send(packetHandler);
         ^///?} else
        PacketDistributor.sendToServer(packetHandler);
        *///?} else {
        /*throw new AssertionError();
         *///?}
    }
}
