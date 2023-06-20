package wily.factoryapi.forge;

import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.loading.FMLPaths;
import wily.factoryapi.base.*;
import wily.factoryapi.forge.base.ForgeEnergyStorage;
import wily.factoryapi.forge.base.ForgeFluidHandler;
import wily.factoryapi.forge.base.ForgeItemFluidHandler;
import wily.factoryapi.forge.base.ForgeItemHandler;

import java.nio.file.Path;
import java.util.function.Predicate;

public class FactoryAPIPlatformImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
    public static IPlatformFluidHandler<?> getFluidHandlerApi(long Capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transportState) {

        return new ForgeFluidHandler(Capacity, be, validator, differential, transportState);
    }
    public static IPlatformItemHandler getItemHandlerApi(int inventorySize, BlockEntity be) {

        return new ForgeItemHandler(inventorySize, be, TransportState.EXTRACT_INSERT);
    }

    public static IPlatformFluidHandler<?> getFluidItemHandlerApi(ItemStack container, IFluidItem.FluidStorageBuilder builder) {
        return new ForgeItemFluidHandler(container,builder);
    }


    public static IPlatformItemHandler filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        return ForgeItemHandler.filtered(itemHandler,direction,slots,transportState);
    }


    public static IPlatformFluidHandler filteredOf(IPlatformFluidHandler fluidHandler, TransportState transportState ) {
        return ForgeFluidHandler.filtered(fluidHandler,transportState);
    }

    public static IPlatformEnergyStorage getEnergyStorageApi(int Capacity, BlockEntity be) {
        return new ForgeEnergyStorage(Capacity,be);
    }

    public static Component getPlatformEnergyComponent() {
        return new TextComponent("Forge Energy (FE)").withStyle(ChatFormatting.GREEN);
    }

    public static IPlatformEnergyStorage filteredOf(IPlatformEnergyStorage energyStorage, TransportState transportState) {
        return ForgeEnergyStorage.filtered(energyStorage,transportState);
    }

    public static long getBucketAmount() {
        return 1000;
    }
}
