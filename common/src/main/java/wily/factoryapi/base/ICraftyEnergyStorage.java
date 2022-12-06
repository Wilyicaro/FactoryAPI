package wily.factoryapi.base;

import org.jetbrains.annotations.Nullable;

public interface ICraftyEnergyStorage extends IPlatformEnergyStorage
{


   FactoryCapacityTiers getSupportedTier();


    FactoryCapacityTiers getStoredTier();


    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param transaction
     *            A transaction of Maximum amount of energy to be inserted with a tier.
     * @param simulate
     *            If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    EnergyTransaction receiveEnergy(EnergyTransaction transaction, boolean simulate);


    default int receiveEnergy(int energy, boolean simulate){return receiveEnergy(new EnergyTransaction(energy, getStoredTier()), simulate).energy;}

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param transaction
     *            Maximum amount of energy to be extracted with a tier.
     * @param simulate
     *            If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    EnergyTransaction consumeEnergy(EnergyTransaction transaction,boolean simulate);

    default int consumeEnergy(int energy,boolean simulate){return consumeEnergy(new EnergyTransaction(energy, getStoredTier()), simulate).energy;}

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
    default int getSpace(){ return Math.max(0, getMaxEnergyStored() - getEnergyStored());}

    void setEnergyStored(int energy);

    void setStoredTier(FactoryCapacityTiers tier);


    class EnergyTransaction{
        public int energy;
        public FactoryCapacityTiers tier;
        public EnergyTransaction(int energyTransferred, @Nullable FactoryCapacityTiers energyTier){
            energy = energyTransferred;
            tier = energyTier;
        }
        public EnergyTransaction reduce(double reduction ){
            energy /= reduction;
            return this;
        }
        public static EnergyTransaction EMPTY = new EnergyTransaction(0, null);

    }
     int getMaxConsume();

}