package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;

public interface IFluidHandlerItem<T extends IPlatformFluidHandler<?>>
{
   default T getFluidStorage(ItemStack stack){
      return (T) FactoryAPIPlatform.getItemFluidHandler(stack);
   };

   long getCapacity();

   default boolean isFluidValid(FluidStack fluidStack){
      return true;
   }
   default TransportState getTransport(){
      return TransportState.EXTRACT_INSERT;
   }

}