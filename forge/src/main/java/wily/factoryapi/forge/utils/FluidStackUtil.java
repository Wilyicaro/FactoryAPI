package wily.factoryapi.forge.utils;

import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidStackUtil {

    public static FluidStack fromForge(net.minecraftforge.fluids.FluidStack fluidStack){
        return  FluidStack.create(fluidStack.getFluid(), Fraction.ofWhole(fluidStack.getAmount()));
    }

    public static net.minecraftforge.fluids.FluidStack toForge(FluidStack fluidStack){
        return  new net.minecraftforge.fluids.FluidStack(fluidStack.getFluid(),fluidStack.getAmount().intValue());
    }
    public static IFluidHandler.FluidAction FluidActionof(boolean simulate){
        return(simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }
}
