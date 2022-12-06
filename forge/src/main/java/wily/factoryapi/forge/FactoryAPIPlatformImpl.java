package wily.factoryapi.forge;

import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.loading.FMLPaths;
import wily.factoryapi.FactoryAPIPlatform;
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

    public static IPlatformFluidHandler<?> getFluidItemHandlerApi(long Capacity, ItemStack container, Predicate<FluidStack> validator, TransportState transportState) {

        return new ForgeItemFluidHandler(Capacity,container,validator, transportState);
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
        return Component.literal("Forge Energy (FE)").withStyle(ChatFormatting.GREEN);
    }
}
