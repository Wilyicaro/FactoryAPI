package wily.factoryapi.base.client.drawable;

import wily.factoryapi.base.Progress;

public class DrawableStaticProgress extends AbstractDrawableStatic<DrawableStaticProgress,IFactoryDrawableType.DrawableProgress>{

    public DrawableStaticProgress(DrawableProgress drawable, int posX, int posY) {
        super(drawable, posX, posY);
    }

    public void drawProgress(net.minecraft.client.gui.GuiGraphics graphics, float percentage){
        drawable.drawProgress(graphics,getX(),getY(), percentage);
    }

    public void drawProgress(net.minecraft.client.gui.GuiGraphics graphics, int progress, int max){
        drawable.drawProgress(graphics,getX(),getY(), progress, max);
    }

    public void drawProgress(net.minecraft.client.gui.GuiGraphics graphics, Progress progress){
        drawable.drawProgress(graphics,getX(),getY(),progress);
    }
}
