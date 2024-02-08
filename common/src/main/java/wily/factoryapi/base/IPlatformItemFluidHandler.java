package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;

public interface IPlatformItemFluidHandler extends IPlatformFluidHandler{
    ItemStack getContainer();
}
