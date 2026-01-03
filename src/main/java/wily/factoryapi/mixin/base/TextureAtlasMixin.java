package wily.factoryapi.mixin.base;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryGuiGraphics;

import java.util.Map;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin implements FactoryGuiGraphics.AtlasAccessor {
    @Shadow private Map<Identifier, TextureAtlasSprite> texturesByName;
    //? if <=1.20.1 {
    /*@Shadow @Final
    private Identifier location;
    @Unique
    private TextureAtlasSprite missingSprite;
    @Inject(method="upload", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;texturesByName:Ljava/util/Map;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void init(SpriteLoader.Preparations preparations, CallbackInfo ci){
        this.missingSprite = this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
        if (this.missingSprite == null) {
            throw new IllegalStateException("Atlas '" + this.location + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
        }
    }
    @Inject(method = "getSprite", at = @At("HEAD"), cancellable = true)
    public void getSprite(Identifier resourceLocation, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        TextureAtlasSprite textureAtlasSprite = this.texturesByName.getOrDefault(resourceLocation, this.missingSprite);
        if (textureAtlasSprite == null) throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
        cir.setReturnValue(textureAtlasSprite);
    }
    *///?}
    @Override
    public Map<Identifier, TextureAtlasSprite> getTexturesByName() {
        return texturesByName;
    }
}
