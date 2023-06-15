package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.function.Predicate;

public interface IFluidItem<T extends IPlatformFluidHandler>
{
   default T getFluidStorage(ItemStack stack){
      return (T) FactoryAPIPlatform.getFluidItemHandlerApi(stack,getFluidStorageBuilder(stack));
   };

   record FluidStorageBuilder(long Capacity, Predicate<FluidStack> validator, TransportState transportState){};

   default FluidStorageBuilder getFluidStorageBuilder(){
      return getFluidStorageBuilder(null);
   };

   FluidStorageBuilder getFluidStorageBuilder(@Nullable ItemStack stack);
}