package wily.factoryapi.mixin.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.renderer.texture.SpriteContents;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.20.1 {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactorySpriteContents;
import wily.factoryapi.base.client.GuiMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
*///?}
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.MipmapMetadataSection;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    @Shadow @Final private Identifier location;

    //? if <=1.20.1 {
    /*@Inject(method = "loadSprite", at = @At("RETURN"))
    private static void loadSprite(Identifier resourceLocation, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
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
    *///?} else if <1.21.9 {
    /*@Inject(method = "loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/Identifier;ILjava/util/concurrent/Executor;Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "HEAD"))
    public void loadAndStitch(CallbackInfoReturnable<CompletableFuture<SpriteLoader.Preparations>> cir, @Local(argsOnly = true) LocalRef<Collection<MetadataSectionType<?>>> types) {
        if (location.equals(FactoryAPIClient.BLOCK_ATLAS_ID))
            types.set(ImmutableSet.<MetadataSectionType<?>>builder().addAll(types.get()).add(MipmapMetadataSection.TYPE).build());
    }
    *///?} else {
    @Inject(method = "loadAndStitch", at = @At(value = "HEAD"))
    public void loadAndStitch(CallbackInfoReturnable<CompletableFuture<SpriteLoader.Preparations>> cir, @Local(argsOnly = true) LocalRef<Set<MetadataSectionType<?>>> types) {
        if (location.equals(FactoryAPIClient.BLOCK_ATLAS_ID))
            types.set(ImmutableSet.<MetadataSectionType<?>>builder().addAll(types.get()).add(MipmapMetadataSection.TYPE).build());
    }
    //?}
}

