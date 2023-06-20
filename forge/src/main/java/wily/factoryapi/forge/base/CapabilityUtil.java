package wily.factoryapi.forge.base;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.Storages;

public class CapabilityUtil {


    public static  Storages.Storage<? extends IPlatformHandlerApi> capabilityToStorage(Capability<?> capability){
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return Storages.FLUID;
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return Storages.FLUID_ITEM;
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return Storages.ITEM;
        else if (capability == CapabilityEnergy.ENERGY) return Storages.ENERGY;
        return null;
    }
}
