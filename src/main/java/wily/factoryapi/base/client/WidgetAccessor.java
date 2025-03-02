package wily.factoryapi.base.client;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.ArbitrarySupplier;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface WidgetAccessor {
    static WidgetAccessor of(AbstractWidget widget){
        return (WidgetAccessor) widget;
    }

    void setSpriteOverride(ResourceLocation sprite);

    void setHighlightedSpriteOverride(ResourceLocation sprite);

    ResourceLocation getSpriteOverride();

    Consumer<AbstractWidget> getOnPressOverride();

    void setOnPressOverride(Consumer<AbstractWidget> onPressOverride);

    void setVisibility(ArbitrarySupplier<Boolean> supplier);

    //? if <=1.20.1
    /*void setHeight(int height);*/
}
