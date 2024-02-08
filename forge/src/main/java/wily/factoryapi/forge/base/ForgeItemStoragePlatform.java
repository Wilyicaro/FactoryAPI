package wily.factoryapi.forge.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;

import java.util.function.BiPredicate;
import java.util.function.Predicate;


public interface ForgeItemStoragePlatform extends IPlatformItemHandler, IPlatformHandlerApi<IItemHandler> {


    @Override
    default int getContainerSize() {
        return getHandler().getSlots();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < getHandler().getSlots(); i++)
            if (!getHandler().getStackInSlot(i).isEmpty()) return false;
        return true;
    }
    default ArbitrarySupplier<IItemHandlerModifiable> getModifiableHandler(){
        return ()-> getHandler() instanceof IItemHandlerModifiable h ?h : null;
    }
    @Override
    default @NotNull ItemStack getItem(int slot) {
        return getHandler().getStackInSlot(slot);
    }

    @Override
    default void setItem(int i, ItemStack arg) {
        getModifiableHandler().ifPresent(m->m.setStackInSlot(i,arg));
    }

    @Override
    default void setChanged() {

    }

    @Override
    default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return getHandler().insertItem(slot,stack,simulate);
    }

    @Override
    default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return getHandler().extractItem(slot,amount,simulate);
    }


    @Override
    default boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return getHandler().isItemValid(slot,stack);
    }


    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }


    @Override
    default void clearContent() {
        getModifiableHandler().ifPresent(m->{
            for (int i = 0; i < getHandler().getSlots(); i++)
                m.setStackInSlot(i,ItemStack.EMPTY);
        });
    }

    @Override
    default CompoundTag serializeTag() {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < getContainerSize(); ++i) {
            if (!getItem(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getItem(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        return nbt;
    }

    @Override
    default void deserializeTag(CompoundTag tag) {
        ListTag tagList = tag.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSize()) {
                setItem(slot, ItemStack.of(itemTags));
            }
        }
    }
}
