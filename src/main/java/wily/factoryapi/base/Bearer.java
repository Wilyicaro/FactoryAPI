package wily.factoryapi.base;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Bearer<T> extends ArbitrarySupplier<T> {
    static <T> Bearer<T> empty(){
        return new Bearer<>() {
            @Override
            public void set(T obj) {
            }
            @Override
            public T get() {
                return null;
            }
        };
    }

    @Override
    default <U> Bearer<U> secureCast(Class<U> aimClass) {
        return isEmpty() ? empty() : aimClass.isInstance(get()) ? (Bearer<U>) this : empty();
    }
    void set(T obj);
    static <T> Bearer<T> of(T obj) {
        return new Stocker<>(obj);
    }
    static <T> Bearer<T> of(Supplier<T> obj, Consumer<T> setObj) {
        return new Bearer<>() {
            @Override
            public void set(T obj) {
                setObj.accept(obj);
            }

            @Override
            public T get() {
                return obj.get();
            }
        };
    }

}
