package wily.factoryapi.fabric.base;

import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.EnergyHandler;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

public interface FabricEnergyStoragePlatform extends IPlatformEnergyStorage<EnergyHandler> {

    String KEY = "energy";

    @Override
    default int receiveEnergy(int energy, boolean simulate) {
        EnergyHandler handler = getHandler();
        if (!getTransport().canInsert())
            return 0;
        if (simulate) handler.simulate();
        int energyReceived = (int) handler.insert(energy);
        return energyReceived;
    }

    @Override
    default int consumeEnergy(int energy, boolean simulate) {
        EnergyHandler handler = getHandler();
        if (!getTransport().canExtract())
            return 0;
        if (simulate) handler.simulate();
        return (int) handler.extract(energy);
    }

    @Override
    default int getEnergyStored() {
        return (int) getHandler().getEnergy();
    }

    @Override
    default int getMaxEnergyStored() {
        return (int) getHandler().getMaxStored();
    }

    @Override
    default void setEnergyStored(int energy) {
        this.consumeEnergy(getEnergyStored(),false);
        this.receiveEnergy(energy,false);
    }

    @Override
    default int getMaxConsume() {
        return (int) getHandler().getMaxOutput();
    }

    @Override
    default int getMaxReceive() {
        return (int) getHandler().getMaxInput();
    }

    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }

    @Override
    default CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY,getEnergyStored());
        return tag;
    }

    @Override
    default void deserializeTag(CompoundTag nbt) {
        setEnergyStored(nbt.getInt(KEY));
    }

}
