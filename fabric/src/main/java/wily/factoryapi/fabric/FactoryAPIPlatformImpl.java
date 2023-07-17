package wily.factoryapi.fabric;

import dev.architectury.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.fabric.base.FabricEnergyStorage;
import wily.factoryapi.fabric.base.FabricFluidStorage;
import wily.factoryapi.fabric.base.FabricItemFluidStorage;
import wily.factoryapi.fabric.base.FabricItemStorage;

import java.nio.file.Path;
import java.util.function.Predicate;

public class FactoryAPIPlatformImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
    public static IPlatformFluidHandler getFluidHandlerApi(long Capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transportState) {
        return new FabricFluidStorage(Capacity, be,validator,differential, transportState);
    }
    public static IPlatformItemHandler getItemHandlerApi(int Capacity, BlockEntity be) {
        return new FabricItemStorage(Capacity, be, TransportState.EXTRACT_INSERT);

    }
    public static IPlatformItemHandler filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        return FabricItemStorage.filtered(itemHandler, direction,slots,transportState);
    }


    public static IPlatformFluidHandler filteredOf(IPlatformFluidHandler fluidHandler, TransportState transportState) {
        return FabricFluidStorage.filtered(fluidHandler,transportState);
    }
    public static IPlatformFluidHandler getFluidItemHandlerApi(ItemStack container, IFluidItem.FluidStorageBuilder builder) {
        Storage<FluidVariant> handStorage = ItemContainerUtilImpl.slotContextFromItemStack(container).find(FluidStorage.ITEM);
        if (handStorage instanceof  IPlatformFluidHandler p) return p;
        return new FabricItemFluidStorage(ContainerItemContext.withConstant(container),builder);
    }

    public static IPlatformEnergyStorage getEnergyStorageApi(int Capacity, BlockEntity be) {
        return new FabricEnergyStorage(Capacity,be);
    }

    public static Component getPlatformEnergyComponent() {
        return Component.literal("Energy (E)").withStyle(ChatFormatting.GOLD);
    }

    public static IPlatformEnergyStorage filteredOf(IPlatformEnergyStorage fluidHandler, TransportState transportState) {
        return FabricEnergyStorage.filtered(fluidHandler, transportState);
    }
}
