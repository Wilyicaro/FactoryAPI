package wily.factoryapi.base;

import net.minecraft.core.Direction;

public interface IFactoryStorage {

    default <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage){return getStorage(storage, null);}


    <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction);

    default ArbitrarySupplier<SideList<? super ISideType<?>>> getStorageSides(Storages.Storage<?> storage){return ArbitrarySupplier.empty();}


}
