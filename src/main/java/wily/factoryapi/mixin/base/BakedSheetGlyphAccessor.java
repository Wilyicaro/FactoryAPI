//? if >=1.21.9 {
package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BakedSheetGlyph.class)
public interface BakedSheetGlyphAccessor {

    @Accessor("up")
    float getTop();

    @Accessor("down")
    float getBottom();
}
//?}
