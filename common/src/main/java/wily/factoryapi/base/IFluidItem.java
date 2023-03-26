package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.function.Predicate;

public interface IFluidItem<T extends IPlatformFluidHandler>
{
   default T getFluidStorage(ItemStack stack){
      return (T) FactoryAPIPlatform.getFluidItemHandlerApi(stack,getFluidStorageBuilder());
   };

   record FluidStorageBuilder(long Capacity, Predicate<FluidStack> validator, TransportState transportState){};

   FluidStorageBuilder getFluidStorageBuilder();
}