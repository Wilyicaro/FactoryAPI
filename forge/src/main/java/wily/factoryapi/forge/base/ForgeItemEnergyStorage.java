package wily.factoryapi.forge.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class ForgeItemEnergyStorage implements IPlatformEnergyStorage<IEnergyStorage>,IEnergyStorage {
    private static final String KEY = "energy";

    private int capacity;
    private final int maxOutput;

    private final int maxInput;

    ItemStack container;
    TransportState transportState;


    public ForgeItemEnergyStorage (ItemStack stack, int initialEnergy, int capacity, int maxOutput, int maxInput, TransportState transportState){
        this.capacity = capacity;
        this.container = stack;
        if (!stack.getOrCreateTag().contains(KEY)) setEnergyStored(initialEnergy);
        this.transportState = transportState;
        this.maxOutput = maxOutput;
        this.maxInput = maxInput;
    }
    public ForgeItemEnergyStorage (ItemStack stack, int capacity, TransportState transportState){
        this(stack,0,capacity,capacity,capacity, transportState);
    }

    @Override
    public int receiveEnergy(int receive, boolean simulate) {
        int energyReceived = Math.min(getSpace(), Math.min(getMaxReceive(), receive));
        int energy = getEnergyStored();
        if (!simulate) {
            energy += energyReceived;
            setEnergyStored(energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int i, boolean bl) {
        return consumeEnergy(i,bl);
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
        return container.getOrCreateTag().getInt(KEY);
    }

    public void setEnergyStored(int energy) {
        container.getOrCreateTag().putInt(KEY,energy);
    }



    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return getTransport().canExtract();
    }

    @Override
    public boolean canReceive() {
        return getTransport().canInsert();
    }


    @Override
    public CompoundTag serializeTag() {
        return container.getOrCreateTag();
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        this.container.setTag(nbt);
    }


    public int getMaxConsume(){
        return Math.min(getEnergyStored(),maxOutput);
    }

    @Override
    public int getMaxReceive() {return Math.min(getMaxEnergyStored(),maxInput);}


    @Override
    public IEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
