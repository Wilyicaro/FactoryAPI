package wily.factoryapi.fabric.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

import java.util.function.Predicate;

public interface FabricFluidStoragePlatform extends IPlatformFluidHandler<Storage<FluidVariant>> {



    @Override
    default @NotNull FluidStack getFluidStack() {
        for (StorageView<FluidVariant> view : getHandler())
            return FluidStackHooksFabric.fromFabric(view);
        return FluidStack.empty();
    }
    @Override
    default void deserializeTag(CompoundTag tag) {
        setFluid(FluidStackHooksFabric.fromFabric(FluidVariant.fromNbt(tag.getCompound("fluidVariant")),tag.getLong("amount")));
    }
    @Override
    default CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        for (StorageView<FluidVariant> view : getHandler()) {
            tag.put("fluidVariant", view.getResource().toNbt());
            tag.putLong("amount", view.getAmount());
        }
        return tag;
    }

    @Override
    default long getMaxFluid() {
        for (StorageView<FluidVariant> view : getHandler())
            return view.getCapacity();
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
        for (StorageView<FluidVariant> view : getHandler())
            return drain(FluidStackHooksFabric.fromFabric(view.getResource(),maxDrain), simulate);
        return FluidStack.empty();
    }
    @Override
    default void setFluid(FluidStack fluidStack) {
        for (StorageView<FluidVariant> view : getHandler())
            drain((int) view.getAmount(), false);
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
