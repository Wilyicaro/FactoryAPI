package wily.factoryapi.forge.mixin;

import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.forge.utils.FluidStackUtil;

@Mixin(IFluidHandler.class)
public interface FluidHandler extends IPlatformFluidHandler<IFluidHandler> {

    @Override
    default SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }

    @Override
    default @NotNull FluidStack getFluidStack() {
        return FluidStackUtil.fromForge(getHandler().getFluidInTank(0));
    }

    @Override
    default long getMaxFluid() {
        return getHandler().getTankCapacity(0);
    }

    @Override
    default boolean isFluidValid(@NotNull FluidStack stack) {
        return getHandler().isFluidValid(0,FluidStackUtil.toForge(stack));
    }

    @Override
    default long fill(FluidStack resource, boolean simulate) {
        return getHandler().fill(FluidStackUtil.toForge(resource), FluidStackUtil.FluidActionof(simulate));
    }

    @Override
    default @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        return FluidStackUtil.fromForge(getHandler().drain(FluidStackUtil.toForge(resource), FluidStackUtil.FluidActionof(simulate)));
    }

    @Override
    default @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        return drain(FluidStack.create(getFluidStack().getFluid(), Fraction.ofWhole(maxDrain)),simulate);
    }

    @Override
    default void setFluid(FluidStack fluidStack) {
        if (!this.getFluidStack().isEmpty()) drain(getFluidStack(),false);
        fill(fluidStack,false);
    }

    @Override
    default IFluidHandler getHandler() {
        return (IFluidHandler) this;
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
