//? if forge || (neoforge && <1.21.9) {
/*package wily.factoryapi.base.forge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
//? if forge {
/^import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
^///?} elif neoforge {
/^import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
^///?}
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;


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
        return new CompoundTag();
    }

    @Override
    default void deserializeTag(CompoundTag tag) {
    }
}
*///?}