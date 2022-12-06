package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;

public interface IFluidItem<T extends IPlatformFluidHandler>
{
   T getFluidStorage(ItemStack stack);


}