package wily.factoryapi.base.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
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

    interface Consumer<T> {
        void apply(T packet, SecureExecutor executor, Supplier<Player> player);
    }

    interface Payload extends Consumer<Payload>,CustomPacketPayload {
        void apply(SecureExecutor executor, Supplier<Player> player);
        default void apply(Payload payload, SecureExecutor executor, Supplier<Player> player){
            payload.apply(executor, player);
        }
    }
}
