package wily.factoryapi.mixin.common;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if <=1.20.1 {
/*import wily.factoryapi.base.client.FactorySpriteContents;
import wily.factoryapi.base.client.GuiMetadataSection;
*///?}

import java.io.IOException;
import java.util.Optional;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {
    //? if <=1.20.1 {
    /*@Inject(method = "loadSprite", at = @At("RETURN"))
    private static void loadSprite(ResourceLocation resourceLocation, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
        if (cir.getReturnValue() != null) {
            try {
                GuiMetadataSection section = resource.metadata().getSection(GuiMetadataSection.TYPE).orElse(null);
                ((FactorySpriteContents) cir.getReturnValue()).setMetadata(new ResourceMetadata() {
                    @Override
                    public <T> Optional<T> getSection(MetadataSectionSerializer<T> metadataSectionSerializer) {
                        return metadataSectionSerializer == GuiMetadataSection.TYPE ? (Optional<T>) Optional.ofNullable(section) : Optional.empty();
                    }
                });
            } catch (IOException e) {
            }
        }
    }
    *///?}
}
