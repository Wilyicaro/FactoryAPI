package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.util.FactoryItemUtil;

import java.util.function.BiPredicate;

public class FactoryItemHandler extends SimpleContainer implements IPlatformItemHandler{
    protected BlockEntity be;
    protected TransportState transportState;

    public FactoryItemHandler(int inventorySize, BlockEntity be, TransportState transportState){
        super(inventorySize);
        this.be = be;
        this.transportState = transportState;
    }

    @Override
    public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
        return getTransport().canExtract();
    }
    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return getTransport().canInsert() && (!(be instanceof IFactoryExpandedStorage storage) || storage.getSlots(null).get(slot).mayPlace(stack));
    }

    public FactoryItemHandler(IPlatformItemHandler  handler, TransportState transportState){
        this(handler.getContainerSize(), handler instanceof FactoryItemHandler h ? h.be : null,transportState);

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
                } else if (!FactoryItemUtil.equalItems(stack, stackInSlot)) {
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
        if (amount == 0 || !canTakeItem(this,slot,getItem(slot))) {
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
    public void setChanged() {
        super.setChanged();
        if (be != null)
            be.setChanged();
    }
    @Override
    public CompoundTag serializeTag() {
        //? if <1.20.5 {
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
        //?} else
        /*return ContainerHelper.saveAllItems(new CompoundTag(),getItems(), FactoryAPI.getRegistryAccess());*/
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return be == null || Container.stillValidBlockEntity(be, player);
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        //? if <1.20.5 {
        ListTag tagList = tag.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < getContainerSize()) {
                setItem(slot, ItemStack.of(itemTags));
            }
        }
        //?} else
        /*ContainerHelper.loadAllItems(tag,getItems(), FactoryAPI.getRegistryAccess());*/
    }

    @Override
    public ItemStack addItem(ItemStack itemStack) {
        return super.addItem(itemStack);
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public boolean isRemoved() {
        return be != null && be.isRemoved();
    }

    public static class SidedWrapper extends FactoryItemHandler implements IModifiableTransportHandler{

        private final IPlatformItemHandler  platformItemHandler;
        public int[] slots = new int[0];

        public SidedWrapper(IPlatformItemHandler platformItemHandler) {
            super(platformItemHandler, platformItemHandler.getTransport());
            this.platformItemHandler = platformItemHandler;
        }
        @Override
        public boolean canPlaceItem(int i, @NotNull ItemStack arg) {
            return platformItemHandler.canPlaceItem(i,arg) && ArrayUtils.contains(slots,i);
        }

        @Override
        public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
            return platformItemHandler.canTakeItem(container, i, itemStack) && ArrayUtils.contains(slots,i);
        }

        @Override
        public ItemStack getItem(int i) {
            return platformItemHandler.getItem(i);
        }

        @Override
        public ItemStack removeItem(int i, int j) {
            return platformItemHandler.removeItem(i,j);
        }

        @Override
        public ItemStack removeItemNoUpdate(int i) {
            return platformItemHandler.removeItemNoUpdate(i);
        }

        @Override
        public void setItem(int i, ItemStack arg) {
            platformItemHandler.setItem(i,arg);
        }

        @Override
        public void setTransport(TransportState state) {
            transportState = state;
        }
    }
}
