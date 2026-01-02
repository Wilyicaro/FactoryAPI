package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Font.class)
public interface FontAccessor {
    //? if <1.21.9 {
    /*@Invoker("getFontSet")
    FontSet getDefaultFontSet(ResourceLocation arg);
    @Accessor
    boolean getFilterFishyGlyphs();
    *///?} else {
    @Invoker("getGlyph")
    BakedGlyph getBakedGlyph(int i, Style style);
    //?}
}
