package wily.factoryapi.fabric;


import net.fabricmc.api.ModInitializer;
import wily.factoryapi.FactoryAPI;

public class FactoryAPIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FactoryAPI.init();
    }
}
