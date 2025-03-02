package wily.factoryapi.mixin.base;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.20.1 {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactorySpriteContents;
import wily.factoryapi.base.client.GuiMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
*///?}
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import wily.factoryapi.base.client.MipmapMetadataSection;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    //? if <=1.20.1 {
    /*@Inject(method = "loadSprite", at = @At("RETURN"))
    private static void loadSprite(ResourceLocation resourceLocation, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
        if (cir.getReturnValue() != null) {
            try {
                ResourceMetadata metadata = resource.metadata();
                Map<MetadataSectionSerializer<?>, Optional<?>> map = GuiMetadataSection.DEFAULT_TYPES.stream().collect(Collectors.toMap(t-> t, metadata::getSection));
                ((FactorySpriteContents) cir.getReturnValue()).setMetadata(new ResourceMetadata() {
                    @Override
                    public <T> Optional<T> getSection(MetadataSectionSerializer<T> metadataSectionSerializer) {
                        return (Optional<T>) map.getOrDefault(metadataSectionSerializer, Optional.empty());
                    }
                });
            } catch (IOException e) {
            }
        }
    }
    *///?} else {
    @ModifyArg(method = "loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/SpriteLoader;loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;"), index = 4)
    public Collection<MetadataSectionType<?>> loadAndStitch(Collection<MetadataSectionType<?>> collection) {
        return ImmutableList.<MetadataSectionType<?>>builder().addAll(collection).add(MipmapMetadataSection.TYPE).build();
    }
    //?}
}

