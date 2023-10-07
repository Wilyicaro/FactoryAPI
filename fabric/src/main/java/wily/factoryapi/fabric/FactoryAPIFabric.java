package wily.factoryapi.fabric;


import net.fabricmc.api.ModInitializer;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.fabric.base.FabricStorages;

public class FactoryAPIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FactoryAPI.init();
        FabricStorages.registerDefaultStorages();
    }
}
