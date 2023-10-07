package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;


public interface IEnergyStorageItem<T extends IPlatformEnergyStorage<?>>
{
   default T getEnergyStorage(ItemStack stack){
      return (T) FactoryAPIPlatform.getItemEnergyStorage(stack);
   }
   default int getMaxConsume(){
      return getCapacity();
   }
   default int getMaxReceive(){
      return getCapacity();
   }

   int getCapacity();

   default TransportState getTransport(){
    return TransportState.EXTRACT_INSERT;
   }
}