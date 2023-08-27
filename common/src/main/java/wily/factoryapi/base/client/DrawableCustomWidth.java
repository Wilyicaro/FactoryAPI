package wily.factoryapi.base.client;

import net.minecraft.client.gui.GuiGraphics;

public class DrawableCustomWidth extends IFactoryDrawableType.DrawableStatic<IFactoryDrawableType> {
    public Integer customWidth;
    public DrawableCustomWidth(IFactoryDrawableType drawable, int posX, int posY) {
        super(drawable, posX, posY);
    }
    public DrawableCustomWidth(IFactoryDrawableType drawable) {
        super(drawable, 0, 0);
    }
    @Override
    public int width() {
        return customWidth != null ? customWidth : super.width();
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        super.draw(graphics, x, y);
        if (customWidth != null) graphics.blit(texture(),x + width() - 2, y, uvX() + drawable.width() - 2, uvY(), 2,height());
    }
}
