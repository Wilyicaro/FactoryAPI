package wily.factoryapi.fabric.base;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import team.reborn.energy.api.EnergyStorage;
import wily.factoryapi.base.*;
import wily.factoryapi.fabric.FactoryAPIPlatformImpl;

public class FabricStorages {
    public static void registerDefaultStorages(){
        CraftyEnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFactoryItem st)
                return st.getStorage(Storages.CRAFTY_ENERGY,stack).get();
            else return null;
        });
        EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFactoryItem st)
                return (EnergyStorage) st.getStorage(Storages.ENERGY,stack).get();
            else return null;
        });
        FluidStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFactoryItem st)
                return (Storage<FluidVariant>) st.getStorage(Storages.FLUID_ITEM,stack).get();
            else return null;
        });
        EnergyStorage.SIDED.registerFallback((level,blockPos,blockState,be,d) -> {
            if (be instanceof IFactoryStorage s && s.getStorage(Storages.ENERGY,d).isPresent())
                return (EnergyStorage) s.getStorage(Storages.ENERGY,d).get();
            else return null;
        });
        CraftyEnergyStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) ->
        {
            if (be instanceof IFactoryStorage s && s.getStorage(Storages.CRAFTY_ENERGY,d).isPresent())
                return s.getStorage(Storages.CRAFTY_ENERGY,d).get();
            return null;
        });
        FluidStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) ->
        {
            if (be instanceof IFactoryStorage s && s.getStorage(Storages.FLUID,d).isPresent())
                return (Storage<FluidVariant>) s.getStorage(Storages.FLUID,d).get();
            return null;
        });
        ItemStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) ->
        {
            if (be instanceof IFactoryStorage s && s.getStorage(Storages.ITEM,d).isPresent())
                return (Storage<ItemVariant>) s.getStorage(Storages.ITEM,d).get();
            return null;
        });
    }
}
