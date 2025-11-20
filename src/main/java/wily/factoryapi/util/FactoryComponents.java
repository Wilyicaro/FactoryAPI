package wily.factoryapi.util;

import net.minecraft.network.chat.Component;

public class FactoryComponents {
    public static Component optionsName(String key){
        return Component.translatable("options.factory_api."+key);
    }

    public static Component pixelValueLabel(Component name, Component value) {
        return Component.translatable("options.pixel_value", name, value);
    }

    public static Component percentValueLabel(Component name, Component value) {
        return Component.translatable("options.percent_value", name, value);
    }
}
