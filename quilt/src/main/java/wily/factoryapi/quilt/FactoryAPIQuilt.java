package wily.factoryapi.quilt;

import wily.factoryapi.fabric.FactoryAPIFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class FactoryAPIQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        FactoryAPIFabricLike.init();
    }
}
