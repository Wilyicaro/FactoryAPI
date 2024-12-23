package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public interface ICraftyEnergyStorage extends IPlatformEnergyStorage {


 FactoryCapacityTier getSupportedTier();


 FactoryCapacityTier getStoredTier();


 /**
  * Adds energy to the storage. Returns quantity of energy that was accepted.
  *
  * @param transaction
  *            A transaction of Maximum amount of energy to be inserted with a tier.
  * @param simulate
  *            If TRUE, the insertion will only be simulated.
  * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
  */
 CraftyTransaction receiveEnergy(CraftyTransaction transaction, boolean simulate);


 default int receiveEnergy(int energy, boolean simulate){return receiveEnergy(new CraftyTransaction(energy, getStoredTier()), simulate).energy;}

 /**
  * Removes energy from the storage. Returns quantity of energy that was removed.
  *
  * @param transaction
  *            Maximum amount of energy to be extracted with a tier.
  * @param simulate
  *            If TRUE, the extraction will only be simulated.
  * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
  */
 CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate);

 default int consumeEnergy(int energy,boolean simulate){return consumeEnergy(new CraftyTransaction(energy, getStoredTier()), simulate).energy;}


 void setStoredTier(FactoryCapacityTier tier);

 void setSupportedTier(FactoryCapacityTier tier);


 @Override
 default Style getComponentStyle() {
  return Style.EMPTY.applyFormat(ChatFormatting.AQUA);
 }
}