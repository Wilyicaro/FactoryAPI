package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.util.FluidInstance;

public interface IFluidHandlerItem<T extends IPlatformFluidHandler> extends IFactoryItem
{
   default T getFluidStorage(ItemStack stack){
      return (T) FactoryAPIPlatform.getItemFluidHandler(stack);
   };

   int getCapacity();

   default boolean isFluidValid(FluidInstance fluidInstance){
      return true;
   }
   default TransportState getTransport(){
      return TransportState.EXTRACT_INSERT;
   }

   @Override
   default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(FactoryStorage<T> storage, ItemStack stack) {
      if (storage == FactoryStorage.FLUID) return ()-> (T) new FactoryItemFluidHandler(getCapacity(),stack,this::isFluidValid,getTransport());
      return ()->null;
   }
}