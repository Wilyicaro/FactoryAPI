package wily.factoryapi.base;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@FunctionalInterface
public interface ArbitrarySupplier<T> extends Supplier<T> {
    ArbitrarySupplier<?> EMPTY = ()-> null;

    static<T> ArbitrarySupplier<T> empty(){
        return (ArbitrarySupplier<T>) EMPTY;
    }

    static <T> ArbitrarySupplier<T> of(T object){
        return ()-> object;
    }

    default <O> Object orObject(@NotNull O alternative){
        return isPresent() ? get() : alternative;
    }

    default T orElse(T alternative){
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

    default void ifPresentAnd(Consumer<T> consumer, Predicate<T> predicate){
        if (isPresent() && predicate.test(get())) consumer.accept(get());
    }

    default void accept(Consumer<T> consumer, T alternative){
        consumer.accept(isPresent() ? get() : alternative);
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

    default  <U> ArbitrarySupplier<U> secureCast(Class<U> aimClass) {
        return map(o-> aimClass.isInstance(o) ? aimClass.cast(o) : null);
    }

    default  <U> ArbitrarySupplier<U> cast() {
        return (ArbitrarySupplier<U>)this;
    }

    default <U> ArbitrarySupplier<U> map(Function<T,U> mapping){
        return isPresent() ? ()-> mapping.apply(get()) : empty();
    }

    default <U> ArbitrarySupplier<U> flatMap(Function<T,ArbitrarySupplier<U>> mapping){
        return isPresent() ? mapping.apply(get()) : empty();
    }

    default Optional<T> optional(){
        return Optional.ofNullable(get());
    }
}
