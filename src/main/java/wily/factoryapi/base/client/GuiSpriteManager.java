//? if <=1.20.1 {
/*package wily.factoryapi.base.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import java.util.Set;


public class GuiSpriteManager extends TextureAtlasHolder {
    public static final Set<MetadataSectionSerializer<?>> GUI_METADATA_SECTIONS = Set.of(AnimationMetadataSection.SERIALIZER, GuiMetadataSection.TYPE);
    public static final Set<MetadataSectionSerializer<?>> DEFAULT_METADATA_SECTIONS = Set.of(AnimationMetadataSection.SERIALIZER);

    public GuiSpriteManager(TextureManager arg) {
        super(arg, new Identifier("textures/atlas/gui.png"), new Identifier("gui"));
    }

    @Override
    public TextureAtlasSprite getSprite(Identifier resourceLocation) {
        return super.getSprite(resourceLocation);
    }

    public GuiSpriteScaling getSpriteScaling(TextureAtlasSprite arg) {
        return this.getMetadata(arg).scaling();
    }

    private GuiMetadataSection getMetadata(TextureAtlasSprite arg) {
        return ((FactorySpriteContents)arg.contents()).metadata().getSection(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT);
    }

}
*///?}