//? if >=1.21.4 {
/*package wily.factoryapi.mixin.base;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTintSources.class)
public interface ItemTintSourcesAccessor {
    @Accessor("ID_MAPPER")
    static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends ItemTintSource>> getIdMapper() {
        return null;
    }
}
*///?}
