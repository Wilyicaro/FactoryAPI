package wily.factoryapi.forge.base;

import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidMultiUtil {
    public static IFluidHandler.FluidAction fluidActionOf(boolean simulate){
        return(simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }
}
