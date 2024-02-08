package wily.factoryapi.forge.mixin;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformFluidHandler;
@Mixin(IPlatformFluidHandler.class)
public interface IPlatformFluidHandlerMixin extends IPlatformFluidHandler,IFluidHandler, IFluidTank {

    @Override
    default @NotNull FluidStack getFluid() {
        return FluidStackHooksForge.toForge(getFluidStack());
    }

    @Override
    default int getFluidAmount() {
        return getFluid().getAmount();
    }

    @Override
    default int getCapacity() {
        return (int) getMaxFluid();
    }

    @Override
    default boolean isFluidValid(FluidStack fluidStack) {
        return isFluidValid(FluidStackHooksForge.fromForge(fluidStack));
    }

    @Override
    default int getTanks() {
        return 1;
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int i) {
        return getFluid();
    }

    @Override
    default int getTankCapacity(int i) {
        return getCapacity();
    }

    @Override
    default boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
        return isFluidValid(fluidStack);
    }

    @Override
    default int fill(FluidStack fluidStack, FluidAction fluidAction) {
        return (int)fill(FluidStackHooksForge.fromForge(fluidStack), fluidAction.simulate());
    }

    @Override
    default @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        return FluidStackHooksForge.toForge(drain(FluidStackHooksForge.fromForge(fluidStack),fluidAction.simulate()));
    }

    @Override
    default @NotNull FluidStack drain(int i, FluidAction fluidAction) {
        return FluidStackHooksForge.toForge(drain(i,fluidAction.simulate()));
    }
}
