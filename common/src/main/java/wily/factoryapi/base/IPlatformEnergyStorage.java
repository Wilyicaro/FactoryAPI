package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import wily.factoryapi.FactoryAPIPlatform;

public interface IPlatformEnergyStorage <T> extends ITagSerializable<CompoundTag>,IPlatformHandlerApi<T>
{



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
    default int getSpace(){ return Math.max(0, getMaxEnergyStored() - getEnergyStored());}

    void setEnergyStored(int energy);


     int getMaxConsume();

         default Style getComponentStyle(){
         return FactoryAPIPlatform.getPlatformEnergyComponent().getStyle();
     }

}