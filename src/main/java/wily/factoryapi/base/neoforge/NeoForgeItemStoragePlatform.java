//? if neoforge && >=1.21.9 {
/*package wily.factoryapi.base.neoforge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;


public interface NeoForgeItemStoragePlatform extends IPlatformItemHandler, IPlatformHandlerApi<ResourceHandler<ItemResource>> {

    @Override
    default int getContainerSize() {
        return getHandler().size();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++)
            if (!getHandler().getResource(i).isEmpty()) return false;
        return true;
    }

    @Override
    default @NotNull ItemStack getItem(int slot) {
        return ItemUtil.getStack(getHandler(), slot);
    }

    @Override
    default void setItem(int i, ItemStack arg) {
        extractItem(i, getHandler().getAmountAsInt(i), false);
        insertItem(i, arg, false);
    }

    @Override
    default void setChanged() {

    }

    @Override
    default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        try (Transaction transaction = Transaction.open(null))
        {
            int amount = getHandler().insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
            if (!simulate) transaction.commit();
            return amount == 0 ? ItemStack.EMPTY : stack.copyWithCount(amount);
        }
    }

    @Override
    default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        try (Transaction transaction = Transaction.open(null))
        {
            int extracted = getHandler().extract(slot, getHandler().getResource(slot), getHandler().getAmountAsInt(slot), transaction);
            if (!simulate) transaction.commit();
            return extracted == 0 ? ItemStack.EMPTY : getHandler().getResource(slot).toStack(extracted);
        }
    }

    @Override
    default boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return getHandler().isValid(slot, ItemResource.of(stack));
    }

    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }


    @Override
    default void clearContent() {
        for (int i = 0; i < getContainerSize(); i++)
            extractItem(i, getHandler().getAmountAsInt(i), false);
    }

    @Override
    default CompoundTag serializeTag() {
        return new CompoundTag();
    }

    @Override
    default void deserializeTag(CompoundTag tag) {
    }
}
*///?}