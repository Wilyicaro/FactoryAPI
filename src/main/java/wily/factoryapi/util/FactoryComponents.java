package wily.factoryapi.util;

import net.minecraft.network.chat.Component;

public class FactoryComponents {
    public static Component optionsName(String key){
        return Component.translatable("options.factory_api."+key);
    }
}
