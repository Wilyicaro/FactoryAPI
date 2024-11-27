package wily.factoryapi.mixin.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.2
/*import net.minecraft.util.ARGB;*/
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
//? if <=1.20.1 {
/*import wily.factoryapi.base.client.GuiSpriteScaling;
*///?}
import java.util.function.Function;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements FactoryGuiGraphics.Accessor {
    //? if >=1.21.2 {
    /*private int blitColor = -1;
    *///?}
    @Shadow @Final private MultiBufferSource.BufferSource bufferSource;

    //? if <=1.20.1
    /*@Shadow abstract void innerBlit(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n);*/

    @Unique
    private final FactoryGuiGraphics factoryGuiGraphics = new FactoryGuiGraphics() {
        @Override
        public Accessor accessor() {
            return GuiGraphicsMixin.this;
        }

        @Override
        public void blit(ResourceLocation texture, int x, int y, int uvX, int uvY, int width, int height) {
            //? if <1.21.2 {
            self().blit(texture, x, y, uvX, uvY, width, height);
             //?} else {
            /*self().blit(RenderType::guiTextured, texture, x, y, uvX, uvY, width, height, 256, 256);
            *///?}
        }

        @Override
        public void blit(ResourceLocation texture, int x, int y, int z, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight) {
            //? if <1.21.2 {
            self().blit(texture, x, y, z, uvX, uvY, width, height, textureWidth, textureHeight);
            //?} else {
            /*innerBlit(RenderType::guiTextured, texture, x, x + width, y, y + height, 0, (uvX + 0.0F) / (float)textureWidth, (uvX + (float)width) / (float)textureWidth, (uvY + 0.0F) / (float)textureHeight, (uvY + (float)height) / (float)textureHeight, blitColor);
            *///?}
        }

        @Override
        public void blit(ResourceLocation texture, int x, int xd, int y, int yd, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight) {
            //? if <1.21.2 {
            self().blit(texture, x, xd, y, yd, uvX, uvY, width, height, textureWidth, textureHeight);
            //?} else {
            /*innerBlit(RenderType::guiTextured, texture, x, xd, y, yd, 0, (uvX + 0.0F) / (float)textureWidth, (uvX + (float)width) / (float)textureWidth, (uvY + 0.0F) / (float)textureHeight, (uvY + (float)height) / (float)textureHeight, blitColor);
            *///?}
        }

        @Override
        public void blit(ResourceLocation texture, int x, int y, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight) {
            //? if <1.21.2 {
            self().blit(texture, x, y, uvX, uvY, width, height, textureWidth, textureHeight);
            //?} else
            /*self().blit(RenderType::guiTextured, texture, x, y, uvX, uvY, width, height, textureWidth, textureHeight);*/
        }


        public void blitSprite(ResourceLocation resourceLocation, int x, int y, int width, int height) {
            //? if <1.20.2 {
            /*this.blitSprite(resourceLocation, x, y, 0, width, height);
             *///?} else if <1.21.2 {
            self().blitSprite(resourceLocation, x, y, width, height);
             //?} else
            /*self().blitSprite(RenderType::guiTextured,resourceLocation, x, y, width, height,blitColor);*/
        }

        public void blitSprite(ResourceLocation resourceLocation, int x, int y, int z, int width, int height) {
        //? if <1.20.2 {
        /*TextureAtlasSprite textureAtlasSprite = FactoryGuiGraphics.getSprites().getSprite(resourceLocation);
        GuiSpriteScaling guiSpriteScaling = FactoryGuiGraphics.getSprites().getSpriteScaling(textureAtlasSprite);
        if (guiSpriteScaling instanceof GuiSpriteScaling.Stretch) {
            this.blitSprite(textureAtlasSprite, x, y, z, width, height);
        } else if (guiSpriteScaling instanceof GuiSpriteScaling.Tile tile) {
            this.blitTiledSprite(textureAtlasSprite, x, y, z, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height());
        } else if (guiSpriteScaling instanceof GuiSpriteScaling.NineSlice nineSlice) {
            this.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, z, width, height);
        }
        *///?} else if <1.21.2 {
            self().blitSprite(resourceLocation, x, y, z, width, height);
             //?} else {
            /*if (z != 0) {
                self().pose().pushPose();
                self().pose().translate(0,z,0);
            }
            self().blitSprite(RenderType::guiTextured,resourceLocation, x, y, width, height, blitColor);
            if (z != 0) self().pose().popPose();
            *///?}
        }

        @Override
        public void setColor(float r, float g, float b, float a) {
            //? if <1.21.2 {
            self().setColor(r,g,b,a);
             //?} else {
            /*blitColor = ARGB.colorFromFloat(a,r,g,b);
            *///?}
        }

        @Override
        public float[] getColor() {
            //? if <1.21.2 {
            return RenderSystem.getShaderColor();
             //?} else {
            /*return new float[]{ARGB.red(blitColor),ARGB.green(blitColor), ARGB.blue(blitColor)};
            *///?}
        }
        //? if >=1.21.2 {
        /*private void innerBlit(Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l, int z, float f, float g, float h, float m, int n) {
            RenderType renderType = function.apply(resourceLocation);
            Matrix4f matrix4f = self().pose().last().pose();
            VertexConsumer vertexConsumer = getBufferSource().getBuffer(renderType);
            vertexConsumer.addVertex(matrix4f, (float)i, (float)k, z).setUv(f, h).setColor(n);
            vertexConsumer.addVertex(matrix4f, (float)i, (float)l, z).setUv(f, m).setColor(n);
            vertexConsumer.addVertex(matrix4f, (float)j, (float)l, z).setUv(g, m).setColor(n);
            vertexConsumer.addVertex(matrix4f, (float)j, (float)k, z).setUv(g, h).setColor(n);
            getBufferSource().endBatch(renderType);
        }
        *///?}
        //? if <=1.20.1 {
        /*public void blitSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
            if (p != 0 && q != 0) {
                GuiGraphicsMixin.this.innerBlit(textureAtlasSprite.atlasLocation(), m, m + p, n, n + q, o, textureAtlasSprite.getU((float)k / (float)i * 16), textureAtlasSprite.getU((float)(k + p) / (float)i * 16), textureAtlasSprite.getV((float)l / (float)j * 16), textureAtlasSprite.getV((float)(l + q) / (float)j * 16));
            }
        }

        public void blitSprite(TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m) {
            if (l != 0 && m != 0) {
                GuiGraphicsMixin.this.innerBlit(textureAtlasSprite.atlasLocation(), i, i + l, j, j + m, k, textureAtlasSprite.getU0(), textureAtlasSprite.getU1(), textureAtlasSprite.getV0(), textureAtlasSprite.getV1());
            }
        }
        *///?}

    };
    @Override
    public FactoryGuiGraphics getFactoryGuiGraphics() {
        return factoryGuiGraphics;
    }

    @Override
    public MultiBufferSource.BufferSource getBufferSource() {
        return this.bufferSource;
    }

    //? if >=1.21.2 {

    /*@ModifyArg(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIIIIIIII)V"), index = 10)
    public int blitBlitCustom(int par3){
        return blitColor;
    }
    @ModifyArg(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIIII)V"), index = 6)
    public int blitSprite(int par3){
        return blitColor;
    }
    @ModifyArg(method = "blitSprite(Ljava/util/function/Function;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIIII)V"), index = 6)
    public int blitSpriteAtlas(int par3){
        return blitColor;
    }
    @ModifyArg(method = "blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIIII)V"), index = 12)
    public int blit(int par3){
        return blitColor;
    }
    *///?}

    //? if >1.20.1 {
    @Inject(method = "blitTiledSprite", at = @At("HEAD"), cancellable = true)
    public void blitTiledSprite(/*? if >=1.21.2 {*//*Function<ResourceLocation, RenderType> function,*//*?}*/ TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, int s, CallbackInfo ci) {
        //? if <1.21.2 {
        getFactoryGuiGraphics().blitTiledSprite(textureAtlasSprite, i, j, k, l, m, n, o, p, q, r, s);
        //?} else
        /*getFactoryGuiGraphics().blitTiledSprite(function, textureAtlasSprite, i, j, s,k, l, m, n, o, p, q, r);*/
        ci.cancel();
    }
    //?}
}
