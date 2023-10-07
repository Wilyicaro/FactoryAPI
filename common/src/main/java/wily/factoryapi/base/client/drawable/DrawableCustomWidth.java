package wily.factoryapi.base.client.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

import static wily.factoryapi.util.ProgressElementRenderUtil.minecraft;

public class DrawableCustomWidth<D extends DrawableCustomWidth<D>> extends AbstractDrawableStatic<D,IFactoryDrawableType> {
    public Integer customWidth;
    public DrawableCustomWidth(IFactoryDrawableType drawable, int posX, int posY) {
        super(drawable, posX, posY);
    }
    public DrawableCustomWidth(IFactoryDrawableType drawable) {
        super(drawable, 0, 0);
    }
    @Override
    public int width() {
        return customWidth != null ? customWidth : super.getWidth();
    }

    @Override
    public void draw(PoseStack poseStack, int x, int y) {
        super.draw(poseStack, x, y);
        if (customWidth != null){
            RenderSystem.setShaderTexture(0,texture());
            GuiComponent.blit(poseStack,x + width() - 2, y, uvX() + drawable.width() - 2, uvY(), 2,getHeight(),256,256);
        }
    }
}
