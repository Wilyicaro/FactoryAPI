package wily.factoryapi.base.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
//? if >1.20.1 {
import net.minecraft.client.gui.GuiSpriteManager;
//?}

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface FactoryGuiGraphics {
    Map<TextureAtlasSprite, Map<String,ResourceLocation>> spriteTilesCache = new ConcurrentHashMap<>();

    Accessor accessor();

    static FactoryGuiGraphics of(GuiGraphics guiGraphics) {
        return ((Accessor) guiGraphics).getFactoryGuiGraphics();
    }

    static GuiSpriteManager getSprites(){
        return /*? if >1.20.1 {*/ Minecraft.getInstance().getGuiSprites()/*?} else {*//*FactoryAPIClient.sprites*//*?}*/;
    }
    void blit(ResourceLocation texture, int x, int y, int uvX, int uvY, int width, int height);

    void blit(ResourceLocation texture, int x, int y, int z, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight);

    void blit(ResourceLocation texture, int x, int xd, int y, int yd, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight);

    void blit(ResourceLocation texture, int x, int y, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight);

    void blitSprite(ResourceLocation resourceLocation, int x, int y, int width, int height);

    void blitSprite(ResourceLocation resourceLocation, int x, int y, int z, int width, int height);

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
    default void blitSprite(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p) {
        this.blitSprite(resourceLocation, i, j, k, l, m, n, 0, o, p);
    }

    default void blitSprite(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
        TextureAtlasSprite textureAtlasSprite = getSprites().getSprite(resourceLocation);
        GuiSpriteScaling guiSpriteScaling = getSprites().getSpriteScaling(textureAtlasSprite);
        if (guiSpriteScaling instanceof GuiSpriteScaling.Stretch) {
            this.blitSprite(textureAtlasSprite, i, j, k, l, m, n, o, p, q);
        } else {
            this.blitSprite(textureAtlasSprite, m, n, o, p, q);
        }
    }
    void blitSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q);

    void blitSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m);
    *///?}

    default void blitTiledSprite(/*? >=1.21.2 {*/ /*Function<ResourceLocation, RenderType> function, *//*?}*/TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, int s) {
        Minecraft minecraft = Minecraft.getInstance();
        if (l <= 0 || m <= 0 ) {
            return;
        }
        if (p <= 0 || q <= 0) {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + p + "x" + q);
        }

        ResourceLocation tile = spriteTilesCache.computeIfAbsent(textureAtlasSprite, sp-> new ConcurrentHashMap<>()).computeIfAbsent("tile_" + n + "x" + o + "_" + p + "x" + q,(string)->{
            try {
                TextureAtlas atlas = (TextureAtlas) minecraft.getTextureManager().getTexture(textureAtlasSprite.atlasLocation());
                Optional<ResourceLocation> opt = AtlasAccessor.of(atlas).getTexturesByName().entrySet().stream().filter(e-> e.getValue() == textureAtlasSprite).findFirst().map(Map.Entry::getKey);
                if (opt.isPresent()) {
                    NativeImage image = NativeImage.read(minecraft.getResourceManager().getResourceOrThrow(opt.get().withPath("textures/gui/sprites/" +opt.get().getPath() + ".png")).open());
                    int width = (int)Math.ceil(p * image.getWidth() / (double) r);
                    int height = (int)Math.ceil(q * image.getHeight() / (double) s);
                    NativeImage tileImage = new NativeImage(width, height, false);
                    image.copyRect(tileImage,  n * image.getWidth() / r, o * image.getHeight() / s, 0, 0, width, height, false, false);
                    return minecraft.getTextureManager().register("tile", new DynamicTexture(tileImage));
                }
            } catch (IOException e) {
                FactoryAPI.LOGGER.warn(e.getMessage());
            }
            return null;
        });
        //? if <1.21.2 {
        blit(tile,i,j,Math.min(n,p),Math.min(o,q),l,m,p,q);
         //?} else
        /*accessor().self().blit(function,tile,i,j,Math.min(n,p),Math.min(o,q),l,m,p,q,k);*/
    }
    void setColor(float r, float g, float b, float a);
    float[] getColor();
    default void clearColor(){
        setColor(1.0f,1.0f,1.0f,1.0f);
    }
    interface AtlasAccessor {
        static AtlasAccessor of(TextureAtlas atlas){
            return (AtlasAccessor) atlas;
        }
        Map<ResourceLocation,TextureAtlasSprite> getTexturesByName();
    }
    interface Accessor {
        FactoryGuiGraphics getFactoryGuiGraphics();
        default GuiGraphics self(){
            return (GuiGraphics) this;
        }
        MultiBufferSource.BufferSource getBufferSource();
    }

}
