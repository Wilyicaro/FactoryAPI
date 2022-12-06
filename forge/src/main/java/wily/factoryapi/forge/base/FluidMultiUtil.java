package wily.factoryapi.forge.base;

import dev.architectury.fluid.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidMultiUtil {
    public static FluidStack to(net.minecraftforge.fluids.FluidStack fluid){
       return  FluidStack.create(fluid.getFluid(), fluid.getAmount());

    }
    public static net.minecraftforge.fluids.FluidStack from(FluidStack fluid){
        return  new net.minecraftforge.fluids.FluidStack(fluid.getFluid(), (int) fluid.getAmount());
    }
    public static IFluidHandler.FluidAction FluidActionof(boolean simulate){
        return(simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }
}
