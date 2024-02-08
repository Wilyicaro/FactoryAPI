package wily.factoryapi.forge.base;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import wily.factoryapi.base.IPlatformHandler;
import wily.factoryapi.base.Storages;

import java.util.List;

public class CapabilityUtil {

    private static final List<Capability<?>> CAPABILITIES = List.of(ForgeCapabilities.FLUID_HANDLER,ForgeCapabilities.FLUID_HANDLER_ITEM,ForgeCapabilities.ITEM_HANDLER, ForgeCapabilities.ENERGY,FactoryCapabilities.CRAFTY_ENERGY);
    public static  Storages.Storage<? extends IPlatformHandler> capabilityToStorage(Capability<?> capability){
        int index = CAPABILITIES.indexOf(capability);
        return index >= Storages.STORAGES.size() || index < 0 ? null : Storages.STORAGES.get(CAPABILITIES.indexOf(capability));
    }
    public static Capability<?> storageToCapability(Storages.Storage<?> capability){
        int index = Storages.STORAGES.indexOf(capability);
        return index >= CAPABILITIES.size() || index < 0 ? null : CAPABILITIES.get(Storages.STORAGES.indexOf(capability));
    }
}
