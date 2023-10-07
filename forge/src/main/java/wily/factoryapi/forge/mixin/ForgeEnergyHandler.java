package wily.factoryapi.forge.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

@Mixin(IEnergyStorage.class)
public interface ForgeEnergyHandler extends IPlatformEnergyStorage<IEnergyStorage> {
    @Override
    default IEnergyStorage getHandler() {
        return (IEnergyStorage) this;
    }
    @Override
    default int receiveEnergy(int energy, boolean simulate) {
        return getHandler().receiveEnergy(energy,simulate);
    }

    @Override
    default int consumeEnergy(int energy, boolean simulate) {
        return getHandler().extractEnergy(energy,simulate);
    }

    @Override
    default int getEnergyStored() {
        return getHandler().getEnergyStored();
    }

    @Override
    default int getMaxEnergyStored() {
        return getHandler().getMaxEnergyStored();
    }

    @Override
    default void setEnergyStored(int energy) {
        if (getHandler().getEnergyStored() > 0 && getHandler().getEnergyStored() != energy) getHandler().extractEnergy(getEnergyStored(),false);
        getHandler().receiveEnergy(energy,false);
    }

    @Override
    default int getMaxConsume() {
        return getMaxEnergyStored();
    }


    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }

    @Override
    default CompoundTag serializeTag() {
        return new CompoundTag();
    }

}
