package wily.factoryapi.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import wily.factoryapi.FactoryAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FactoryAPI.MOD_ID)
public class FactoryAPIForge {
    public FactoryAPIForge() {
        EventBuses.registerModEventBus(FactoryAPI.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FactoryAPI.init();
    }
}
