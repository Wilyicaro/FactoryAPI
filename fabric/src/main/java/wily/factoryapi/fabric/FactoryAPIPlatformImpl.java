package wily.factoryapi.fabric;

import dev.architectury.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.impl.SimpleItemEnergyStorageImpl;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.fabric.base.*;

import java.nio.file.Path;
import java.util.function.Predicate;

public class FactoryAPIPlatformImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
    public static IPlatformFluidHandler<?> getFluidHandlerApi(long Capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transportState) {
        return new FabricFluidStorage(Capacity, be,validator,differential, transportState);
    }
    public static IPlatformItemHandler<?> getItemHandlerApi(int Capacity, BlockEntity be) {
        return new FabricItemStorage(Capacity, be, TransportState.EXTRACT_INSERT);

    }
    public static IPlatformItemHandler<?> filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        return FabricItemStorage.filtered(itemHandler, direction,slots,transportState);
    }


    public static IPlatformFluidHandler<?> filteredOf(IPlatformFluidHandler fluidHandler, TransportState transportState) {
        return FabricFluidStorage.filtered(fluidHandler,transportState);
    }
    public static IPlatformFluidHandler<?> getItemFluidHandler(ItemStack container) {
        ContainerItemContext context = ItemContainerUtilImpl.slotContextFromItemStack(container);
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);
        if (handStorage instanceof  IPlatformFluidHandler<?> p) return p;
        if (container.getItem() instanceof IFluidHandlerItem<?>) return getItemFluidHandler(container,context);
        return handStorage != null ? (FabricFluidStoragePlatform)()-> handStorage : null;
    }

    public static IPlatformFluidHandler<?> getItemFluidHandler(ItemStack container, ContainerItemContext context) {
        return container.getItem() instanceof IFluidHandlerItem<?> f ? new FabricItemFluidStorage(context,f.getCapacity(),f::isFluidValid,f.getTransport()) : null;
    }

    public static IPlatformEnergyStorage<?> getItemEnergyStorage(ItemStack container) {
        ContainerItemContext context = ItemContainerUtilImpl.slotContextFromItemStack(container);
        EnergyStorage handStorage = EnergyStorage.ITEM.find(container,context);
        if (handStorage instanceof  IPlatformEnergyStorage<?> p) return p;
        if (container.getItem() instanceof IEnergyStorageItem<?>) return getItemEnergyStorage(container,context);
        return handStorage != null ? (FabricEnergyStoragePlatform)()-> handStorage : null;
    }
    public static IPlatformEnergyStorage<?> getItemEnergyStorage(ItemStack container, ContainerItemContext context) {
        return  container.getItem() instanceof IEnergyStorageItem<?> f  ? (FabricEnergyStoragePlatform) ()-> SimpleItemEnergyStorageImpl.createSimpleStorage(context,f.getCapacity(),f.getTransport().canInsert() ? f.getMaxReceive() : 0,f.getTransport().canExtract() ? f.getMaxConsume() : 0) : null;
    }

    public static IPlatformEnergyStorage<?> getEnergyStorageApi(int Capacity, BlockEntity be) {
        return new FabricEnergyStorage(Capacity,be);
    }

    public static Component getPlatformEnergyComponent() {
        return Component.literal("Energy (E)").withStyle(ChatFormatting.GOLD);
    }

    public static IPlatformEnergyStorage filteredOf(IPlatformEnergyStorage fluidHandler, TransportState transportState) {
        return FabricEnergyStorage.filtered(fluidHandler, transportState);
    }

    public static IFactoryStorage getPlatformFactoryStorage(BlockEntity be) {
        if (be instanceof IFactoryStorage s) return s;
        return new IFactoryStorage() {
            @Override
            public <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
                if (be.hasLevel())
                    if (storage == Storages.ITEM) {
                        Storage<ItemVariant> variantStorage = ItemStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (variantStorage instanceof IPlatformItemHandler<?>) return ()->((T) variantStorage);
                        if (variantStorage!= null)
                            return ()->((T)(FabricItemStoragePlatform)()-> variantStorage);
                    } else if (storage == Storages.FLUID) {
                        Storage<FluidVariant> variantStorage = FluidStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (variantStorage instanceof IPlatformFluidHandler<?>) return ()->(T) variantStorage;
                        if (variantStorage!= null)
                            return ()->((T)(FabricFluidStoragePlatform) ()-> variantStorage);
                    }else if (storage == Storages.ENERGY) {
                        EnergyStorage energyStorage = EnergyStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (energyStorage instanceof IPlatformEnergyStorage<?>) return ()->(T) energyStorage;
                        if (energyStorage!= null)
                            return ()->((T)(FabricEnergyStoragePlatform)()-> energyStorage);
                    }
                    else if (storage == Storages.CRAFTY_ENERGY) {
                        ICraftyEnergyStorage energyStorage = CraftyEnergyStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (energyStorage!= null) return ()->((T)energyStorage);
                    }
                return ()-> null;
            }
        };
    }


    public static ICraftyEnergyStorage getItemCraftyEnergyStorage(ItemStack container) {
        ICraftyEnergyStorage craftyStorage = CraftyEnergyStorage.ITEM.find(container,ItemContainerUtilImpl.slotContextFromItemStack(container));
        if (craftyStorage == null) return getItemCraftyEnergyStorageApi(container);
        return craftyStorage;
    }
    public static ICraftyEnergyStorage getItemCraftyEnergyStorageApi(ItemStack container) {
        return container.getItem() instanceof ICraftyStorageItem f ? new SimpleItemCraftyStorage(container,0,f.getCapacity(), f.getMaxConsume(), f.getMaxReceive(),f.getTransport(),f.getSupportedEnergyTier(), ItemContainerUtil.isBlockItem(container)) : null;
    }
}
