package wily.factoryapi.forge.base;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.ICraftyEnergyStorage;
@Mod.EventBusSubscriber(modid = FactoryAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FactoryCapabilities {
    public static Capability<ICraftyEnergyStorage> CRAFTY_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ICraftyEnergyStorage.class);
    }
}
