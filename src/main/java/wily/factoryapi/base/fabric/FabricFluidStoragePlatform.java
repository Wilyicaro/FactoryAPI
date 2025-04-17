//? if fabric {
package wily.factoryapi.base.fabric;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.FluidInstance;

public interface FabricFluidStoragePlatform extends IPlatformFluidHandler, IPlatformHandlerApi<Storage<FluidVariant>> {

    @Override
    default @NotNull FluidInstance getFluidInstance() {
        for (StorageView<FluidVariant> view : getHandler())
            return FluidInstance.create(view.getResource().getFluid(),view.getAmount());
        return FluidInstance.empty();
    }

    @Override
    default int getMaxFluid() {
        int maxFluid = 0;
        for (StorageView<FluidVariant> view : getHandler())
            maxFluid +=  FluidInstance.getMilliBucketsFluidAmount(view.getCapacity());
        return maxFluid;
    }

    @Override
    default long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return getHandler().insert(resource, maxAmount, transaction);
    }

    @Override
    default long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return getHandler().extract(resource, maxAmount, transaction);
    }

    @Override
    default boolean isFluidValid(@NotNull FluidInstance stack) {
        return true;
    }

    @Override
    default int fill(FluidInstance resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            try (Transaction nested = transaction.openNested()){
                i = insert(FluidVariant.of(resource.getFluid()), FluidInstance.getPlatformFluidAmount(resource.getAmount()), nested);
                if (!simulate) nested.commit();
            }
            transaction.commit();
            return FluidInstance.getMilliBucketsFluidAmount(i);
        }
    }

    @Override
    default @NotNull FluidInstance drain(FluidInstance resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            try (Transaction nested = transaction.openNested()) {
                i = extract(FluidVariant.of(resource.getFluid()), resource.getPlatformAmount(), nested);
                if (!simulate) nested.commit();
            }
            transaction.commit();
            return FluidInstance.create(resource.getFluid(), i);
        }
    }

    @Override
    default @NotNull FluidInstance drain(int maxDrain, boolean simulate) {
        for (StorageView<FluidVariant> view : getHandler())
            return drain(FluidInstance.create(view.getResource().getFluid(),maxDrain), simulate);
        return FluidInstance.empty();
    }
    @Override
    default void setFluid(FluidInstance fluidInstance) {
        drain(getFluidInstance(), false);
        fill(fluidInstance,false);
    }

    @Override
    default SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }
    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }
}
//?}