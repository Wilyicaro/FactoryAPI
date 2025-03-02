package wily.factoryapi.base.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.FilenameUtils;
import wily.factoryapi.FactoryAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MipmapMetadataSection(Map<Integer, Level> levels) {
    public static final Codec<MipmapMetadataSection> CODEC = Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Object::toString), Level.CODEC).xmap(MipmapMetadataSection::new, MipmapMetadataSection::levels);
    public static final MetadataSectionType<MipmapMetadataSection> TYPE = /*? if >1.21.3 {*//*new MetadataSectionType<>("mipmap", CODEC)*//*?} else if >1.20.1 {*/ MetadataSectionType.fromCodec("mipmap", CODEC)/*?} else {*//*GuiMetadataSection.fromCodec("mipmap", CODEC)*//*?}*/;
    public static final Pattern MANUAL_MIPMAP_PATTERN = Pattern.compile(".+/(\\d).png");

    public static MipmapMetadataSection createFallback(SpriteContents contents){
        MipmapMetadataSection section = new MipmapMetadataSection(new HashMap<>());
        String baseName = FilenameUtils.getBaseName(contents.name().getPath());
        if (!baseName.isEmpty()) {
            for (Map.Entry<ResourceLocation, Resource> entry : Minecraft.getInstance().getResourceManager().listResources(FactoryOptions.MANUAL_MIPMAP_PATH.get() + "/" + baseName, r -> r.getNamespace().equals(contents.name().getNamespace()) && r.getPath().endsWith(".png")).entrySet()) {
                Matcher matcher = MANUAL_MIPMAP_PATTERN.matcher(entry.getKey().getPath());
                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        try {
                            Integer level = Integer.parseInt(matcher.group(1));
                            section.levels().put(level, new Level(entry.getKey()));
                        } catch (NumberFormatException e) {
                            FactoryAPI.LOGGER.error("Failed to load fallback mipmap level from texture {}: {}", contents.name(), e.getMessage());
                        }
                    }
                }
            }
        }
        return section;
    }

    public record Level(ResourceLocation texture, NativeImage image){
        public Level(ResourceLocation texture){
            this(texture, readSecure(texture));
        }

        public static NativeImage readSecure(ResourceLocation texture){
            try {
                return NativeImage.read(Minecraft.getInstance().getResourceManager().open(texture));
            } catch (IOException e) {
                FactoryAPI.LOGGER.error("Failed to load mipmap level from texture {}: {}", texture, e.getMessage());
                return null;
            }
        }
        public static final Codec<Level> CODEC = ResourceLocation.CODEC.xmap(Level::new, Level::texture);
    }
}
