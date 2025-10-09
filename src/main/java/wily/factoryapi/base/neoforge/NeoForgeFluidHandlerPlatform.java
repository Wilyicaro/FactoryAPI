//? if neoforge && >=1.21.9 {
/*package wily.factoryapi.base.neoforge;


import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.FluidInstance;

public interface NeoForgeFluidHandlerPlatform extends IPlatformFluidHandler, IPlatformHandlerApi<ResourceHandler<FluidResource>> {

    @Override
    default SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }

    @Override
    default @NotNull FluidInstance getFluidInstance() {
        return FactoryAPIPlatform.fluidStackToInstance(getHandler().getResource(0).toStack(getHandler().getAmountAsInt(0)));
    }

    @Override
    default int getMaxFluid() {
        return getHandler().getCapacityAsInt(0, getHandler().getResource(0));
    }

    @Override
    default boolean isFluidValid(@NotNull FluidInstance instance) {
        return getHandler().isValid(0, FluidResource.of(instance.toStack()));
    }

    @Override
    default int fill(FluidInstance resource, boolean simulate) {
        try (Transaction transaction = Transaction.open(null))
        {
            int amount = getHandler().insert(FluidResource.of(resource.toStack()), resource.getAmount(), transaction);
            if (!simulate) transaction.commit();
            return amount;
        }
    }

    @Override
    default @NotNull FluidInstance drain(FluidInstance resource, boolean simulate) {
        try (Transaction transaction = Transaction.open(null))
        {
            int amount = getHandler().extract(FluidResource.of(resource.toStack()), resource.getAmount(), transaction);
            if (!simulate) transaction.commit();
            return resource.copyWithAmount(amount);
        }
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