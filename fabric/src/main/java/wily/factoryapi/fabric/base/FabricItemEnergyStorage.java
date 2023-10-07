package wily.factoryapi.fabric.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.*;

import static wily.factoryapi.base.SimpleItemCraftyStorage.BLOCK_ENTITY_TAG;

public class FabricItemEnergyStorage implements FabricEnergyStoragePlatform, EnergyStorage {
    private static final String KEY = "energy";
    private int capacity;
    private final int maxOutput;

    private final int maxInput;

    ItemStack container;
    final boolean isBlockItem;

    public final EnergyTier tier;

    public FabricItemEnergyStorage(ItemStack stack, int initialEnergy, int capacity, int maxOutput, int maxInput, EnergyTier tier, boolean isBlockItem){
        this.isBlockItem = isBlockItem;
        this.capacity = capacity;
        this.container = stack;
        CompoundTag tag = stack.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        if (tag.getCompound("EnergyStorage").isEmpty()){
            CompoundTag storage = new CompoundTag();
            storage.putInt(KEY,initialEnergy);
            tag.put("EnergyStorage", storage);
            if (isBlockItem) stack.getOrCreateTag().put(BLOCK_ENTITY_TAG, tag);
        }
        this.tier = tier;
        this.maxOutput = maxOutput;
        this.maxInput = maxInput;
    }
    @Override
    public EnergyHandler getHandler() {
        return Energy.of(this);
    }
    private CompoundTag getEnergyCompound(){
        CompoundTag tag = container.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        return tag.getCompound("EnergyStorage");
    }

    @Override
    public double getStored(EnergySide face) {
        return getEnergyCompound().getInt(KEY) * container.getCount();
    }

    @Override
    public double getMaxInput(EnergySide side) {
        return maxInput;
    }
    @Override
    public double getMaxOutput(EnergySide side) {
        return maxOutput;
    }
    @Override
    public void setStored(double amount) {
        getEnergyCompound().putInt(KEY, (int) amount);
    }

    @Override
    public double getMaxStoredPower() {
        return capacity * container.getCount();
    }

    @Override
    public EnergyTier getTier() {
        return tier;
    }
}
