package wily.factoryapi.base;

import net.minecraft.core.Direction;

public interface IFactoryStorage {

    default <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage){return getStorage(storage, null);}


    <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction);

    default ArbitrarySupplier<SideList<FluidSide>> fluidSides(){return ArbitrarySupplier.empty();}

    default ArbitrarySupplier<SideList<ItemSide>> itemSides(){return ArbitrarySupplier.empty();}

    default ArbitrarySupplier<SideList<TransportState>> energySides(){return ArbitrarySupplier.empty();}

}
