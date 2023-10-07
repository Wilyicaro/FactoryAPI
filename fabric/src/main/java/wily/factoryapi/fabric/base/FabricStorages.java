package wily.factoryapi.fabric.base;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyStorage;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;
import wily.factoryapi.fabric.FactoryAPIPlatformImpl;

public class FabricStorages {
    public static void registerDefaultStorages(){
        CraftyEnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof ICraftyStorageItem) {
                return FactoryAPIPlatformImpl.getItemCraftyEnergyStorageApi(stack);
            } else
                return null;
        });
        Energy.registerHolder(FabricEnergyStorage.class,(o)-> (EnergyStorage) o);
        Energy.registerHolder(FabricItemStorage.class,(o)-> (EnergyStorage) o);
        Energy.registerHolder((o)-> (o instanceof ItemStack && ((ItemStack)o).getItem() instanceof IEnergyStorageItem<?>), (stack) -> (EnergyStorage) FactoryAPIPlatformImpl.getItemEnergyStorageApi((ItemStack) stack));
        FluidStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFluidHandlerItem<?>) {
                return (Storage<FluidVariant>) FactoryAPIPlatformImpl.getItemFluidHandler(stack,ctx);
            } else
                return null;
        });
        Energy.registerHolder((o)-> (o instanceof IFactoryStorage && ((IFactoryStorage) o).getStorage(Storages.ENERGY).isPresent()), (s) -> (EnergyStorage) ((IFactoryStorage)s).getStorage(Storages.ENERGY).get());
        CraftyEnergyStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) ->
        {
            if (be instanceof IFactoryStorage && ((IFactoryStorage)be).getStorage(Storages.CRAFTY_ENERGY,d).isPresent())
                return ((IFactoryStorage)be).getStorage(Storages.CRAFTY_ENERGY,d).get().getHandler();
            return null;
        });
        FluidStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) ->
        {
            if (be instanceof IFactoryStorage && ((IFactoryStorage)be).getStorage(Storages.FLUID,d).isPresent())
                return (Storage<FluidVariant>) ((IFactoryStorage)be).getStorage(Storages.FLUID,d).get().getHandler();
            return null;
        });
        ItemStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) ->
        {
            if (be instanceof IFactoryStorage && ((IFactoryStorage)be).getStorage(Storages.ITEM,d).isPresent())
                return (Storage<ItemVariant>) ((IFactoryStorage)be).getStorage(Storages.ITEM,d).get().getHandler();
            return null;
        });
    }
}
