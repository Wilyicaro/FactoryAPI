package wily.factoryapi.fabriclike;

import dev.architectury.platform.Platform;

public class FactoryAPIFabricLike {
    public static void init() {
        wily.factoryapi.FactoryAPI.init();
    }

    public static boolean hasTechReborn(){
        return Platform.isModLoaded("techreborn");
    }
}
