package wily.factoryapi.fabric;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
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
import wily.factoryapi.base.*;
import wily.factoryapi.fabriclike.base.FabricEnergyStorage;
import wily.factoryapi.fabriclike.base.FabricFluidStorage;
import wily.factoryapi.fabriclike.base.FabricItemStorage;

import java.nio.file.Path;
import java.util.function.Predicate;

public class FactoryAPIPlatformImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
    public static IPlatformFluidHandler getFluidHandlerApi(long Capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transportState) {
        // Just throw an error, the content should get replaced at runtime.
        return new FabricFluidStorage(Capacity, be,validator,differential, transportState);
    }
    public static IPlatformItemHandler getItemHandlerApi(int Capacity, BlockEntity be) {
        // Just throw an error, the content should get replaced at runtime.
        return new FabricItemStorage(Capacity, be, TransportState.EXTRACT_INSERT);

    }
    public static IPlatformItemHandler filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        return FabricItemStorage.filtered(itemHandler, direction,slots,transportState);
    }


    public static IPlatformFluidHandler filteredOf(IPlatformFluidHandler fluidHandler, TransportState transportState) {
        return FabricFluidStorage.filtered(fluidHandler,transportState);
    }
    public static IPlatformFluidHandler getFluidItemHandlerApi(long Capacity, ItemStack container, Predicate<FluidStack> validator, TransportState transportState) {
        // Just throw an error, the content should get replaced at runtime.
        Storage<FluidVariant> handStorage = ContainerItemContext.withInitial(container).find(FluidStorage.ITEM);
        if (handStorage instanceof  IPlatformFluidHandler p) return p;
        return null;
    }

    public static IPlatformEnergyStorage getEnergyStorageApi(int Capacity, BlockEntity be) {
        if (!Platform.isModLoaded("techreborn")) return null;
        return new FabricEnergyStorage(Capacity,be);
    }

    public static Component getPlatformEnergyComponent() {
        return Component.literal("Energy (E)").withStyle(ChatFormatting.GOLD);
    }
}
