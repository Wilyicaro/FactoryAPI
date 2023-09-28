package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;


@Environment(value = EnvType.CLIENT)
public class ProgressElementRenderUtil {


    public static void renderDefaultProgress(GuiGraphics gui, int x, int y, float percentage, IFactoryDrawableType.DrawableProgress type){

        int progress = Math.round(percentage*(type.plane().isVertical() ? type.height() : type.width()));
        if (type.reverse()) {
            if (type.plane().isHorizontal()) x+= type.width() - progress;
            else y+= type.height() - progress;
        }
        if(progress > 0) {
            if (type.plane().isHorizontal())
                gui.blit(type.texture(),  x,  y, type.uvX(), type.uvY(), progress, type.height());
            else
                gui.blit(type.texture(),  x,  y + type.height() - progress, type.uvX(), type.uvY() + (type.height() - progress), type.width(), progress);
        }
    }

    public static void renderFluidTank(GuiGraphics graphics, int x, int y, IFactoryDrawableType type, FluidStack stack, long capacity, boolean hasColor){
        int progress = capacity <= 0 ? 0 : Math.round(((float)stack.getAmount() /capacity) * type.height());
        if (progress > 0) {
            RenderSystem.enableBlend();
            progress /= 1.3;
            int fluidWidth = type.width();
            int posY = y + type.height() - progress;

            TextureAtlasSprite fluidSprite = FluidRenderUtil.fluidSprite(stack, hasColor);
            RenderSystem.setShaderTexture(0, fluidSprite.atlasLocation());
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            for (int i = 0; i < fluidWidth; i += 16) {
                for (int j = 0; j < progress; j += 16) {
                   FluidRenderUtil.renderTiledFluid(graphics ,x, posY, i, j, progress, fluidWidth, fluidSprite);
                }
            }

            RenderSystem.disableBlend();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0,type.texture());
        type.draw(graphics,x,y);
    }

}
