package wily.factoryapi.base.network;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public abstract class SecureExecutor implements Executor {
    public abstract boolean isSecure();

    final Collection<BooleanSupplier> queue = new ConcurrentLinkedQueue<>();

    public void executeAll() {
        queue.removeIf(BooleanSupplier::getAsBoolean);
    }

    public void execute(Runnable runnable) {
        executeWhen(() -> {
            if (isSecure()) {
                runnable.run();
                return true;
            }
            return false;
        });
    }

    public void executeWhen(BooleanSupplier supplier) {
        queue.add(supplier);
    }

    public void executeNowIfPossible(BooleanSupplier supplier) {
        if (!supplier.getAsBoolean()) executeWhen(supplier);
    }

    static BooleanSupplier createBooleanRunnable(Runnable action, BooleanSupplier supplier) {
        return () -> {
            boolean execute = supplier.getAsBoolean();
            if (execute) {
                action.run();
                return true;
            }
            return false;
        };
    }

    public void executeNowIfPossible(Runnable action, BooleanSupplier supplier) {
        executeNowIfPossible(createBooleanRunnable(action, supplier));
    }

    public void clear() {
        queue.clear();
    }

}
