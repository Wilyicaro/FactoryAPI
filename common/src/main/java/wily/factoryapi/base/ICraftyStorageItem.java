package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;


public interface ICraftyStorageItem extends IEnergyStorageItem<ICraftyEnergyStorage>
{
   default ICraftyEnergyStorage getEnergyStorage(ItemStack stack){
      return FactoryAPIPlatform.getItemCraftyEnergyStorage(stack);
   }
   FactoryCapacityTiers getSupportedEnergyTier();
}