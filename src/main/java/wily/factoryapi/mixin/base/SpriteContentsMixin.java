package wily.factoryapi.mixin.base;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.client.FactoryOptions;
import wily.factoryapi.base.client.MipmapMetadataSection;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
//? if <=1.20.1
/*import wily.factoryapi.base.client.FactorySpriteContents;*/

@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin /*? if <=1.20.1 {*/ /*implements FactorySpriteContents *//*?}*/{
    //? if <=1.20.1 {
    /*ResourceMetadata metadata = ResourceMetadata.EMPTY;
    @Override
    public ResourceMetadata metadata() {
        return metadata;
    }

    @Override
    public void setMetadata(ResourceMetadata metadata) {
        this.metadata = metadata;
    }
    *///?} else if <1.21.9 {
    /*@Shadow public abstract ResourceMetadata metadata();
    *///?} else {
    @Shadow public abstract <T> Optional<T> getAdditionalMetadata(MetadataSectionType<T> par1);
    //?}

    @Shadow NativeImage[] byMipLevel;

    @Shadow public abstract ResourceLocation name();


    @Inject(method = "increaseMipLevel", at = @At("RETURN"))
    public void increaseMipLevel(int i, CallbackInfo ci) {
        if (!FactoryOptions.MANUAL_MIPMAP.get()) return;

        //? if >=1.21.9 {
        MipmapMetadataSection manualMipmap = getAdditionalMetadata(MipmapMetadataSection.TYPE).orElseGet(()->MipmapMetadataSection.createFallback((SpriteContents) (Object) this, i));
        //?} else {
        /*MipmapMetadataSection manualMipmap = metadata().getSection(MipmapMetadataSection.TYPE).orElseGet(()->MipmapMetadataSection.createFallback((SpriteContents) (Object) this, i));
        *///?}
        NativeImage original = byMipLevel[0];
        for (Map.Entry<Integer, MipmapMetadataSection.Level> entry : manualMipmap.levels().entrySet()) {
            if (entry.getKey() > i) break;
            NativeImage image = entry.getValue().image();
            int divisor = (int) Math.pow(2, entry.getKey());
            int width = original.getWidth() / divisor;
            int height = original.getHeight() / divisor;
            if (image == null) {
                FactoryAPI.LOGGER.error("Failed to replace generated mipmap from texture {}: {} failed to load", name(), entry.getValue().texture());
            } else if (image.getWidth() != width || image.getHeight() != height) {
                FactoryAPI.LOGGER.error("Failed to replace generated mipmap from texture {}: {} has an incorrect size, it should be {}x{}", name(), entry.getValue().texture(), width, height);
            } else byMipLevel[entry.getKey()] = image;
        }
    }
}
