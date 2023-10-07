package wily.factoryapi.base;

import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface IFactoryStorage {
    default <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage){return getStorage(storage, null);}


    <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction);

    default ArbitrarySupplier<SideList<FluidSide>> fluidSides(){return ArbitrarySupplier.empty();}

    default ArbitrarySupplier<SideList<ItemSide>> itemSides(){return ArbitrarySupplier.empty();}

    default ArbitrarySupplier<SideList<TransportState>> energySides(){return ArbitrarySupplier.empty();}

}
