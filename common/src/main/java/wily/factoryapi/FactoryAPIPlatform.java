package wily.factoryapi;

import dev.architectury.fluid.FluidStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class FactoryAPIPlatform {
    public static final Map<IPlatformHandler, SideList<? super IModifiableTransportHandler>> filteredHandlersCache = new ConcurrentHashMap<>();
    public static final Map<BlockEntity, IFactoryStorage> platformStorageWrappersCache = new ConcurrentHashMap<>();
    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static Component getPlatformEnergyComponent() {
        throw new AssertionError();
    }
    @Deprecated
    public static IPlatformFluidHandler getFluidHandlerApi(long capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transport) {
        return new FactoryFluidHandler(capacity,be,validator,differential,transport);
    }
    @Deprecated
    public static IPlatformItemHandler getItemHandlerApi(int inventorySize, BlockEntity be) {
        return new FactoryItemHandler(inventorySize,be, TransportState.EXTRACT_INSERT);
    }
    @Deprecated
    public static IPlatformEnergyStorage getEnergyStorageApi(int capacity, BlockEntity be) {
        return new FactoryEnergyStorage(capacity,be);
    }
    @ExpectPlatform
    public static IPlatformFluidHandler getItemFluidHandler(ItemStack container) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static IPlatformEnergyStorage getItemEnergyStorage(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ICraftyEnergyStorage getItemCraftyEnergyStorage(ItemStack stack) {
        throw new AssertionError();
    }

    public static <T extends IPlatformHandler, U extends IModifiableTransportHandler & IPlatformHandler> U filteredOf(T handler, Direction direction, TransportState transportState, Function<T,U> sidedGetter) {
        filteredHandlersCache.entrySet().removeIf(e-> e.getKey().isRemoved());
        SideList<? super IModifiableTransportHandler> list = filteredHandlersCache.computeIfAbsent(handler, d-> new SideList<>(() -> null));
        if (!list.contains(direction)) list.put(direction,sidedGetter.apply(handler));
        list.get(direction).setTransport(transportState);
        return list.get(direction) != null ? (U)list.get(direction) : null;
    }
    public static IPlatformItemHandler filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        FactoryItemHandler.SidedWrapper storage = FactoryAPIPlatform.filteredOf(itemHandler,direction,transportState,FactoryItemHandler.SidedWrapper::new);
        if (storage != null) storage.slots = slots;
        return storage;
    }
    public static IPlatformFluidHandler filteredOf(IPlatformFluidHandler fluidHandler, Direction direction, TransportState transportState) {
        return filteredOf((FactoryFluidHandler)fluidHandler,direction,transportState,FactoryFluidHandler.SidedWrapper::new);
    }
    public static IPlatformEnergyStorage filteredOf(IPlatformEnergyStorage energyStorage, Direction direction, TransportState transportState) {
        return filteredOf((FactoryEnergyStorage) energyStorage,direction,transportState,FactoryEnergyStorage.SidedWrapper::new);
    }

    @ExpectPlatform
    public static IFactoryStorage getPlatformFactoryStorage(BlockEntity entity) {
        throw new AssertionError();
    }

}
