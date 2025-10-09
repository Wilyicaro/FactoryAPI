//? if neoforge && >=1.21.9 {
/*package wily.factoryapi.base.neoforge;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.TransportState;

public interface NeoForgeEnergyHandlerPlatform extends IPlatformEnergyStorage, IPlatformHandlerApi<EnergyHandler> {
    @Override
    default int receiveEnergy(int energy, boolean simulate) {
        try (Transaction transaction = Transaction.open(null))
        {
            int amount = getHandler().insert(energy, transaction);
            if (!simulate) transaction.commit();
            return amount;
        }
    }

    @Override
    default int consumeEnergy(int energy, boolean simulate) {
        try (Transaction transaction = Transaction.open(null))
        {
            int amount = getHandler().extract(energy, transaction);
            if (!simulate) transaction.commit();
            return amount;
        }
    }

    @Override
    default int getEnergyStored() {
        return getHandler().getAmountAsInt();
    }

    @Override
    default int getMaxEnergyStored() {
        return getHandler().getCapacityAsInt();
    }

    @Override
    default void setEnergyStored(int energy) {
        if (getEnergyStored() > 0 && getEnergyStored() != energy) consumeEnergy(getEnergyStored(),false);
        receiveEnergy(energy,false);
    }

    @Override
    default TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
    }

    @Override
    default CompoundTag serializeTag() {
        return new CompoundTag();
    }
    @Override
    default void deserializeTag(CompoundTag nbt){
    }
}
*///?}
