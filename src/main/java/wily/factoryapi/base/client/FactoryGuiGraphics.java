package wily.factoryapi.base.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
//? if >1.20.1 && <1.21.9 {
/*import net.minecraft.client.gui.GuiSpriteManager;
*///?}
//? if >=1.21.9 {
import net.minecraft.data.AtlasIds;
//?}
//? if >=1.21.6 {
import com.mojang.blaze3d.pipeline.RenderPipeline;
//?}

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import wily.factoryapi.FactoryAPIClient;

import java.util.Map;

public interface FactoryGuiGraphics {
    GuiGraphics context();

    MultiBufferSource.BufferSource getBufferSource();
    void pushBufferSource(MultiBufferSource.BufferSource bufferSource);
    void popBufferSource();

    FactoryGuiMatrixStack pose();

    static FactoryGuiGraphics of(GuiGraphics guiGraphics) {
        return ((Accessor) guiGraphics).getFactoryGuiGraphics();
    }

    //? if >=1.21.9 {
    static TextureAtlas getSprites() {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);
    }
    //?} else {
    /*static GuiSpriteManager getSprites() {
        return /^? if >1.20.1 {^/ Minecraft.getInstance().getGuiSprites()/^?} else {^//^FactoryAPIClient.sprites^//^?}^/;
    }
    *///?}

    void blit(ResourceLocation texture, int x, int y, int uvX, int uvY, int width, int height);

    void blit(ResourceLocation texture, int x, int y, int z, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight);

    void blit(ResourceLocation texture, int x, int xd, int y, int yd, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight);

    void blit(ResourceLocation texture, int x, int y, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight);

    void blitSprite(ResourceLocation resourceLocation, int x, int y, int width, int height);

    void blitSprite(ResourceLocation resourceLocation, int x, int y, int z, int width, int height);

    void blitSprite(ResourceLocation resourceLocation, int textureWidth, int textureHeight, int uvX, int uvY, int x, int y, int z, int width, int height);

    void blit(int x, int y, int z, int width, int height, TextureAtlasSprite textureAtlasSprite);

    default void blitSprite(ResourceLocation resourceLocation, int textureWidth, int textureHeight, int uvX, int uvY, int x, int y, int width, int height) {
        this.blitSprite(resourceLocation, textureWidth, textureHeight, uvX, uvY, x, y,0, width, height);
    }

    void enableScissor(int x, int y, int xd, int yd, boolean matrixAffects);

    default void enableScissor(int x, int y, int xd, int yd) {
        enableScissor(x, y, xd, yd, true);
    }

    //? if <1.20.2 {
    /*default void blitNineSlicedSprite(TextureAtlasSprite textureAtlasSprite, GuiSpriteScaling.NineSlice nineSlice, int x, int y, int z, int width, int height) {
        GuiSpriteScaling.NineSlice.Border border = nineSlice.border();
        int n = Math.min(border.left(), width / 2);
        int o = Math.min(border.right(), width / 2);
        int p = Math.min(border.top(), height / 2);
        int q = Math.min(border.bottom(), height / 2);
        if (width == nineSlice.width() && height == nineSlice.height()) {
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, width, height);
        } else if (height == nineSlice.height()) {
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, n, height);
            this.blitTiledSprite(textureAtlasSprite, x + n, y, z, width - o - n, height, n, 0, nineSlice.width() - o - n, nineSlice.height(), nineSlice.width(), nineSlice.height());
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, 0, x + width - o, y, z, o, height);
        } else if (width == nineSlice.width()) {
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, width, p);
            this.blitTiledSprite(textureAtlasSprite, x, y + p, z, width, height - q - p, 0, p, nineSlice.width(), nineSlice.height() - q - p, nineSlice.width(), nineSlice.height());
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - q, x, y + height - q, z, width, q);
        } else {
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, z, n, p);
            this.blitTiledSprite(textureAtlasSprite, x + n, y, z, width - o - n, p, n, 0, nineSlice.width() - o - n, p, nineSlice.width(), nineSlice.height());
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, 0, x + width - o, y, z, o, p);
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - q, x, y + height - q, z, n, q);
            this.blitTiledSprite(textureAtlasSprite, x + n, y + height - q, z, width - o - n, q, n, nineSlice.height() - q, nineSlice.width() - o - n, q, nineSlice.width(), nineSlice.height());
            this.blitSprite(textureAtlasSprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - o, nineSlice.height() - q, x + width - o, y + height - q, z, o, q);
            this.blitTiledSprite(textureAtlasSprite, x, y + p, z, n, height - q - p, 0, p, n, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height());
            this.blitTiledSprite(textureAtlasSprite, x + n, y + p, z, width - o - n, height - q - p, n, p, nineSlice.width() - o - n, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height());
            this.blitTiledSprite(textureAtlasSprite, x + width - o, y + p, z, n, height - q - p, nineSlice.width() - o, p, o, nineSlice.height() - q - p, nineSlice.width(), nineSlice.height());
        }
    }

    void blitSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q);

    void blitSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m);
    *///?}

    //? if <1.21.2 {
    /*default void blitTiledSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, int s) {
        if (l <= 0 || m <= 0 ) {
            return;
        }
        if (p <= 0 || q <= 0) {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + p + "x" + q);
        }
        RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = context().pose().last().pose();

        //? if >=1.20.5 {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        //?} else {
        /^BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        ^///?}

        for(int t = 0; t < l; t += p) {
            int u = Math.min(p, l - t);

            for(int v = 0; v < m; v += q) {
                int w = Math.min(q, m - v);
                addBlitSpriteQuad(textureAtlasSprite, bufferBuilder, matrix4f, r, s, n, o, i + t, j + v, k, u, w);
            }
        }
        BufferUploader.drawWithShader(bufferBuilder./^? if <1.20.5 {^//^end^//^?} else {^/buildOrThrow/^?}^/());
    }

    //? if <=1.20.1 {
    /^private void addBlitSpriteQuad(TextureAtlasSprite textureAtlasSprite, BufferBuilder bufferBuilder, Matrix4f matrix4f, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        addBlitQuad(bufferBuilder, matrix4f, m, m + p, n, n + q, o, textureAtlasSprite.getU((float)k / (float)i * 16), textureAtlasSprite.getU((float)(k + p) / (float)i * 16), textureAtlasSprite.getV((float)l / (float)j * 16), textureAtlasSprite.getV((float)(l + q) / (float)j * 16));
    }
    ^///?} else {
    private void addBlitSpriteQuad(TextureAtlasSprite textureAtlasSprite, BufferBuilder bufferBuilder, Matrix4f matrix4f, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        addBlitQuad(bufferBuilder, matrix4f, m, m + p, n, n + q, o, textureAtlasSprite.getU((float)k / (float)i), textureAtlasSprite.getU((float)(k + p) / (float)i), textureAtlasSprite.getV((float)l / (float)j), textureAtlasSprite.getV((float)(l + q) / (float)j));
    }
    //?}

    private void addBlitQuad(BufferBuilder bufferBuilder, Matrix4f matrix4f, int i, int j, int k, int l, int m, float f, float g, float h, float n) {
        //? if <1.20.5 {
        /^bufferBuilder.vertex(matrix4f, (float)i, (float)k, (float)m).uv(f, h).endVertex();
        bufferBuilder.vertex(matrix4f, (float)i, (float)l, (float)m).uv(f, n).endVertex();
        bufferBuilder.vertex(matrix4f, (float)j, (float)l, (float)m).uv(g, n).endVertex();
        bufferBuilder.vertex(matrix4f, (float)j, (float)k, (float)m).uv(g, h).endVertex();
        ^///?} else {
        bufferBuilder.addVertex(matrix4f, (float)i, (float)k, (float)m).setUv(f, h);
        bufferBuilder.addVertex(matrix4f, (float)i, (float)l, (float)m).setUv(f, n);
        bufferBuilder.addVertex(matrix4f, (float)j, (float)l, (float)m).setUv(g, n);
        bufferBuilder.addVertex(matrix4f, (float)j, (float)k, (float)m).setUv(g, h);
        //?}
    }
    *///?}

    //? if <1.21.6 {

    /*void setColor(int color, boolean changeBlend);

    void setColor(float r, float g, float b, float a, boolean changeBlend);

    default void setColor(int color) {
        setColor(color, false);
    }

    default void setColor(float r, float g, float b, float a) {
        setColor(r, g, b, a, false);
    }


    float[] getColor();

    default void clearColor(boolean changeBlend) {
        setColor(1.0f,1.0f,1.0f,1.0f, changeBlend);
    }

    default void clearColor() {
        clearColor(false);
    }

    *///?}

    //? if >=1.21.2 {
    void setBlitColor(int color);

    void setBlitColor(float r, float g, float b, float a);

    int getBlitColor();

    default void clearBlitColor() {
        setBlitColor(1.0f,1.0f,1.0f,1.0f);
    }
    //?}

    @Deprecated
    void disableDepthTest();

    @Deprecated
    void enableDepthTest();

    interface AtlasAccessor {
        static AtlasAccessor of(TextureAtlas atlas) {
            return (AtlasAccessor) atlas;
        }
        Map<ResourceLocation,TextureAtlasSprite> getTexturesByName();
    }

    interface Accessor {
        FactoryGuiGraphics getFactoryGuiGraphics();
    }

}
