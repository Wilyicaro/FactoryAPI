package wily.factoryapi.util;

import net.minecraft.client.gui.GuiGraphics;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

public class ProgressElementRenderUtil {


    public static void renderDefaultProgress(GuiGraphics gui, int x, int y, float percentage, IFactoryDrawableType.DrawableProgress type){

        int progress = Math.round(percentage*(type.plane().isVertical() ? type.drawable().height() : type.drawable().width()));
        if (type.reverse()) {
            if (type.plane().isHorizontal()) x+= type.drawable().width() - progress;
            else y+= type.drawable().height() - progress;
        }
        if(progress > 0) {
            if (type.plane().isHorizontal())
                FactoryGuiGraphics.of(gui).blit(type.drawable().texture(),  x,  y, type.drawable().uvX(), type.drawable().uvY(), progress, type.drawable().height());
            else
                FactoryGuiGraphics.of(gui).blit(type.drawable().texture(),  x,  y + type.drawable().height() - progress, type.drawable().uvX(), type.drawable().uvY() + (type.drawable().height() - progress), type.drawable().width(), progress);
        }
    }

    public static void renderFluidTank(GuiGraphics graphics, int x, int y, IFactoryDrawableType type, FluidInstance instance, int capacity, boolean hasColor){
        int fluidHeight = capacity <= 0 ? 0 : Math.round(((float)instance.getAmount() / capacity) * type.height());
        if (fluidHeight > 0) {
            int fluidWidth = type.width();
            int posY = y + type.height() - fluidHeight;
            if (hasColor) {
                //? if <1.21.6 {
                /*FactoryGuiGraphics.of(graphics).setColor(FluidRenderUtil.getFixedColor(instance), true);
                *///?} else
                FactoryGuiGraphics.of(graphics).setBlitColor(FluidRenderUtil.getFixedColor(instance));
            }
            for (int i = 0; i < fluidWidth; i += 16) {
                for (int j = 0; j < fluidHeight; j += 16) {
                   FluidRenderUtil.renderTiledFluid(graphics, x, posY, i, j, fluidWidth, fluidHeight, FactoryAPIClient.getFluidStillTexture(instance.getFluid()));
                }
            }
            //? if <1.21.6 {
            /*FactoryGuiGraphics.of(graphics).clearColor(true);
            *///?} else
            FactoryGuiGraphics.of(graphics).clearBlitColor();
        }
        type.draw(graphics,x,y);
    }

}
