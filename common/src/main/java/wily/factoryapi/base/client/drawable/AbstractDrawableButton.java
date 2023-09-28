package wily.factoryapi.base.client.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.util.ScreenUtil;

import java.awt.*;
import java.util.function.BiConsumer;

public abstract class AbstractDrawableButton<D extends AbstractDrawableButton<D>> extends DrawableCustomWidth<D> implements GuiEventListener {
    public @Nullable Boolean selected;
    protected BiConsumer<D,Integer> onPress = (b,i)->{};
    public IFactoryDrawableType selection;

    protected boolean hoverSelection = true;
    public Color color;
    public float grave = 1.5F;

    public AbstractDrawableButton(int x, int y,  IFactoryDrawableType buttonImage){
        super(buttonImage,x,y);
    }
    public D onPress(BiConsumer<D,Integer> onPress){
        this.onPress = onPress;
        return (D) this;
    }

    public D grave(float grave){
        this.grave = grave;
        return (D) this;
    }
    public D color(Color color){
        this.color = color;
        return (D) this;
    }
    public D icon(IFactoryDrawableType icon){
        return overlay(icon);
    }
    public D select(Boolean selected){
        this.selected = selected;
        return (D) this;
    }
    public D selection(IFactoryDrawableType selection){
        this.selection = selection;
        return (D) this;
    }
    public D selection(Direction direction){
        return selection(adjacentImage(direction));
    }
    public D withWidth(Integer width){
        this.customWidth = width != null ? Math.max(4,width) : null;
        return (D) this;
    }
    public D disableHoverSelection(){
        this.hoverSelection = false;
        return (D) this;
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return inMouseLimit(d,e);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (inMouseLimit(d,e) && visible.get()) {
            ScreenUtil.playButtonDownSound(grave);
            if (selected != null) selected = !selected;
            onPress.accept((D) this,i);
            return true;
        }
        return false;
    }

    public boolean inMouseLimit(double mouseX, double mouseY) {
        return IFactoryDrawableType.getMouseLimit(mouseX,mouseY,getX(),getY(),width(),height());
    }

    @Override
    public void draw(GuiGraphics graphics) {
        this.draw(graphics,getX(),getY());
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        if (color != null)
            graphics.setColor(color.getRed() / 255F,color.getGreen() / 255F, color.getBlue() / 255F, 1F);
        super.draw(graphics, x, y);
        RenderSystem.setShaderColor(1, 1, 1, RenderSystem.getShaderColor()[3]);
        if (isSelected() || hovered && hoverSelection) {
            if (selection != null) selection.draw(graphics, x, y);
            else graphics.renderOutline(x, y, width(), height(), -1);
        }
    }
    public ScreenRectangle getRectangle() {
        return ScreenUtil.rect2iToRectangle(this);
    }
    public boolean isSelected(){
        return selected == Boolean.TRUE;
    }

    public void setFocused(boolean bl) {
        if (bl) selected = true;
    }

    public boolean isFocused() {
        return false;
    }
}
