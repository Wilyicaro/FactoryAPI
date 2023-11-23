package wily.factoryapi.fabric.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

public interface FabricFluidStoragePlatform extends IPlatformFluidHandler<Storage<FluidVariant>> {



    @Override
    default @NotNull FluidStack getFluidStack() {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                return FluidStackHooksFabric.fromFabric(view);
        }
        return FluidStack.empty();
    }
    @Override
    default void deserializeTag(CompoundTag tag) {
        setFluid(FluidStackHooksFabric.fromFabric(FluidVariant.fromNbt(tag.getCompound("fluidVariant")),tag.getLong("amount")));
        if (tag.contains("capacity")) setCapacity(tag.getLong("capacity"));
    }
    @Override
    default CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction)) {
                tag.put("fluidVariant", view.getResource().toNbt());
                tag.putLong("amount", view.getAmount());
                tag.putLong("capacity", view.getCapacity());
            }
            transaction.commit();
        }
        return tag;
    }

    @Override
    default long getMaxFluid() {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                return view.getCapacity();
        }
        return 0L;
    }

    @Override
    default boolean isFluidValid(@NotNull FluidStack stack) {
        return true;
    }



    @Override
    default long fill(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) getHandler().insert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            }else i = (int) getHandler().simulateInsert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            transaction.commit();
            return i;
        }
    }

    @Override
    default @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) getHandler().extract(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            }else i = (int) getHandler().simulateExtract(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            transaction.commit();
            return FluidStack.create(resource.getFluid(), i);
        }
    }

    @Override
    default @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                return drain(FluidStackHooksFabric.fromFabric(view.getResource(), maxDrain), simulate);
            transaction.commit();
        }
        return FluidStack.empty();
    }
    @Override
    default void setFluid(FluidStack fluidStack) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                drain((int) view.getAmount(), false);
        }
        fill(fluidStack,false);
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
