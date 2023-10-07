package wily.factoryapi.forge.base;

import com.google.common.collect.Lists;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.Storages;

import java.util.List;

public class CapabilityUtil {

    private static final List<Capability<?>> CAPABILITIES = Lists.newArrayList(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, CapabilityEnergy.ENERGY, FactoryCapabilities.CRAFTY_ENERGY);
    public static  Storages.Storage<? extends IPlatformHandlerApi<?>> capabilityToStorage(Capability<?> capability){
        int index = CAPABILITIES.indexOf(capability);
        return index >= Storages.STORAGES.size() || index < 0 ? null : Storages.STORAGES.get(CAPABILITIES.indexOf(capability));
    }
    public static Capability<?> storageToCapability(Storages.Storage<?> capability){
        int index = Storages.STORAGES.indexOf(capability);
        return index >= CAPABILITIES.size() || index < 0 ? null : CAPABILITIES.get(Storages.STORAGES.indexOf(capability));
    }
}
