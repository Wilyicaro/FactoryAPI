package wily.factoryapi.base;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@FunctionalInterface

public interface ArbitrarySupplier<T> extends Supplier<T> {
    static<T> ArbitrarySupplier<T> empty(){
        return ()-> null;
    }
    static <T> ArbitrarySupplier<T> of(T object){
        return ()-> object;
    }

    default <O> Object orObject(@NotNull O alternative){
        return isPresent() ? get() : alternative;
    }
    default T or(@NotNull T alternative){
        return or(()-> alternative).get();
    }
    default ArbitrarySupplier<T> or(ArbitrarySupplier<@NotNull T> alternative){
        return isPresent() ? this : alternative;
    }
    default void ifPresent(Consumer<T> consumer){
        if (isPresent()) consumer.accept(get());
    }
    default boolean isPresent(){
        return !isEmpty();
    }
    default boolean isEmpty(){
        return get() == null;
    }
    default boolean isPresentAnd(Predicate<T> predicate){
        return isPresent() && predicate.test(get());
    }
    default  <U> ArbitrarySupplier<U> cast() {
        return (ArbitrarySupplier<U>)this;
    }
    default Optional<T> optional(){
        return Optional.ofNullable(get());
    }
}
