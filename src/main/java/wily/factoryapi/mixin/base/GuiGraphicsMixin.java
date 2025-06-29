package wily.factoryapi.mixin.base;


import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
//? if >=1.21.6 {
import com.mojang.blaze3d.textures.GpuTextureView;
//?}
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.FactoryGuiMatrixStack;
import wily.factoryapi.util.ColorUtil;
import wily.factoryapi.util.FactoryScreenUtil;
//? if <=1.20.1 {
/*import wily.factoryapi.base.client.GuiSpriteScaling;
*///?}
import java.util.function.Function;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements FactoryGuiGraphics.Accessor {
    //? if >=1.21.2 {
    @Unique private int blitColor = -1;
    @Unique private boolean depthTest = true;

    //? if <1.21.6 {
    /*@Unique private Function<ResourceLocation,RenderType> getRenderFunction() {
        return depthTest ? RenderType::guiTextured : RenderType::guiTexturedOverlay;
    }
    *///?} else {
    @Unique private RenderPipeline getRenderFunction() {
        return depthTest ? RenderPipelines.GUI_TEXTURED : RenderPipelines.GUI_TEXTURED; // TODO
    }
    //?}
    //? if <1.21.6 {
    /*@Mutable
    @Shadow @Final private MultiBufferSource.BufferSource bufferSource;

    @Shadow protected abstract void applyScissor(ScreenRectangle arg);
    *///?}

    @Shadow @Final private GuiGraphics.ScissorStack scissorStack;
    @Shadow @Final private Minecraft minecraft;

    //? if >=1.21.6 {
    @Shadow protected abstract void submitBlit(RenderPipeline par1, GpuTextureView par2, int par3, int par4, int par5, int par6, float par7, float par8, float par9, float par10, int par11);
    //?}

    @Unique
    private MultiBufferSource.BufferSource lastBufferSource;

    //? if <=1.20.1
    /*@Shadow abstract void innerBlit(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n);*/

    @Unique
    private final FactoryGuiGraphics factoryGuiGraphics = new FactoryGuiGraphics() {

        @Override
        public GuiGraphics context() {
            return (GuiGraphics) (Object)GuiGraphicsMixin.this;
        }


        public void disableDepthTest() {
            FactoryScreenUtil.disableDepthTest();
            //? if >=1.21.2
            depthTest = false;
        }

        public void enableDepthTest() {
            FactoryScreenUtil.enableDepthTest();
            //? if >=1.21.2
            depthTest = true;
        }

        @Override
        public void blit(ResourceLocation texture, int x, int y, int uvX, int uvY, int width, int height) {
            //? if <1.21.2 {
            /*context().blit(texture, x, y, uvX, uvY, width, height);
             *///?} else {
            context().blit(getRenderFunction(), texture, x, y, uvX, uvY, width, height, 256, 256);
            //?}
        }

        @Override
        public void blit(ResourceLocation texture, int x, int y, int z, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight) {
            //? if <1.21.2 {
            /*context().blit(texture, x, y, z, uvX, uvY, width, height, textureWidth, textureHeight);
            *///?} else {
            innerBlit(getRenderFunction(), texture, x, x + width, y, y + height, 0, (uvX + 0.0F) / (float)textureWidth, (uvX + (float)width) / (float)textureWidth, (uvY + 0.0F) / (float)textureHeight, (uvY + (float)height) / (float)textureHeight, blitColor);
            //?}
        }

        @Override
        public void blit(ResourceLocation texture, int x, int xd, int y, int yd, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight) {
            //? if <1.21.2 {
            /*context().blit(texture, x, xd, y, yd, uvX, uvY, width, height, textureWidth, textureHeight);
            *///?} else {
            innerBlit(getRenderFunction(), texture, x, xd, y, yd, 0, (uvX + 0.0F) / (float)textureWidth, (uvX + (float)width) / (float)textureWidth, (uvY + 0.0F) / (float)textureHeight, (uvY + (float)height) / (float)textureHeight, blitColor);
            //?}
        }

        @Override
        public void blit(ResourceLocation texture, int x, int y, float uvX, float uvY, int width, int height, int textureWidth, int textureHeight) {
            //? if <1.21.2 {
            /*context().blit(texture, x, y, uvX, uvY, width, height, textureWidth, textureHeight);
            *///?} else
            context().blit(getRenderFunction(), texture, x, y, uvX, uvY, width, height, textureWidth, textureHeight);
        }


        public void blitSprite(ResourceLocation resourceLocation, int x, int y, int width, int height) {
            //? if <1.20.2 {
            /*this.blitSprite(resourceLocation, x, y, 0, width, height);
             *///?} else if <1.21.2 {
            /*context().blitSprite(resourceLocation, x, y, width, height);
             *///?} else
            context().blitSprite(getRenderFunction(),resourceLocation, x, y, width, height,blitColor);
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
            /*context().blitSprite(resourceLocation, x, y, z, width, height);
             *///?} else {
            if (z != 0) {
                FactoryGuiMatrixStack.of(context().pose()).pushPose();
                FactoryGuiMatrixStack.of(context().pose()).translate(0,z,0);
            }
            context().blitSprite(getRenderFunction(), resourceLocation, x, y, width, height, blitColor);
            if (z != 0) FactoryGuiMatrixStack.of(context().pose()).popPose();
            //?}
        }

        @Override
        public void blitSprite(ResourceLocation resourceLocation, int textureWidth, int textureHeight, int uvX, int uvY, int x, int y, int z, int width, int height) {
            //? if <=1.20.1 {
            /*TextureAtlasSprite textureAtlasSprite = FactoryGuiGraphics.getSprites().getSprite(resourceLocation);
            GuiSpriteScaling guiSpriteScaling = FactoryGuiGraphics.getSprites().getSpriteScaling(textureAtlasSprite);
            if (guiSpriteScaling instanceof GuiSpriteScaling.Stretch) {
                this.blitSprite(textureAtlasSprite, textureWidth, textureHeight, uvX, uvY, x, y, z, width, height);
            } else {
                this.blitSprite(textureAtlasSprite, x, y, z, width, height);
            }
            *///?} else if <1.21.2 {
            /*context().blitSprite(resourceLocation, textureWidth, textureHeight, uvX, uvY, x, y, z, width, height);
            *///?} else {
            if (z != 0) {
                FactoryGuiMatrixStack.of(context().pose()).pushPose();
                FactoryGuiMatrixStack.of(context().pose()).translate(0,z,0);
            }
            context().blitSprite(getRenderFunction(), resourceLocation, textureWidth, textureHeight, uvX, uvY, x, y, width, height);
            if (z != 0) FactoryGuiMatrixStack.of(context().pose()).popPose();
            //?}
        }

        @Override
        public void blit(int x, int y, int z, int width, int height, TextureAtlasSprite textureAtlasSprite) {
            //? if <=1.20.1 {
            /*blitSprite(textureAtlasSprite, x, y, z, width, height);
            *///?} else if <1.21.2 {
            /*context().blit(x, y, z, width, height, textureAtlasSprite);
            *///?} else {
            if (z != 0) {
                FactoryGuiMatrixStack.of(context().pose()).pushPose();
                FactoryGuiMatrixStack.of(context().pose()).translate(0,z,0);
            }
            context().blitSprite(getRenderFunction(), textureAtlasSprite, x, y, width, height);
            if (z != 0) FactoryGuiMatrixStack.of(context().pose()).popPose();
            //?}
        }

        @Override
        public void enableScissor(int x, int y, int xd, int yd, boolean matrixAffects) {
            //? if <1.21.4 {
            /*if (matrixAffects) {
                Matrix4f matrix4f = FactoryGuiMatrixStack.of(context().pose()).last().pose();
                Vector3f vector3f = matrix4f.transformPosition(x, y, 0.0F, new Vector3f());
                Vector3f vector3f2 = matrix4f.transformPosition(xd, yd, 0.0F, new Vector3f());
                applyScissor(scissorStack.push(new ScreenRectangle(Mth.floor(vector3f.x), Mth.floor(vector3f.y), Mth.floor(vector3f2.x - vector3f.x), Mth.floor(vector3f2.y - vector3f.y))));
            } else context().enableScissor(x, y, xd, yd);
            *///?} else {

            if (matrixAffects) {
                context().enableScissor(x, y, xd, yd);
            } else {
                //? if <1.21.6 {
                /*applyScissor(scissorStack.push(new ScreenRectangle(x, y, xd - x, yd - y)));
                *///?} else {
                scissorStack.push(new ScreenRectangle(x, y, xd - x, yd - y));
                //?}
            }
            //?}
        }

        @Override
        public void setColor(int color, boolean changeBlend) {
            setColor(ColorUtil.getRed(color), ColorUtil.getGreen(color), ColorUtil.getBlue(color), ColorUtil.getAlpha(color), changeBlend);
        }

        @Override
        public void setColor(float r, float g, float b, float a, boolean changeBlend) {
            if (changeBlend) {
                if (a < 1) FactoryScreenUtil.enableBlend();
                else FactoryScreenUtil.disableBlend();
            }
            //? if <1.21.6 {
            /*//? if >=1.21.2 {
            context().flush();
            RenderSystem.setShaderColor(r,g,b,a);
            //?} else {
            /^context().setColor(r,g,b,a);
            ^///?}
            *///?}
        }

        @Override
        public float[] getColor() {
            //? if >=1.21.6 {
            return new float[]{1,1,1,1};
            //?} else
            /*return RenderSystem.getShaderColor();*/
            //?}
        }

        //? if >=1.21.2 {
        @Override
        public void setBlitColor(float r, float g, float b, float a) {
            blitColor = ColorUtil.colorFromFloat(r,g,b,a);
        }

        @Override
        public void setBlitColor(int color) {
            blitColor = color;
        }

        @Override
        public int getBlitColor() {
            return blitColor;
        }
        //?}

        //? if >=1.21.2 {
        //? if <1.21.6 {
        /*private void innerBlit(Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l, int z, float f, float g, float h, float m, int n) {
            RenderType renderType = function.apply(resourceLocation);
            Matrix4f matrix4f = FactoryGuiMatrixStack.of(context().pose()).<PoseStack>getNative().last().pose();
            VertexConsumer vertexConsumer = getBufferSource().getBuffer(renderType);
            vertexConsumer.addVertex(matrix4f, (float)i, (float)k, z).setUv(f, h).setColor(n);
            vertexConsumer.addVertex(matrix4f, (float)i, (float)l, z).setUv(f, m).setColor(n);
            vertexConsumer.addVertex(matrix4f, (float)j, (float)l, z).setUv(g, m).setColor(n);
            vertexConsumer.addVertex(matrix4f, (float)j, (float)k, z).setUv(g, h).setColor(n);
            getBufferSource().endBatch(renderType);
        }
        *///?} else {
        private void innerBlit(RenderPipeline pipeline, ResourceLocation resourceLocation, int i, int j, int k, int l, int z, float f, float g, float h, float m, int n) {
            GpuTextureView gpuTextureView = minecraft.getTextureManager().getTexture(resourceLocation).getTextureView();
            submitBlit(pipeline, gpuTextureView, i, k, j, l, f, g, h, m, n);
//            RenderType renderType = function.apply(resourceLocation);
//            Matrix4f matrix4f = FactoryGuiMatrixStack.of(context().pose()).last().pose();
//            VertexConsumer vertexConsumer = getBufferSource().getBuffer(renderType);
//            vertexConsumer.addVertex(matrix4f, (float)i, (float)k, z).setUv(f, h).setColor(n);
//            vertexConsumer.addVertex(matrix4f, (float)i, (float)l, z).setUv(f, m).setColor(n);
//            vertexConsumer.addVertex(matrix4f, (float)j, (float)l, z).setUv(g, m).setColor(n);
//            vertexConsumer.addVertex(matrix4f, (float)j, (float)k, z).setUv(g, h).setColor(n);
//            getBufferSource().endBatch(renderType);
        }
        //?}
        //?}
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
        @Override
        public MultiBufferSource.BufferSource getBufferSource() {
            //? if >=1.21.6 {
            return null;
            //?} else {
            /*return bufferSource;
            *///?}
        }
        @Override
        public void pushBufferSource(MultiBufferSource.BufferSource newBufferSource) {
            //? if <1.21.6 {
            /*lastBufferSource = bufferSource;
            bufferSource = newBufferSource;
            *///?}
        }

        @Override
        public void popBufferSource() {
            //? if <1.21.6 {
            /*if (lastBufferSource != null) bufferSource = lastBufferSource;
            *///?}
        }
    };
    @Override
    public FactoryGuiGraphics getFactoryGuiGraphics() {
        return factoryGuiGraphics;
    }


    //? if >=1.21.2 {
    //? if <1.21.6 {
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
    *///?} else {
    @ModifyArg(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIIIIII)V"), index = 10)
    public int blitBlitCustom(int par3){
        return blitColor;
    }
    @ModifyArg(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIII)V"), index = 6)
    public int blitSprite(int par3){
        return blitColor;
    }
    @ModifyArg(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIIII)V"), index = 6)
    public int blitSpriteAtlas(int par3){
        return blitColor;
    }
    @ModifyArg(method = "blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIIII)V"), index = 12)
    public int blit(int par3){
        return blitColor;
    }
    //?}
    //?}

    //? if >1.20.1 {
    @Inject(method = "blitTiledSprite", at = @At("HEAD"), cancellable = true)
    public void blitTiledSprite(/*? if >=1.21.2 {*//*? if <1.21.6 {*//*Function<ResourceLocation, RenderType>*//*?} else {*/RenderPipeline/*?}*/ function,/*?}*/ TextureAtlasSprite textureAtlasSprite, int i, int j, int k, int l, int m, int n, int o, int p, int q, int r, int s, CallbackInfo ci) {
        //? if <1.21.2 {
        /*getFactoryGuiGraphics().blitTiledSprite(textureAtlasSprite, i, j, k, l, m, n, o, p, q, r, s);
        *///?} else {
        getFactoryGuiGraphics().blitTiledSprite(function, textureAtlasSprite, i, j, s, k, l, m, n, o, p, q, r);
        //?}
        ci.cancel();
    }
    //?}
}
