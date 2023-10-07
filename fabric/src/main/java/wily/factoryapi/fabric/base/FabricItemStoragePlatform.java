package wily.factoryapi.fabric.base;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;

import java.util.function.BiPredicate;

public interface FabricItemStoragePlatform extends IPlatformItemHandler<Storage<ItemVariant>> {
    @Override
    default int getContainerSize() {
        int s = 0;
        try(Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : getHandler().iterable(transaction))
                s++;
            transaction.commit();
        }
        return s;
    }

    @Override
    default boolean isEmpty() {
        try(Transaction transaction = Transaction.openOuter()){
        for (StorageView<ItemVariant> view : getHandler().iterable(transaction))
            if (!view.isResourceBlank()) return false;
        transaction.commit();
        }
        return true;
    }

    @Override
    default @NotNull ItemStack getItem(int slot) {
        if (getHandler() instanceof InventoryStorage) {
            return ((InventoryStorage)getHandler()).getSlot(slot).getResource().toStack((int) ((InventoryStorage)getHandler()).getSlot(slot).getAmount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItemNoUpdate(int i) {
        if (getHandler() instanceof InventoryStorage){
            SingleSlotStorage<ItemVariant> slot = ((InventoryStorage)getHandler()).getSlot(i);
            ItemStack stack;
            try(Transaction transaction = Transaction.openOuter()){
                stack =slot.getResource().toStack((int) slot.extract(slot.getResource(),slot.getAmount(), transaction));
                transaction.commit();
            }
            return stack;

        }
        return ItemStack.EMPTY;
    }

    @Override
    default void setItem(int i, ItemStack itemStack) {
        if (getHandler() instanceof InventoryStorage){
            SingleSlotStorage<ItemVariant> slot = ((InventoryStorage)getHandler()).getSlot(i);
            try(Transaction transaction = Transaction.openOuter()){
                slot.extract(slot.getResource(),slot.getAmount(), transaction);
                slot.insert(ItemVariant.of(itemStack),itemStack.getCount(),transaction);
                transaction.commit();
            }
        }
    }

    @Override
    default void setChanged() {

    }

    @Override
    default @NotNull ItemStack insertItem(int i, @NotNull ItemStack stack, boolean simulate) {
        ItemStack inserted = ItemStack.EMPTY;
        if (!stack.isEmpty() && getHandler() instanceof InventoryStorage){
            SingleSlotStorage<ItemVariant> slot = ((InventoryStorage)getHandler()).getSlot(i);
            ItemVariant slotVariant = slot.getResource();
            if (slotVariant.matches(stack))
                try(Transaction transaction = Transaction.openOuter()){
                    int count = (int) slot.insert(ItemVariant.of(stack),stack.getCount(),transaction);
                    transaction.commit();
                    inserted = slot.getResource().toStack(count);
                }
        }
        return inserted;
    }

    @Override
    default @NotNull ItemStack extractItem(int i, int amount, boolean simulate) {
        ItemStack extracted = ItemStack.EMPTY;
        if (amount > 0 && getHandler() instanceof InventoryStorage){
            SingleSlotStorage<ItemVariant> slot = ((InventoryStorage)getHandler()).getSlot(i);
            ItemVariant slotVariant = slot.getResource();
            if (!slotVariant.isBlank())
                try(Transaction transaction = Transaction.openOuter()){
                    int count = (int) slot.extract(slot.getResource(),amount,transaction);
                    transaction.commit();
                    extracted = slotVariant.toStack(count);
                }
        }
        return extracted;
    }

    @Override
    default void setExtractableSlots(BiPredicate<Integer, ItemStack> extractableSlots) {

    }

    @Override
    default void setInsertableSlots(BiPredicate<Integer, ItemStack> insertableSlots) {

    }

    @Override
    default void clearContent() {
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : getHandler().iterable(transaction))
                if (!view.isResourceBlank()) {
                    try (Transaction nested = Transaction.openOuter()) {
                        view.extract(view.getResource(), view.getAmount(), nested);
                        nested.commit();
                    }
                    transaction.commit();
                }
        }
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
