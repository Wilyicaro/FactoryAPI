package wily.factoryapi.fabric.base;

import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.fabric.util.FluidStackUtil;

public interface FabricFluidStoragePlatform extends IPlatformFluidHandler<Storage<FluidVariant>> {



    @Override
    default @NotNull FluidStack getFluidStack() {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                return FluidStackUtil.fromFabric(view);
            transaction.commit();
        }
        return FluidStack.empty();
    }
    @Override
    default void deserializeTag(CompoundTag tag) {
        setFluid(FluidStackUtil.fromFabric(FluidVariant.fromNbt(tag.getCompound("fluidVariant")),tag.getLong("amount")));
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
            transaction.commit();
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
                i = (int) getHandler().insert(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            }else i = (int) getHandler().simulateInsert(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            transaction.commit();
            return i;
        }
    }

    @Override
    default @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) getHandler().extract(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            }else i = (int) getHandler().simulateExtract(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            transaction.commit();
            return FluidStack.create(resource.getFluid(), Fraction.ofWhole(i));
        }
    }

    @Override
    default @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()){
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                return drain(FluidStackUtil.fromFabric(view.getResource(),maxDrain), simulate);
            transaction.commit();
        }
        return FluidStack.empty();
    }
    @Override
    default void setFluid(FluidStack fluidStack) {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<FluidVariant> view : getHandler().iterable(transaction))
                drain((int) view.getAmount(), false);
            transaction.commit();
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
