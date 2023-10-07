package wily.factoryapi.base.client.drawable;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static wily.factoryapi.util.ProgressElementRenderUtil.minecraft;

public abstract class AbstractDrawableStatic<D extends AbstractDrawableStatic<D,T>, T extends IFactoryDrawableType> extends Rect2i implements IFactoryDrawableType, Widget {
    public  T drawable;
    protected final List<Component> tooltips = new ArrayList<>();
    public boolean hovered = false;
    protected Minecraft mc = Minecraft.getInstance();
    public Supplier<Boolean> visible = ()-> true;
    protected IFactoryDrawableType overlay;

    public AbstractDrawableStatic(T drawable, int posX, int posY){
        super(posX,posY,drawable.width(),drawable.height());
        this.drawable = drawable;
    }
    public D overlay(IFactoryDrawableType overlay){
        this.overlay = overlay;
        return (D) this;
    }
    public D visible(Supplier<Boolean> visible){
        this.visible = visible;
        return (D) this;
    }
    public D tooltip(Component component){
        tooltips.add(component);
        return (D) this;
    }
    public D tooltips(List<Component> components){
        tooltips.addAll(components);
        return (D) this;
    }
    public D clearTooltips(){
        tooltips.clear();
        return (D) this;
    }
    public void draw(PoseStack poseStack) {
        draw(poseStack, getX(), getY());
    }

    @Override
    public void draw(PoseStack poseStack, int x, int y) {
        IFactoryDrawableType.super.draw(poseStack, x, y);
        poseStack.pushPose();
        poseStack.translate(0F,0F,1F);
        if (overlay != null) {
            int dX = overlay instanceof AbstractDrawableStatic<?,?> ? ((AbstractDrawableStatic<?,?>)overlay).getX() : 0;
            int dY = overlay instanceof AbstractDrawableStatic<?,?> ? ((AbstractDrawableStatic<?,?>)overlay).getY() : 0;
            overlay.draw(poseStack, x + dX + (getWidth() - overlay.width()) / 2, y + dY + (getHeight() - overlay.height()) / 2);
        }
        poseStack.popPose();
    }

    public void drawAsFluidTank(PoseStack poseStack, FluidStack stack, long capacity, boolean hasColor) {
        drawable.drawAsFluidTank(poseStack, getX(), getY(), stack, capacity, hasColor);
    }
    public boolean inMouseLimit(double mouseX, double mouseY) {
        return drawable.inMouseLimit(mouseX,mouseY,getX(),getY());
    }

    public ResourceLocation texture() {return drawable.texture();}

    public int uvX() {return drawable.uvX();}
    public int uvY() {return drawable.uvY();}

    public int width() {
        return getWidth();
    }
    public int height() {
        return getHeight();
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        if (!visible.get()) return;
        hovered = inMouseLimit(i,j);
        draw(poseStack);
        if (hovered && !tooltips.isEmpty() && minecraft.screen != null) minecraft.screen.renderComponentTooltip(poseStack,tooltips,i,j);
    }
}
