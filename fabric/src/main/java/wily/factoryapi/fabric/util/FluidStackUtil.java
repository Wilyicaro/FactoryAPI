package wily.factoryapi.fabric.util;

import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

public class FluidStackUtil {
    public static FluidVariant toFabric(FluidStack fluidStack) {
        return FluidVariant.of(fluidStack.getFluid(),fluidStack.getTag());
    }

    public static FluidStack fromFabric(FluidVariant variant, long amount) {
        return FluidStack.create(variant.getFluid(), Fraction.ofWhole(amount));
    }
    public static FluidStack fromFabric(StorageView<FluidVariant> view) {
        return FluidStack.create(view.getResource().getFluid(), Fraction.ofWhole(view.getAmount()));
    }
}
