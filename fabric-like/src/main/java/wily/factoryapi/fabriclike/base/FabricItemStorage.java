package wily.factoryapi.fabriclike.base;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.fabriclike.util.ItemStackHelper;

import java.util.function.BiPredicate;


public class FabricItemStorage extends SimpleContainer implements IPlatformItemHandler {



    private BlockEntity be;
    protected TransportState transportState;

    protected BiPredicate<Integer,ItemStack> extractableSlots = (i,stack)-> true;
    protected BiPredicate<Integer,ItemStack> insertableSlots = (i,stack)-> true;
    public FabricItemStorage(int inventorySize, @Nullable BlockEntity be,TransportState transportState){
        super(inventorySize);
        this.be = be;
        this.transportState = transportState;
    }


    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return extractableSlots.test(i,itemStack);
    }
    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return insertableSlots.test(i,itemStack);
    }
    public static FabricItemStorage filtered(IPlatformItemHandler itemHandler, Direction d, int[] slots, TransportState transportState){
        FabricItemStorage newItemHandler =  new  FabricItemStorage(itemHandler.getContainerSize(),(( FabricItemStorage)itemHandler).be,transportState){

            @Override
            public ItemStack getItem(int i) {
                return itemHandler.getItem(i);
            }

            @Override
            public void setItem(int i, ItemStack itemStack) {
                itemHandler.setItem(i,itemStack);
            }

            @Override
            public ItemStack removeItemNoUpdate(int i) {
                return itemHandler.removeItemNoUpdate(i);
            }

            @Override
            public ItemStack removeItemType(Item item, int i) {
                return ((FabricItemStorage) itemHandler).removeItemType(item,i);
            }

            @Override
            public boolean canPlaceItem(int i, ItemStack itemStack) {
                return itemHandler.canPlaceItem(i,itemStack);
            }

            @Override
            public ItemStack addItem(ItemStack itemStack) {
                return ((FabricItemStorage) itemHandler).addItem(itemStack);
            }

            @Override
            public int[] getSlotsForFace(Direction direction) {
                return slots;
            }

            @Override
            public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
                return itemHandler.canPlaceItemThroughFace(i,itemStack,direction) && ArrayUtils.contains(slots,i) && direction == d && getTransport().canInsert();
            }

            @Override
            public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
                return itemHandler.canTakeItemThroughFace(i,itemStack,direction) && ArrayUtils.contains(slots,i) && direction == d && getTransport().canExtract();
            }
        };
        newItemHandler.inventoryStorage = InventoryStorage.of(newItemHandler, d);
        return newItemHandler;
    }
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = this.getItem(slot);
            int m;
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), this.getMaxStackSize())) {
                    return stack;
                } else if (!ItemStackHelper.canItemStacksStack(stack, stackInSlot)) {
                    return stack;
                } else if (!this.canPlaceItem(slot, stack)) {
                    return stack;
                } else {
                    m = Math.min(stack.getMaxStackSize(), this.getMaxStackSize()) - stackInSlot.getCount();
                    ItemStack copy;
                    if (stack.getCount() <= m) {
                        if (!simulate) {
                            copy = stack.copy();
                            copy.grow(stackInSlot.getCount());
                            this.setItem(slot, copy);
                            this.setChanged();
                        }

                        return ItemStack.EMPTY;
                    } else {
                        stack = stack.copy();
                        if (!simulate) {
                            copy = stack.split(m);
                            copy.grow(stackInSlot.getCount());
                            this.setItem(slot, copy);
                            this.setChanged();
                            return stack;
                        } else {
                            stack.shrink(m);
                            return stack;
                        }
                    }
                }
            } else if (!this.canPlaceItem(slot, stack)) {
                return stack;
            } else {
                m = Math.min(stack.getMaxStackSize(), this.getMaxStackSize());
                if (m < stack.getCount()) {
                    stack = stack.copy();
                    if (!simulate) {
                        this.setItem(slot, stack.split(m));
                        this.setChanged();
                        return stack;
                    } else {
                        stack.shrink(m);
                        return stack;
                    }
                } else {
                    if (!simulate) {
                        this.setItem(slot, stack);
                        this.setChanged();
                    }

                    return ItemStack.EMPTY;
                }
            }
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = this.getItem(slot);
            if (stackInSlot.isEmpty()) {
                return ItemStack.EMPTY;
            } else if (simulate) {
                if (stackInSlot.getCount() < amount) {
                    return stackInSlot.copy();
                } else {
                    ItemStack copy = stackInSlot.copy();
                    copy.setCount(amount);
                    return copy;
                }
            } else {
                int m = Math.min(stackInSlot.getCount(), amount);
                ItemStack decrStackSize = this.removeItem(slot, m);
                this.setChanged();
                return decrStackSize;
            }
        }
    }

    @Override
    public void setExtractableSlots(BiPredicate<Integer, ItemStack> extractableSlots) {
        this.extractableSlots = extractableSlots;
    }

    @Override
    public void setInsertableSlots(BiPredicate<Integer, ItemStack> insertableSlots) {
        this.insertableSlots = insertableSlots;
    }


    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        if (be instanceof IFactoryStorage storage) return storage.getSlots(null).get(i).mayPlace(itemStack);
        return true;
    }


    @Override
    public CompoundTag serializeTag() {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < getContainerSize(); ++i) {
            if (!(getItem(i)).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                (getItem(i)).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        return nbt;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        ListTag tagList = tag.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSize()) {
                setItem(slot, ItemStack.of(itemTags));
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (be != null) be.setChanged();
    }
    protected Storage<ItemVariant> inventoryStorage = InventoryStorage.of(this, null);

    @Override
    public Object getHandler() {
        return inventoryStorage;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
