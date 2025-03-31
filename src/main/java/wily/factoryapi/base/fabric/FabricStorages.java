//? if fabric {
package wily.factoryapi.base.fabric;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import team.reborn.energy.api.EnergyStorage;
import wily.factoryapi.base.*;

public class FabricStorages {
    public static void registerDefaultStorages(){
        CraftyEnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFactoryItem st)
                return st.getStorage(FactoryStorage.CRAFTY_ENERGY,stack).get();
            else return null;
        });
        EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFactoryItem st)
                return st.getStorage(FactoryStorage.ENERGY,stack).get();
            else return null;
        });
        FluidStorage.ITEM.registerFallback((stack, ctx) -> {
            if (stack.getItem() instanceof IFactoryItem st)
                return st.getStorage(FactoryStorage.FLUID,stack).get();
            else return null;
        });
        EnergyStorage.SIDED.registerFallback((level,blockPos,blockState,be,d) -> {
            if (be instanceof IFactoryStorage s && s.getStorage(FactoryStorage.ENERGY,d).isPresent())
                return s.getStorage(FactoryStorage.ENERGY,d).get();
            else return null;
        });
        CraftyEnergyStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) -> {
            if (be instanceof IFactoryStorage s && s.getStorage(FactoryStorage.CRAFTY_ENERGY,d).isPresent())
                return s.getStorage(FactoryStorage.CRAFTY_ENERGY,d).get();
            return null;
        });
        FluidStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) -> {
            if (be instanceof IFactoryStorage s && s.getStorage(FactoryStorage.FLUID,d).isPresent())
                return s.getStorage(FactoryStorage.FLUID,d).get();
            return null;
        });
        ItemStorage.SIDED.registerFallback((level, blockPos,blockState,be,d) -> {
            if (be instanceof IFactoryStorage s && s.getStorage(FactoryStorage.ITEM,d).isPresent())
                return s.getStorage(FactoryStorage.ITEM,d).get().getHandler();
            return null;
        });
    }
}
//?}