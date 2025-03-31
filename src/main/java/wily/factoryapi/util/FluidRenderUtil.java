package wily.factoryapi.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.FactoryGuiGraphics;

public class FluidRenderUtil {
    public static void renderTiledFluid(GuiGraphics graphics, int x, int y, int offsetX, int offsetY, int fluidWidth, int fluidHeight, TextureAtlasSprite fluidSprite){
        FactoryGuiGraphics.of(graphics).blit(fluidSprite.atlasLocation(), x + offsetX, y + offsetY, fluidSprite.getX() * 16f / fluidSprite.contents().width(), fluidSprite.getY() * 16f / fluidSprite.contents().height(), Math.min(fluidWidth - offsetX, 16), Math.min(fluidHeight - offsetY, 16), Math.round((1 / (fluidSprite.getU0() / fluidSprite.getX())) * 16 / fluidSprite.contents().width()), Math.round((1 / (fluidSprite.getV0() / fluidSprite.getY())) * 16 / fluidSprite.contents().height()));
    }

    public static int getFixedColor(FluidInstance fluid){
        int color = FactoryAPIClient.getFluidColor(fluid);
        return ColorUtil.getA(color) <= 0 ? ColorUtil.withAlpha(color, 1.0f) : color;
    }
}
