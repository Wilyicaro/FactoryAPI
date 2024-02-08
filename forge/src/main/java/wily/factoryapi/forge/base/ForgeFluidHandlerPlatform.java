package wily.factoryapi.forge.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

public interface ForgeFluidHandlerPlatform extends IPlatformFluidHandler, IPlatformHandlerApi<IFluidHandler> {

    @Override
    default SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }

    @Override
    default @NotNull FluidStack getFluidStack() {
        return FluidStackHooksForge.fromForge(getHandler().getFluidInTank(0));
    }

    @Override
    default long getMaxFluid() {
        return getHandler().getTankCapacity(0);
    }

    @Override
    default boolean isFluidValid(@NotNull FluidStack stack) {
        return getHandler().isFluidValid(0,FluidStackHooksForge.toForge(stack));
    }

    @Override
    default long fill(FluidStack resource, boolean simulate) {
        return getHandler().fill(FluidStackHooksForge.toForge(resource), FluidMultiUtil.fluidActionOf(simulate));
    }

    @Override
    default @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        return FluidStackHooksForge.fromForge(getHandler().drain(FluidStackHooksForge.toForge(resource), FluidMultiUtil.fluidActionOf(simulate)));
    }

    @Override
    default @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        return drain(getFluidStack().copyWithAmount(maxDrain),simulate);
    }

    @Override
    default void setFluid(FluidStack fluidStack) {
        if (!this.getFluidStack().isEmpty()) drain(getFluidStack(),false);
        fill(fluidStack,false);
    }

    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }

    @Override
    default CompoundTag serializeTag() {
        return new CompoundTag();
    }

    @Override
    default void deserializeTag(CompoundTag nbt) {

    }

}
