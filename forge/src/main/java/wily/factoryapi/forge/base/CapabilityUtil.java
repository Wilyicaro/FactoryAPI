package wily.factoryapi.forge.base;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.Storages;

public class CapabilityUtil {


    public static  Storages.Storage<? extends IPlatformHandlerApi> capabilityToStorage(Capability<?> capability){
        if (capability == ForgeCapabilities.FLUID_HANDLER) return Storages.FLUID;
        else if (capability == ForgeCapabilities.FLUID_HANDLER_ITEM) return Storages.FLUID_ITEM;
        else if (capability == ForgeCapabilities.ITEM_HANDLER) return Storages.ITEM;
        else if (capability == ForgeCapabilities.ENERGY) return Storages.ENERGY;
        return  capability == FactoryCapabilities.ENERGY_CAPABILITY ? Storages.CRAFTY_ENERGY : null;
    }
}
