package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
//?} else if forge {
/*import net.minecraftforge.items.IItemHandlerModifiable;
*///?} else if neoforge {
/*import net.neoforged.neoforge.items.IItemHandlerModifiable;
*///?}
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface IPlatformItemHandler extends Container, ITagSerializable<CompoundTag>, IPlatformHandler /*? if forge || neoforge {*//*, IItemHandlerModifiable*//*?} else if fabric {*/,IPlatformHandlerApi<Storage<ItemVariant>>/*?}*/ {

    @Override
    default ItemStack removeItemNoUpdate(int i){
        return extractItem(i,getItem(i).getCount(),false);
    }

    @Override
    default void setChanged() {

    }

    default ItemStack removeItem(int i, int j){
        return extractItem(i,j,false);
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }

    /**
     * Returns the ItemStack in a given slot.
     *
     * The result's stack size may be greater than the itemstack's max size.
     *
     * If the result is empty, then the slot is empty.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
     * altering an inventory's contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * </p>
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
     * </p>
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
     **/
    @NotNull
    ItemStack getItem(int slot);

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     *         The returned ItemStack can be safely modified after.
     **/
    @NotNull
    ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

    /**
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     *         The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @NotNull
    ItemStack extractItem(int slot, int amount, boolean simulate);


    //? if forge || neoforge {
    /*@Override
    default int getSlots() {
        return getContainerSize();
    }

    @Override
    default @NotNull ItemStack getStackInSlot(int i) {
        return getItem(i);
    }

    @Override
    default void setStackInSlot(int i, @NotNull ItemStack arg) {
        setItem(i,arg);
    }

    @Override
    default int getSlotLimit(int i) {
        return getMaxStackSize();
    }

    @Override
    default boolean isItemValid(int i, @NotNull ItemStack arg) {
        return canPlaceItem(i,arg);
    }
    *///?}

}