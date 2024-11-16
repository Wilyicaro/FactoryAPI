package wily.factoryapi.mixin.common;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import wily.factoryapi.base.FactoryGuiGraphics;

import java.util.Map;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin implements FactoryGuiGraphics.AtlasAccessor {
    @Shadow private Map<ResourceLocation, TextureAtlasSprite> texturesByName;

    @Override
    public Map<ResourceLocation, TextureAtlasSprite> getTexturesByName() {
        return texturesByName;
    }
}
