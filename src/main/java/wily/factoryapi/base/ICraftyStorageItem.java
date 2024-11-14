package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerPlatform;


public interface ICraftyStorageItem extends IEnergyStorageItem<ICraftyEnergyStorage>
{
   default ICraftyEnergyStorage getEnergyStorage(ItemStack stack){
      return FactoryAPIPlatform.getItemCraftyEnergyStorage(stack);
   }
   FactoryCapacityTier getSupportedEnergyTier();

   default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(FactoryStorage<T> storage, ItemStack stack) {
      if (storage == FactoryStorage.CRAFTY_ENERGY) return ()-> (T) new SimpleItemCraftyStorage(stack,0,getCapacity(), getMaxConsume(), getMaxReceive(),getTransport(), getSupportedEnergyTier(), ItemContainerPlatform.isBlockItem(stack));
      return ()->null;
   }
}