package wily.factoryapi.forge.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.base.TransportState;

import java.util.List;
import java.util.function.BiPredicate;

public class ForgeItemHandler extends SimpleContainer implements IPlatformItemHandler {
    protected BlockEntity be;

    public IItemHandlerModifiable itemHandler;
    protected TransportState transportState;

    protected BiPredicate<Integer,ItemStack> extractableSlots = (i,stack)-> true;
    protected BiPredicate<Integer,ItemStack> insertableSlots = (i,stack)-> true;

    public ForgeItemHandler(int inventorySize, BlockEntity be, TransportState transportState){
        super(inventorySize);
        this.be = be;
        itemHandler =  new InvWrapper(this);
        this.transportState = transportState;

    }

    public void setExtractableSlots(BiPredicate<Integer, ItemStack> extractableSlots) {
        this.extractableSlots = extractableSlots;
    }
    public void setInsertableSlots(BiPredicate<Integer, ItemStack> insertableSlots) {
        this.insertableSlots = insertableSlots;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return extractableSlots.test(i,itemStack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return insertableSlots.test(i,itemStack);
    }

    public ForgeItemHandler(ForgeItemHandler handler, TransportState transportState){
        this(handler.getContainerSize(), handler.be,transportState);

    }


    public static ForgeItemHandler filtered(IPlatformItemHandler platformItemHandler, Direction d, int[] slots, TransportState transportState){
        ForgeItemHandler newItemHandler =  new ForgeItemHandler((ForgeItemHandler) platformItemHandler,transportState){
            @Override
            public boolean canPlaceItem(int i, @NotNull ItemStack arg) {

                return platformItemHandler.canPlaceItem(i,arg) && getTransport().canInsert();
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
            public ItemStack removeItemType(Item arg, int i) {
                return  ((ForgeItemHandler) platformItemHandler).removeItemType(arg,i);
            }

            @Override
            public ItemStack addItem(ItemStack arg) {
                return  ((ForgeItemHandler) platformItemHandler).addItem(arg);

            }

            @Override
            public ItemStack removeItemNoUpdate(int i) {
                return platformItemHandler.removeItemNoUpdate(i);
            }

            @Override
            public List<ItemStack> removeAllItems() {
                return ((ForgeItemHandler) platformItemHandler).removeAllItems();
            }

            @Override
            public void setItem(int i, ItemStack arg) {
                platformItemHandler.setItem(i,arg);
            }

            @Override
            public int[] getSlotsForFace(Direction direction) {
                return slots;
            }

            @Override
            public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
                return platformItemHandler.canPlaceItemThroughFace(i,itemStack,direction) && ArrayUtils.contains(slots,i) && direction == d && getTransport().canInsert();
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }

            @Override
            public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
                return platformItemHandler.canTakeItemThroughFace(i,itemStack,direction) && ArrayUtils.contains(slots,i) && direction == d && getTransport().canExtract();
            }
        };
        newItemHandler.itemHandler = new SidedInvWrapper(newItemHandler,d);
        return newItemHandler;
    }



    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {

        return itemHandler.insertItem(slot,stack,simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return itemHandler.extractItem(slot,amount,simulate);
    }


    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (be instanceof IFactoryStorage storage) return storage.getSlots(null).get(slot).mayPlace(stack);
        return true;
    }



    @Override
    public void setChanged() {
        be.setChanged();
    }


    @Override
    public CompoundTag serializeTag() {
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
    public Object getHandler() {
        return itemHandler;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
