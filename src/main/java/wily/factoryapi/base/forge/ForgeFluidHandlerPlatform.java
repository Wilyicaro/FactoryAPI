//? if forge || neoforge {
/*package wily.factoryapi.base.forge;


import net.minecraft.nbt.CompoundTag;
//? if forge {
/^import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
^///?} elif neoforge {
/^import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
^///?}
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.FluidInstance;

public interface ForgeFluidHandlerPlatform extends IPlatformFluidHandler, IPlatformHandlerApi<IFluidHandler> {

    @Override
    default SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }

    @Override
    default @NotNull FluidInstance getFluidInstance() {
        return FactoryAPIPlatform.fluidStackToInstance(getHandler().getFluidInTank(0));
    }

    @Override
    default int getMaxFluid() {
        return getHandler().getTankCapacity(0);
    }

    @Override
    default boolean isFluidValid(@NotNull FluidInstance instance) {
        return getHandler().isFluidValid(0,new FluidStack(instance.getFluid(),instance.getAmount()));
    }

    @Override
    default int fill(FluidInstance resource, boolean simulate) {
        return getHandler().fill(new FluidStack(resource.getFluid(),resource.getAmount()), FactoryAPIPlatform.fluidActionOf(simulate));
    }

    @Override
    default @NotNull FluidInstance drain(FluidInstance resource, boolean simulate) {
        return FactoryAPIPlatform.fluidStackToInstance(getHandler().drain(new FluidStack(resource.getFluid(),resource.getAmount()), FactoryAPIPlatform.fluidActionOf(simulate)));
    }

    @Override
    default @NotNull FluidInstance drain(int maxDrain, boolean simulate) {
        return drain(getFluidInstance().copyWithAmount(maxDrain),simulate);
    }

    @Override
    default void setFluid(FluidInstance fluidStack) {
        if (!this.getFluidInstance().isEmpty()) drain(getFluidInstance(),false);
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
*///?}