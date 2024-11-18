package wily.factoryapi.base.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

public interface WidgetAccessor {
    static WidgetAccessor of(AbstractWidget widget){
        return (WidgetAccessor) widget;
    }
    void setSpriteOverlay(ResourceLocation sprite);
    void setHighlightedSpriteOverlay(ResourceLocation sprite);
    ResourceLocation getSpriteOverlay();
    //? if <=1.20.1
    /*void setHeight(int height);*/
}
