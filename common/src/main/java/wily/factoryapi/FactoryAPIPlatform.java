package wily.factoryapi;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.*;

import java.nio.file.Path;
import java.util.function.Predicate;

public class FactoryAPIPlatform {
    @ExpectPlatform
    public static Path getConfigDirectory() {

        throw new AssertionError();
    }

    @ExpectPlatform
    public static Component getPlatformEnergyComponent() {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static IPlatformFluidHandler getFluidHandlerApi(long Capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transport) {

        throw new AssertionError();
    }

    @ExpectPlatform
    public static long getBucketAmount() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static IPlatformItemHandler getItemHandlerApi(int inventorySize, BlockEntity be) {

        throw new AssertionError();
    }

    @ExpectPlatform
    public static IPlatformFluidHandler getFluidItemHandlerApi(ItemStack container, IFluidItem.FluidStorageBuilder builder) {

        throw new AssertionError();
    }

    @ExpectPlatform
    public static IPlatformEnergyStorage getEnergyStorageApi(int Capacity, BlockEntity be) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static IPlatformItemHandler filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static IPlatformFluidHandler filteredOf(IPlatformFluidHandler fluidHandler, TransportState transportState) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static IPlatformEnergyStorage filteredOf(IPlatformEnergyStorage fluidHandler, TransportState transportState) {
        throw new AssertionError();
    }


}
