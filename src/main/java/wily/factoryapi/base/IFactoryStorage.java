package wily.factoryapi.base;

import net.minecraft.core.Direction;

public interface IFactoryStorage {

    default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(FactoryStorage<T> storage){return getStorage(storage, null);}

    <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(FactoryStorage<T> storage, Direction direction);

    default ArbitrarySupplier<SideList<TransportSide>> getStorageSides(FactoryStorage<?> storage){return ArbitrarySupplier.empty();}

}
