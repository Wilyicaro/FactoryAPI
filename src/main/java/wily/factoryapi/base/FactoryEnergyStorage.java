package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class FactoryEnergyStorage implements IPlatformEnergyStorage {
    public static final String KEY = "energy";
    protected int energy = 0;
    public int maxOutput;
    public int maxInput;
    protected int capacity;
    BlockEntity be;

    protected TransportState transportState;
    public FactoryEnergyStorage(int capacity, @Nullable BlockEntity be, TransportState transportState) {
        this.capacity = maxOutput = maxInput = capacity;
        this.be = be;
        this.transportState = transportState;
    }
    public FactoryEnergyStorage(int capacity, BlockEntity be) {
        this(capacity,be,TransportState.EXTRACT_INSERT);
    }
    public void setChanged(){
        if (be != null)
            be.setChanged();
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    public int receiveEnergy(int maxInsert, boolean simulate) {
        if (!getTransport().canInsert()) return 0;
        int energyReceived = Math.min(getMaxReceive(), maxInsert);
        if (!simulate && energyReceived != 0) {
            energy += energyReceived;
            setChanged();
        }
        return energyReceived;
    }

    public int consumeEnergy(int maxExtracted, boolean simulate) {
        if (!getTransport().canExtract()) return 0;
        int energyExtracted = Math.min(getMaxConsume(), maxExtracted);
        if (!simulate) {
            energy -= energyExtracted;
            setChanged();
        }
        return energyExtracted;
    }

    @Override
    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    @Override
    public int getMaxConsume() {
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

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY, energy);
        return tag;
    }

    @Override
    public boolean isRemoved() {
        return be.isRemoved();
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        setEnergyStored(nbt.getInt("energy"));
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    public static class SidedWrapper extends FactoryEnergyStorage implements IModifiableTransportHandler{
        private final IPlatformEnergyStorage energyStorage;

        public SidedWrapper(FactoryEnergyStorage energyStorage) {
            super(energyStorage.getMaxEnergyStored(), energyStorage.be, energyStorage.getTransport());
            this.energyStorage = energyStorage;
        }

        @Override
        public int getEnergyStored() {
            return energyStorage.getEnergyStored();
        }

        @Override
        public void setEnergyStored(int energy) {
            energyStorage.setEnergyStored(energy);
        }
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return energyStorage.receiveEnergy(maxReceive, simulate);
        }
        public int consumeEnergy(int maxExtract, boolean simulate) {
            return energyStorage.consumeEnergy(maxExtract, simulate);
        }

        @Override
        public void setTransport(TransportState state) {
            transportState = state;
        }
    }
}
