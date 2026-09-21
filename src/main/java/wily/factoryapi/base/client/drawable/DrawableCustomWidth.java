package wily.factoryapi.base.client.drawable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import wily.factoryapi.base.client.FactoryGuiGraphics;

public class DrawableCustomWidth<D extends DrawableCustomWidth<D>> extends AbstractDrawableStatic<D,IFactoryDrawableType> {
    public Integer customWidth;
    public DrawableCustomWidth(IFactoryDrawableType drawable, int posX, int posY) {
        super(drawable, posX, posY);
    }
    public DrawableCustomWidth(IFactoryDrawableType drawable) {
        super(drawable, 0, 0);
    }
    @Override
    public int getWidth() {
        return customWidth != null ? customWidth : super.getWidth();
    }

    @Override
    public void draw(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int x, int y) {
        super.draw(graphics, x, y);
        if (customWidth != null && !isSprite()) FactoryGuiGraphics.of(graphics).blit(texture(),x + width() - 2, y, uvX() + drawable.width() - 2, uvY(), 2,height());
    }
}
