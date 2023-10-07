package wily.factoryapi.fabric.base;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.*;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

public class FabricEnergyStorage implements IPlatformEnergyStorage<FabricEnergyStorage>,EnergyStorage {

    BlockEntity be;

    public TransportState transportState;
    public EnergyHandler handler;

    public int energyStored = 0;
    public long capacity;
    public FabricEnergyStorage(long capacity, BlockEntity be, TransportState transportState) {
        this.be = be;
        this.transportState = transportState;
        this.capacity = capacity;
        this.handler = Energy.of(this);
    }
    public FabricEnergyStorage(long capacity, BlockEntity be) {
        this(capacity, be, TransportState.EXTRACT_INSERT);
    }
    public static final String KEY = "energy";

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        if (!getTransport().canInsert())
            return 0;
        if (simulate) handler.simulate();
        int energyReceived = (int) handler.insert(energy);
        handler = Energy.of(this);
        return energyReceived;
    }

    @Override
    public int consumeEnergy(int energy, boolean simulate) {
        if (!getTransport().canExtract())
            return 0;
        if (simulate) handler.simulate();
        int energyReceived = (int) handler.extract(energy);
        handler = Energy.of(this);
        return energyReceived;
    }

    @Override
    public int getEnergyStored() {
        return energyStored;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) capacity;
    }

    @Override
    public void setEnergyStored(int energy) {
        energyStored = energy;
    }


    @Override
    public double getMaxInput(EnergySide side) {
        if (!getTransport().canInsert()) return 0;
        return getMaxReceive();
    }

    @Override
    public double getMaxOutput(EnergySide side) {
        if (!getTransport().canExtract()) return 0;
        return getMaxConsume();
    }

    @Override
    public FabricEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY,getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        setEnergyStored(nbt.getInt(KEY));
    }



    public static FabricEnergyStorage filtered(IPlatformEnergyStorage<FabricEnergyStorage> energyStorage, TransportState transportState){
        return new FabricEnergyStorage(energyStorage.getMaxEnergyStored(), energyStorage.getHandler().be, transportState){
            @Override
            public int getEnergyStored() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public void setEnergyStored(int energy) {
                energyStorage.setEnergyStored(energy);
            }

            @Override
            public int getMaxEnergyStored() {
                return energyStorage.getMaxEnergyStored();
            }

            @Override
            public EnergyTier getTier() {
                return energyStorage.getHandler().getTier();
            }

            @Override
            public void deserializeTag(CompoundTag nbt) {
                energyStorage.deserializeTag(nbt);
            }

            @Override
            public CompoundTag serializeTag() {
                return energyStorage.serializeTag();
            }

        };
    }

    @Override
    public double getStored(EnergySide face) {
        return getEnergyStored();
    }

    @Override
    public void setStored(double amount) {
        setEnergyStored((int) amount);
    }

    @Override
    public double getMaxStoredPower() {
        return getMaxEnergyStored();
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.LOW;
    }
}
