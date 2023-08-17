package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.util.StorageUtil;

import java.util.*;
import java.util.function.Function;

public interface IFactoryStorage {

    default <T extends IPlatformHandlerApi<?>> Optional<T> getStorage(Storages.Storage<T> storage){return getStorage(storage, null);}


    <T extends IPlatformHandlerApi<?>> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction);

    default Optional<SideList<FluidSide>> fluidSides(){return Optional.empty();}

    default Optional<SideList<ItemSide>> itemSides(){return Optional.empty();}

    default Optional<SideList<TransportState>> energySides(){return Optional.empty();}

}
