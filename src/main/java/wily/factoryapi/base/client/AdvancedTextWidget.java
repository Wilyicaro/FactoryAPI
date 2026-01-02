package wily.factoryapi.base.client;

import net.minecraft.client.Minecraft;
//? if >=1.21.11 {
/*import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.network.chat.Style;
import wily.factoryapi.mixin.base.ScreenAccessor;
*///?}
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import wily.factoryapi.mixin.base.BakedSheetGlyphAccessor;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.mixin.base.FontAccessor;
import wily.factoryapi.util.FactoryScreenUtil;

import java.util.Collections;
import java.util.List;

public class AdvancedTextWidget extends SimpleLayoutRenderable implements GuiEventListener, NarratableEntry {
    private final UIAccessor accessor;
    private List<FormattedCharSequence> lines = Collections.emptyList();
    private int[] widthPerLine = new int[0];
    private int[] heightPerLine = new int[0];
    private int lineSpacing = 12;
    private boolean centered = false;
    private int color = 0xFFFFFFFF;
    private boolean shadow = true;
    private boolean multipleHeights = true;

    public AdvancedTextWidget(UIAccessor accessor){
        this.accessor = accessor;
    }

    public AdvancedTextWidget withLines(List<FormattedCharSequence> lines){
        if (lines != null) {
            this.lines = lines;
            processLines();
        }
        return this;
    }

    public void processLines(){
        height = 0;
        widthPerLine = new int[lines.size()];
        heightPerLine = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence sequence = lines.get(i);
            widthPerLine[i] = Minecraft.getInstance().font.width(sequence);
            heightPerLine[i] = multipleHeights ? Math.max(lineSpacing, Math.max(getLineHeight(sequence) - 9, 0) + lineSpacing) : lineSpacing;
            height+= heightPerLine[i];
        }
    }

    public AdvancedTextWidget withLines(Component component, int width){
        return withWidth(width).withLines(Minecraft.getInstance().font.split(component, width));
    }

    public static int getLineHeight(FormattedCharSequence sequence){
        Bearer<Integer> bearer = Bearer.of(0);
        sequence.accept((n, style, pos)->{
            FontAccessor fontAccessor = (FontAccessor) Minecraft.getInstance().font;
            //? if >=1.21.9 {
            /*BakedGlyph glyph = fontAccessor.getBakedGlyph(pos, style);
            if (glyph instanceof BakedSheetGlyphAccessor sheetGlyph) {
                int height = Math.round(sheetGlyph.getBottom() - sheetGlyph.getTop());
                if (height > bearer.get()) bearer.set(height);
            } else if (glyph.info().getAdvance() > bearer.get()) bearer.set(Math.round(glyph.info().getAdvance()));
            *///?} else {
            fontAccessor.getDefaultFontSet(style.getFont()).getGlyphInfo(pos, fontAccessor.getFilterFishyGlyphs()).bake(sheetGlyphInfo-> {
                int height = Math.round(sheetGlyphInfo.getPixelHeight() / sheetGlyphInfo.getOversample());
                if (height > bearer.get()) bearer.set(height);
                return null;
            });
            //?}
            return true;
        });
        return bearer.get();
    }

    public AdvancedTextWidget lineSpacing(int lineSpacing){
        this.lineSpacing = lineSpacing;
        return this;
    }

    public AdvancedTextWidget centered(boolean centered){
        this.centered = centered;
        return this;
    }

    public AdvancedTextWidget multipleHeights(boolean multipleHeights){
        this.multipleHeights = multipleHeights;
        return this;
    }

    public AdvancedTextWidget withPos(int x, int y){
        setPosition(x, y);
        return this;
    }

    public AdvancedTextWidget withWidth(int width){
        this.width = width;
        return this;
    }

    public AdvancedTextWidget withColor(int color){
        this.color = color;
        return this;
    }

    public AdvancedTextWidget withShadow(boolean shadow){
        this.shadow = shadow;
        return this;
    }

    public List<FormattedCharSequence> getLines(){
        return lines;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        int actualHeight = getY();
        for (int i1 = 0; i1 < lines.size(); i1++) {
            int lineHeight = heightPerLine[i1];
            guiGraphics.drawString(Minecraft.getInstance().font, lines.get(i1), getX() + (centered ? (width - widthPerLine[i1]) / 2 : 0), actualHeight + (lineHeight - lineSpacing) / 2, color, shadow);
            actualHeight += lineHeight;
        }
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (handleComponentsClicked(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button()))
            return true;
        return GuiEventListener.super.mouseClicked(mouseButtonEvent, bl);
    }
    *///?} else {
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (handleComponentsClicked(d, e, i))
            return true;
        return GuiEventListener.super.mouseClicked(d, e, i);
    }
    //?}

    public boolean handleComponentsClicked(double d, double e, int i) {
        if (accessor.getScreen() != null && isMouseOver(d,e)){
            if (i == 0) {
                int actualHeight = getY();
                for (int i1 = 0; i1 < lines.size(); i1++) {
                    int lineHeight = heightPerLine[i1];
                    if (e >= actualHeight && e < actualHeight + lineHeight){
                        //? if >=1.21.11 {
                        /*ActiveTextCollector.ClickableStyleFinder clickableStyleFinder = new ActiveTextCollector.ClickableStyleFinder(Minecraft.getInstance().font, (int) d, (int) e);
                        clickableStyleFinder.accept(0, Mth.floor(d - getX()), lines.get(i1)); // TODO WHAT WHAT WHAT WHAT WHAT
                        Style style = clickableStyleFinder.result();
                        if (style != null) ScreenAccessor.callDefaultHandleGameClickEvent(style.getClickEvent(), Minecraft.getInstance(), accessor.getScreen());
                        *///?} else {
                        accessor.getScreen().handleComponentClicked(Minecraft.getInstance().font.getSplitter().componentStyleAtWidth(lines.get(i1), Mth.floor(d - getX())));
                        //?}
                        return true;
                    }
                    actualHeight += lineHeight;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return FactoryScreenUtil.isMouseOver(d, e, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void setFocused(boolean bl) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return super.getRectangle();
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput arg) {
    }
}
