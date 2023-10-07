package wily.factoryapi.base.client.drawable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import wily.factoryapi.util.ScreenUtil;

import java.util.function.Function;

public class FactoryDrawableSlider extends AbstractDrawableButton<FactoryDrawableSlider> {
    private final DrawableCustomWidth<?> sliderBackground;
    public int value;
    public final int maxValue;

    public int relativePosX = 0;

    public boolean dragging = false;

    protected Function<FactoryDrawableSlider,Component> customText;

    public FactoryDrawableSlider(int x, int y, Function<FactoryDrawableSlider,Component> customText, IFactoryDrawableType buttonImage, IFactoryDrawableType sliderBackground, int buttonWidth, int sliderWidth, int initialValue, int maxValue) {
        super(x, y, buttonImage);
        this.customText = customText;
        this.sliderBackground = new DrawableCustomWidth<>(sliderBackground);
        this.sliderBackground.customWidth = sliderWidth;
        this.value = initialValue;
        this.maxValue = maxValue;
        customWidth = buttonWidth;
    }
    public FactoryDrawableSlider(int x, int y, Function<FactoryDrawableSlider,Component> customText, IFactoryDrawableType buttonImage, int buttonWidth, int sliderWidth, int initialValue, int maxValue) {
        this(x, y, customText, buttonImage,IFactoryDrawableType.create(buttonImage.texture(),buttonImage.uvX() + buttonImage.width(), buttonImage.uvY(), sliderWidth, buttonImage.height()),buttonWidth,sliderWidth, initialValue,maxValue);
    }



    public int getValue() {
        return value;
    }
    public float getPercentage(){
        return (float)value / maxValue;
    }


    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (inMouseLimit(d,e)){
            if (!inButtonLimit(d,e)) value = getActualValue(d);
        }
        return super.mouseClicked(d, e, i);
    }

    protected int getActualValue(double mouseX){
        return Math.round(Math.max(0,Math.min(((float)mouseX - getX()) / sliderBackground.width() * maxValue, maxValue)));
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        selected = dragging = false;
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int i, double f, double g) {
        if (!visible.get()) return false;
        if (inButtonLimit(mouseX,mouseY) || dragging) {
            selected = dragging = true;
            value = getActualValue(mouseX);
            onPress.accept(this,i);
            return true;
        }
        return false;
    }

    @Override
    public boolean inMouseLimit(double mouseX, double mouseY) {
        return sliderBackground.inMouseLimit((int) mouseX, (int) mouseY,getX(),getY());
    }
    public boolean inButtonLimit(double mouseX, double mouseY){
        return IFactoryDrawableType.getMouseLimit(mouseX,mouseY,relativePosX,getY(), width(),getHeight());
    }


    @Override
    public void draw(PoseStack poseStack) {
        if (dragging) selected = true;
        sliderBackground.draw(poseStack,getX(),getY());
        Component comp = customText.apply(this);
        if (comp != null && !comp.getString().isEmpty()) {
            ScreenUtil.drawString(poseStack, comp.getString(),  getX() + (sliderBackground.width() - mc.font.width(comp.getString())) / 2, getY() + getHeight() / 2 - 4, -1, true);
        }
        relativePosX = (int) (getX() + getPercentage() * (sliderBackground.width() - width()));
        draw(poseStack, relativePosX, getY());
    }
}
