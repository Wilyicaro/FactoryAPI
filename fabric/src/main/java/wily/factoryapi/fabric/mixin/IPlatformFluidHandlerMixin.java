package wily.factoryapi.fabric.mixin;

import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.fabric.base.FabricFluidStoragePlatform;
@Mixin(IPlatformFluidHandler.class)
public interface IPlatformFluidHandlerMixin extends SingleSlotStorage<FluidVariant>, IPlatformFluidHandler {

    @Override
    default long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        transaction.addCloseCallback((t,r)->{
            if (r.wasCommitted())
                fill(FluidStackHooksFabric.fromFabric(resource,maxAmount),false);
        });
        return fill(FluidStackHooksFabric.fromFabric(resource,maxAmount),true);
    }

    @Override
    default long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        transaction.addCloseCallback((t,r)->{
            if (r.wasCommitted())
                drain(FluidStackHooksFabric.fromFabric(resource,maxAmount),false);
        });
        return drain(FluidStackHooksFabric.fromFabric(resource,maxAmount),true).getAmount();
    }

    @Override
    default boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    default FluidVariant getResource() {
        return FluidStackHooksFabric.toFabric(getFluidStack());
    }

    @Override
    default long getAmount() {
        return getFluidStack().getAmount();
    }

    @Override
    default long getCapacity() {
        return getMaxFluid();
    }
}
