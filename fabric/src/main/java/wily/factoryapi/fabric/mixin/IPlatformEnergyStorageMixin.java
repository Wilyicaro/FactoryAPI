package wily.factoryapi.fabric.mixin;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import team.reborn.energy.api.EnergyStorage;
import wily.factoryapi.base.FactoryEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;

@Mixin(IPlatformEnergyStorage.class)
public interface IPlatformEnergyStorageMixin extends IPlatformEnergyStorage,EnergyStorage {

    @Override
    default boolean supportsInsertion() {
        return getTransport().canInsert();
    }

    @Override
    default long insert(long maxAmount, TransactionContext transaction) {
        transaction.addCloseCallback((t,r)->{
            if (r.wasCommitted()) receiveEnergy((int)maxAmount, false);
        });
        return receiveEnergy((int)maxAmount, true);
    }

    @Override
    default boolean supportsExtraction() {
        return getTransport().canExtract();
    }

    @Override
    default long extract(long maxAmount, TransactionContext transaction) {
        transaction.addCloseCallback((t,r)->{
            if (r.wasCommitted()) consumeEnergy((int)maxAmount, false);
        });
        return consumeEnergy((int)maxAmount, true);
    }

    @Override
    default long getAmount() {
        return getEnergyStored();
    }

    @Override
    default long getCapacity() {
        return getEnergyStored();
    }
}
