package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;
//?} elif forge {
/*import net.minecraftforge.energy.IEnergyStorage;
*///?} elif (neoforge && <1.21.9) {
/*import net.neoforged.neoforge.energy.IEnergyStorage;
*///?} elif neoforge {
/*import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
*///?}

import wily.factoryapi.FactoryAPIPlatform;

public interface IPlatformEnergyStorage extends ITagSerializable<CompoundTag>, IPlatformHandler/*? if forge || (neoforge && <1.21.9) {*//*, IEnergyStorage*//*?} else if fabric {*/,EnergyStorage/*?} else if neoforge {*//*, EnergyHandler*//*?}*/ {



    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param energy
     *            A transaction of Maximum amount of energy to be received;
     * @param simulate
     *            If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    int receiveEnergy(int energy, boolean simulate);

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param energy
     *            Maximum amount of energy to be consumed
     * @param simulate
     *            If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    int consumeEnergy(int energy,boolean simulate);

    /**
     * Returns the amount of energy currently stored.
     */
    int getEnergyStored();

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    int getMaxEnergyStored();


    /**
     * Used to get the remaining energy space available .
     */
    default int getEnergySpace(){ return Math.max(0, getMaxEnergyStored() - getEnergyStored());}

    void setEnergyStored(int energy);


    default int getMaxConsume(){
        return getEnergyStored();
    }

    default int getMaxReceive(){
        return getEnergySpace();
    }

    default Style getComponentStyle(){
         return FactoryAPIPlatform.getPlatformEnergyComponent().getStyle();
     }
    //? if fabric {
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
    //?} else if forge || (neoforge && <1.21.9) {
    /*@Override
    default int extractEnergy(int i, boolean bl) {
        return consumeEnergy(i,bl);
    }

    @Override
    default boolean canExtract() {
        return getTransport().canExtract();
    }

    @Override
    default boolean canReceive() {
        return getTransport().canInsert();
    }
    *///?} else if neoforge {

    /*@Override
    default long getAmountAsLong() {
        return getEnergyStored();
    }

    @Override
    default long getCapacityAsLong() {
        return getMaxEnergyStored();
    }

    @Override
    default int insert(int i, TransactionContext transactionContext) {
        if (transactionContext instanceof Transaction transaction)
            transaction.addCommittingJournal(new SnapshotJournal<Integer>() {
                @Override
                protected Integer createSnapshot() {
                    return 0;
                }

                @Override
                protected void revertToSnapshot(Integer object) {

                }

                @Override
                protected void releaseSnapshot(Integer snapshot) {
                    receiveEnergy(i, false);
                }
            });

        return receiveEnergy(i, true);
    }

    @Override
    default int extract(int i, TransactionContext transactionContext) {
        if (transactionContext instanceof Transaction transaction)
            transaction.addCommittingJournal(new SnapshotJournal<Integer>() {
                @Override
                protected Integer createSnapshot() {
                    return 0;
                }

                @Override
                protected void revertToSnapshot(Integer object) {

                }

                @Override
                protected void releaseSnapshot(Integer snapshot) {
                    consumeEnergy(i, false);
                }
            });

        return consumeEnergy(i, true);
    }

    *///?}
}