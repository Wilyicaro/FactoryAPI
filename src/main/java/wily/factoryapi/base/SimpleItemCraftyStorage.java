package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.init.FactoryRegistries;

//? if <1.20.5
//import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class SimpleItemCraftyStorage implements ICraftyEnergyStorage {
    private static final String KEY = "energy";

    private int capacity;
    private final int maxOutput;

    private final int maxInput;

    ItemStack container;
    TransportState transportState;
    final boolean isBlockItem;

    public final FactoryCapacityTier supportableTier;

    public FactoryCapacityTier storedTier = FactoryCapacityTier.BASIC;
    public SimpleItemCraftyStorage(ItemStack stack, int initialEnergy, int capacity, int maxOutput, int maxInput, TransportState transportState, FactoryCapacityTier supportableTier, boolean isBlockItem){
        this.supportableTier = supportableTier;
        this.isBlockItem = isBlockItem;
        this.capacity = capacity;
        this.container = stack;
        //? if <1.20.5 {
        /*CompoundTag tag = stack.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        if (tag.getCompound("CYEnergy").isEmpty()){
            CompoundTag storage = new CompoundTag();
            storage.putInt(KEY,initialEnergy);
            storage.putInt("supportedTier",supportableTier.ordinal());
            storage.putInt("tier",storedTier.ordinal());
            tag.put("CYEnergy", storage);
            if (isBlockItem) stack.getOrCreateTag().put(BLOCK_ENTITY_TAG, tag);
        }
        *///?}
        this.transportState = transportState;
        this.maxOutput = maxOutput;
        this.maxInput = maxInput;
    }
    public SimpleItemCraftyStorage(ItemStack stack, int capacity, TransportState transportState, FactoryCapacityTier supportableTier){
        this(stack,0,capacity,capacity,capacity, transportState,supportableTier,stack.getItem() instanceof BlockItem);
    }
    //? if <1.20.5 {
    /*private CompoundTag getEnergyCompound(){
        CompoundTag tag = container.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        return tag.getCompound("CYEnergy");
    }
    *///?}
    @Override
    public CraftyTransaction receiveEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energyReceived = Math.min(getMaxReceive(), transaction.energy);
        int energy = getEnergyStored();
        if (!simulate) {
            if (supportableTier.supportTier(transaction.tier)) setStoredTier(transaction.tier);
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
    public FactoryCapacityTier getSupportedTier() {
        return /*? if <1.20.5 {*/ /*FactoryCapacityTier.values()[getEnergyCompound().getInt("tier")]*//*?} else {*/ container.getOrDefault(FactoryRegistries.ENERGY_TIER_COMPONENT.get(), supportableTier)/*?}*/;
    }

    @Override
    public FactoryCapacityTier getStoredTier() {
        return /*? if <1.20.5 {*/ /*FactoryCapacityTier.values()[getEnergyCompound().getInt("tier")]*//*?} else {*/ container.getOrDefault(FactoryRegistries.STORED_ENERGY_TIER_COMPONENT.get(), FactoryCapacityTier.BASIC)/*?}*/;
    }

    @Override
    public int getEnergyStored() {
        return /*? if <1.20.5 {*/ /*getEnergyCompound().getInt(KEY)*//*?} else {*/ container.getOrDefault(FactoryRegistries.ENERGY_COMPONENT.get(), 0)/*?}*/;
    }

    public void setEnergyStored(int energy) {
        //? if <1.20.5 {
        /*CompoundTag tag = getEnergyCompound();
        tag.putInt(KEY,energy);
        *///?} else {
        container.set(FactoryRegistries.ENERGY_COMPONENT.get(),energy);
        //?}
    }

    public void setStoredTier(FactoryCapacityTier tier){
        //? if <1.20.5 {
        /*CompoundTag tag = getEnergyCompound();
        tag.putInt("tier",tier.ordinal());
        *///?} else {
        container.set(FactoryRegistries.STORED_ENERGY_TIER_COMPONENT.get(),tier);
        //?}
    }

    @Override
    public void setSupportedTier(FactoryCapacityTier tier) {
        //? if <1.20.5 {
        /*CompoundTag tag = getEnergyCompound();
        tag.putInt("supportedTier",tier.ordinal());
        *///?} else {
        container.set(FactoryRegistries.ENERGY_TIER_COMPONENT.get(),tier);
        //?}
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }


    @Override
    public CompoundTag serializeTag() {
        return /*? if <1.20.5 {*/ /*getEnergyCompound()*//*?} else {*/ new CompoundTag()/*?}*/;
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        //? if <1.20.5
        //this.container.setTag(nbt);
    }


    public int getMaxConsume(){
        return Math.min(getEnergyStored(),maxOutput);
    }

    @Override
    public int getMaxReceive() {
        return Math.min(getEnergySpace(),maxInput);
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
