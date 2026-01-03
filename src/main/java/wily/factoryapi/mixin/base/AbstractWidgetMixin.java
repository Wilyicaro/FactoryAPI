package wily.factoryapi.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
//? if >=1.20.5 {
import net.minecraft.client.gui.components.WidgetTooltipHolder;
//?} else {
//?}
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.WidgetAccessor;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetAccessor, GuiEventListener {
    @Shadow protected int height;

    @Shadow public abstract boolean isHoveredOrFocused();

    @Shadow public boolean visible;

    @Unique Identifier overrideSprite = null;
    @Unique Identifier highlightedOverrideSprite = null;
    @Unique Consumer<AbstractWidget> onPressOverride = null;
    @Unique ArbitrarySupplier<Boolean> visibility = ArbitrarySupplier.empty();

    @Override
    public void setSpriteOverride(Identifier sprite) {
        this.overrideSprite = sprite;
    }

    @Override
    public void setHighlightedSpriteOverride(Identifier sprite) {
        this.highlightedOverrideSprite = sprite;
    }

    @Override
    public Identifier getSpriteOverride() {
        return isHoveredOrFocused() ? highlightedOverrideSprite : overrideSprite;
    }
    @Override
    public Consumer<AbstractWidget> getOnPressOverride() {
        return onPressOverride;
    }

    @Override
    public void setOnPressOverride(Consumer<AbstractWidget> onPressOverride) {
        this.onPressOverride = onPressOverride;
    }

    @Unique
    private void onPress(){
        if (getOnPressOverride() != null) getOnPressOverride().accept((AbstractWidget) (Object) this);
    }

    @Override
    public void setVisibility(ArbitrarySupplier<Boolean> supplier) {
        this.visibility = supplier;
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics arg, int i, int j, float f, CallbackInfo ci) {
        if (visibility.isPresent()) visible = visibility.get();
    }

    @Inject(method = "onClick", at = @At("HEAD"))
    public void onClick(CallbackInfo ci) {
        onPress();
    }

    //? <=1.20.1 {
    /*@Override
    public void setHeight(int height) {
        this.height = height;
    }
    *///?}
}
