package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPI;

import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class SimpleItemCraftyStorage implements ICraftyEnergyStorage {
    private static final String KEY = "energy";

    private int capacity;
    private final int maxOutput;

    private final int maxInput;

    ItemStack container;
    TransportState transportState;
    final boolean isBlockItem;

    public final FactoryCapacityTiers supportableTier;

    public FactoryCapacityTiers storedTier = FactoryCapacityTiers.BASIC;
    public SimpleItemCraftyStorage(ItemStack stack, int initialEnergy, int capacity, int maxOutput, int maxInput, TransportState transportState, FactoryCapacityTiers supportableTier, boolean isBlockItem){
        this.supportableTier = supportableTier;
        this.isBlockItem = isBlockItem;
        this.capacity = capacity;
        this.container = stack;
        CompoundTag tag = stack.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        if (tag.getCompound("CYEnergy").isEmpty()){
            CompoundTag storage = new CompoundTag();
            storage.putInt(KEY,initialEnergy);
            storage.putInt("supportedTier",supportableTier.ordinal());
            storage.putInt("tier",storedTier.ordinal());
            tag.put("CYEnergy", storage);
            if (isBlockItem) stack.getOrCreateTag().put(BLOCK_ENTITY_TAG, tag);
        }
        this.transportState = transportState;
        this.maxOutput = maxOutput;
        this.maxInput = maxInput;
    }
    public SimpleItemCraftyStorage(ItemStack stack, int capacity, TransportState transportState, FactoryCapacityTiers supportableTier){
        this(stack,0,capacity,capacity,capacity, transportState,supportableTier,stack.getItem() instanceof BlockItem);
    }
    private CompoundTag getEnergyCompound(){
        CompoundTag tag = container.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        return tag.getCompound("CYEnergy");
    }
    @Override
    public CraftyTransaction receiveEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energyReceived = Math.min(getMaxReceive(), transaction.energy);
        int energy = getEnergyStored();
        if (!simulate) {
            if ( supportableTier.supportTier(transaction.tier)) setStoredTier(transaction.tier);
            else {
                return CraftyTransaction.EMPTY;
            }
            energy += energyReceived;
            setEnergyStored(energy);

        }

        return new CraftyTransaction(energyReceived, transaction.tier);
    }

    public CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energy = getEnergyStored();
        int energyExtracted = Math.min(energy, Math.min(getMaxConsume(), transaction.energy));

        if (!simulate) {
            if (!storedTier.supportTier(transaction.tier)) energyExtracted = storedTier.convertEnergyTo(energyExtracted,transaction.tier);
            energy -= energyExtracted;
            setEnergyStored(energy);
        }

        return new CraftyTransaction(energyExtracted, transaction.tier);
    }

    @Override
    public FactoryCapacityTiers getSupportedTier() {
        return FactoryCapacityTiers.values()[getEnergyCompound().getInt("supportedTier")];
    }

    @Override
    public FactoryCapacityTiers getStoredTier() {
        return FactoryCapacityTiers.values()[getEnergyCompound().getInt("tier")];
    }

    @Override
    public int getEnergyStored() {
        return getEnergyCompound().getInt(KEY) * container.getCount();
    }

    public void setEnergyStored(int energy) {
        CompoundTag tag = getEnergyCompound();
        tag.putInt(KEY,energy);
    }

    public void setStoredTier(FactoryCapacityTiers tier){
        CompoundTag tag = getEnergyCompound();
        tag.putInt("tier",tier.ordinal());
    }

    @Override
    public void setSupportedTier(FactoryCapacityTiers tier) {
        CompoundTag tag = getEnergyCompound();
        tag.putInt("supportedTier",tier.ordinal());
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity * container.getCount();
    }


    @Override
    public CompoundTag serializeTag() {
        return getEnergyCompound();
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        this.container.setTag(nbt);
    }


    public int getMaxConsume(){
        return Math.min(getEnergyStored(),maxOutput);
    }

    @Override
    public int getMaxReceive() {
        return Math.min(getSpace(),maxInput);
    }

    @Override
    public ICraftyEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
