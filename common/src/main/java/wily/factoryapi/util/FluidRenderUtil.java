package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.hooks.FluidStackHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.opengl.GL11;

import static wily.factoryapi.util.ProgressElementRenderUtil.minecraft;

public class FluidRenderUtil {
    public static void renderTiledFluid( int posX, int posY,int i, int j, int renderAmount, int sizeX, int sizeY, FluidStack fluid, boolean hasColor){

        TextureAtlasSprite fluidSprite = fluidSprite(fluid, hasColor);
        minecraft.getTextureManager().bind(fluidSprite.atlas().location());
        int drawWidth = Math.min(sizeX - i, 16);
        int drawHeight = Math.min(renderAmount - j, 16);

        int drawX = posX + i;
        int drawY = posY + j;

        float minU = fluidSprite.getU0();
        float maxU = fluidSprite.getU1();
        float minV = fluidSprite.getV0();
        float maxV = fluidSprite.getV1();
        float dH = minV + (maxV - minV) * drawHeight / 16F;
        float dW = minU + (maxU - minU) * drawWidth / 16F;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder tes = tessellator.getBuilder();
        tes.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
        tes.vertex(drawX, drawY + drawHeight, 0).uv(minU, dH).endVertex();
        tes.vertex(drawX + drawWidth, drawY + drawHeight, 0).uv(dW, dH).endVertex();
        tes.vertex(drawX + drawWidth, drawY, 0).uv(dW, minV).endVertex();
        tes.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
        tessellator.end();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static TextureAtlasSprite fluidSprite(FluidStack fluid, boolean hasColor){
        TextureAtlasSprite fluidSprite = FluidStackHooks.getStillTexture(fluid);
        if (hasColor){
            int color = FluidStackHooks.getColor(fluid);
            float a = ((color & 0xFF000000) >> 24) / 255F;
            a = a <= 0.001F ? 1 : a;
            float r = ((color & 0xFF0000) >> 16) / 255F;
            float g = ((color & 0xFF00) >> 8) / 255F;
            float b = (color & 0xFF) / 255F;
            RenderSystem.color4f(r,g,b,a);
        }
        return fluidSprite;
    }
}
