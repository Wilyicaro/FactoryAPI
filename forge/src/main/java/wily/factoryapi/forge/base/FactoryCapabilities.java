package wily.factoryapi.forge.base;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wily.factoryapi.base.ICraftyEnergyStorage;

public class FactoryCapabilities {
    public static Capability<ICraftyEnergyStorage> ENERGY_CAPABILITY = CapabilityManager.get(new CapabilityToken<ICraftyEnergyStorage>() {
    });

    public FactoryCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ICraftyEnergyStorage.class);
    }
}
