package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;

public interface IFluidHandlerItem<T extends IPlatformFluidHandler> extends IFactoryItem
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

   @Override
   default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, ItemStack stack) {
      if (storage == Storages.FLUID_ITEM) return ()-> (T) new FactoryItemFluidHandler(getCapacity(),stack,this::isFluidValid,getTransport());
      return ()->null;
   }
}