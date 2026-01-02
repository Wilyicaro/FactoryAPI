package wily.factoryapi.base;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.FactoryAPI;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface RegisterListing<T> extends Iterable<RegisterListing.Holder<T>> {
    void register();

    default <V extends T> Holder<V> add(String name, Supplier<V> supplier){
        return add(name, id-> supplier.get());
    }

    <V extends T> Holder<V> add(String name, Function<ResourceLocation, V> supplier);

    Stream<Holder<T>> stream();

    Collection<Holder<T>> getEntries();

    Registry<T> getRegistry();

    String getNamespace();

    interface Holder<T> extends Supplier<T> {
        ResourceLocation getId();
    }
}
