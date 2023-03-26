package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;


public interface  ICraftyEnergyItem<T extends ICraftyEnergyStorage>
{
   T getCraftyEnergy(ItemStack stack);

   FactoryCapacityTiers getEnergyTier();

}