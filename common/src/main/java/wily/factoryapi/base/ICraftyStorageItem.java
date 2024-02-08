package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;


public interface ICraftyStorageItem extends IEnergyStorageItem<ICraftyEnergyStorage>
{
   default ICraftyEnergyStorage getEnergyStorage(ItemStack stack){
      return FactoryAPIPlatform.getItemCraftyEnergyStorage(stack);
   }
   FactoryCapacityTiers getSupportedEnergyTier();

   default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, ItemStack stack) {
      if (storage == Storages.CRAFTY_ENERGY) return ()-> (T) new SimpleItemCraftyStorage(stack,0,getCapacity(), getMaxConsume(), getMaxReceive(),getTransport(), getSupportedEnergyTier(), ItemContainerUtil.isBlockItem(stack));
      return ()->null;
   }
}