package wily.factoryapi.fabric;

import wily.factoryapi.fabriclike.FactoryAPIFabricLike;
import net.fabricmc.api.ModInitializer;

public class FactoryAPIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FactoryAPIFabricLike.init();
    }
}
