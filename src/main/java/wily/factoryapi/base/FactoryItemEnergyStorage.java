package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.init.FactoryRegistries;

public class FactoryItemEnergyStorage extends FactoryEnergyStorage{
    ItemStack container;
    public FactoryItemEnergyStorage(ItemStack stack, int initialEnergy, int capacity, int maxOutput, int maxInput, TransportState transportState){
        super(capacity,null,transportState);
        this.container = stack;
        //? if <1.20.5
        //if (!stack.getOrCreateTag().contains(KEY)) setEnergyStored(initialEnergy);
        this.maxOutput = maxOutput;
        this.maxInput = maxInput;
    }
    public FactoryItemEnergyStorage(ItemStack stack, int capacity, TransportState transportState){
        this(stack,0,capacity,capacity,capacity, transportState);
    }

    @Override
    public int receiveEnergy(int receive, boolean simulate) {
        int energyReceived = Math.min(getEnergySpace(), Math.min(getMaxReceive(), receive));
        int energy = getEnergyStored();
        if (!simulate) {
            energy += energyReceived;
            setEnergyStored(energy);
        }
        return energyReceived;
    }

    public int consumeEnergy(int consume, boolean simulate) {
        int energy = getEnergyStored();
        int energyExtracted = Math.min(energy, Math.min(getMaxConsume(), consume));
        if (!simulate) {
            energy -= energyExtracted;
            setEnergyStored(energy);
        }
        return energyExtracted;
    }


    @Override
    public int getEnergyStored() {
        return /*? if <1.20.5 {*//*container.getOrCreateTag().getInt(KEY)*//*?} else {*/ container.getOrDefault(FactoryRegistries.ENERGY_COMPONENT.get(),0)/*?}*/;
    }

    public void setEnergyStored(int energy) {
        //? if <1.20.5 {
        /*container.getOrCreateTag().putInt(KEY,energy);
        *///?} else
        container.set(FactoryRegistries.ENERGY_COMPONENT.get(),energy);
    }
    //? if <1.20.5 {
    /*@Override
    public CompoundTag serializeTag() {
        return container.getOrCreateTag();
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        this.container.setTag(nbt);
    }
    *///?}

    public int getMaxConsume(){
        return Math.min(getEnergyStored(),maxOutput);
    }

    @Override
    public int getMaxReceive() {return Math.min(getMaxEnergyStored(),maxInput);}

}
