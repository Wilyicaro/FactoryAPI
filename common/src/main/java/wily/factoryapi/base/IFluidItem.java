package wily.factoryapi.base;


import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.function.Predicate;

public interface IFluidItem<T extends IPlatformFluidHandler>
{
   default T getFluidStorage(ItemStack stack){
      return (T) FactoryAPIPlatform.getFluidItemHandlerApi(stack,getFluidStorageBuilder(stack));
   };

   class FluidStorageBuilder {
      public final long capacity;
      public final Predicate<FluidStack> validator;
      public final TransportState transportState;

      public FluidStorageBuilder(long Capacity, Predicate<FluidStack> validator, TransportState transportState){

         capacity = Capacity;
         this.validator = validator;
         this.transportState = transportState;
      }
   }

   default FluidStorageBuilder getFluidStorageBuilder(){
      return getFluidStorageBuilder(null);
   };

   FluidStorageBuilder getFluidStorageBuilder(@Nullable ItemStack stack);
}