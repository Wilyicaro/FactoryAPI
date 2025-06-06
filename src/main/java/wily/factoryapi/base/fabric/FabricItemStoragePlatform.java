//? if fabric {
package wily.factoryapi.base.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface FabricItemStoragePlatform extends IPlatformItemHandler {
    @Override
    default int getContainerSize() {
        int s = 0;
        for (StorageView<ItemVariant> view : getHandler())
            s++;
        return s;
    }

    @Override
    default boolean isEmpty() {
        for (StorageView<ItemVariant> view : getHandler())
            if (!view.isResourceBlank()) return false;
        return true;
    }

    @Override
    default @NotNull ItemStack getItem(int slot) {
        if (getHandler() instanceof SlottedStorage<ItemVariant> slots)
            return slots.getSlot(slot).getResource().toStack(slots.getSlotCount());
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItemNoUpdate(int i) {
        if (getHandler() instanceof SlottedStorage<ItemVariant> slots){
            SingleSlotStorage<ItemVariant> slot = slots.getSlot(i);
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
        if (getHandler() instanceof SlottedStorage<ItemVariant> slots){
            SingleSlotStorage<ItemVariant> slot = slots.getSlot(i);
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
        ItemStack inserted = stack;
        if (!stack.isEmpty() && getHandler() instanceof SlottedStorage<ItemVariant> slots){
            SingleSlotStorage<ItemVariant> slot = slots.getSlot(i);
            try(Transaction transaction = Transaction.openOuter()){
                try (Transaction nested = transaction.openNested()){
                    int count = (int) slot.insert(ItemVariant.of(stack),stack.getCount(),nested);
                    inserted = slot.getResource().toStack(stack.getCount() - count);
                    if (!simulate) nested.commit();
                }
                transaction.commit();
            }
        }
        return inserted;
    }

    @Override
    default @NotNull ItemStack extractItem(int i, int amount, boolean simulate) {
        ItemStack extracted = ItemStack.EMPTY;
        if (amount > 0 && getHandler() instanceof SlottedStorage<ItemVariant> slots){
            SingleSlotStorage<ItemVariant> slot = slots.getSlot(i);
            ItemVariant slotVariant = slot.getResource();
            if (!slotVariant.isBlank())
                try(Transaction transaction = Transaction.openOuter()){
                    try (Transaction nested = transaction.openNested()){
                        int count = (int) slot.extract(slot.getResource(),amount,nested);
                        extracted = slotVariant.toStack(count);
                        if (!simulate) nested.commit();
                    }
                    transaction.commit();
                }
        }
        return extracted;
    }
    @Override
    default void clearContent() {
        for (StorageView<ItemVariant> view : getHandler())
            if (!view.isResourceBlank()){
                try (Transaction transaction = Transaction.openOuter()){
                    view.extract(view.getResource(),view.getAmount(),transaction);
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
//?}
