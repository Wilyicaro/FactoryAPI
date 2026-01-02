package wily.factoryapi.util;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryEvent;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.FactoryGuiMatrixStack;
import wily.factoryapi.base.client.UIAccessor;

public record FactoryGuiElement(String name, boolean isHud, FactoryEvent<GuiRender> pre, FactoryEvent<GuiRender> modifiedPre, FactoryEvent<GuiRender> post, FactoryEvent<GuiRender> modifiedPost) {
    public static final FactoryGuiElement HOTBAR = new FactoryGuiElement("hotbar");
    public static final FactoryGuiElement SPECTATOR_HOTBAR = new FactoryGuiElement("spectator_hotbar");
    public static final FactoryGuiElement EFFECTS = new FactoryGuiElement("effects",false);
    public static final FactoryGuiElement SCOREBOARD = new FactoryGuiElement("scoreboard",false);
    public static final FactoryGuiElement CROSSHAIR = new FactoryGuiElement("crosshair");
    public static final FactoryGuiElement OVERLAY_MESSAGE = new FactoryGuiElement("overlay_message");
    public static final FactoryGuiElement PLAYER_HEALTH = new FactoryGuiElement("player_health");
    public static final FactoryGuiElement VEHICLE_HEALTH = new FactoryGuiElement("vehicle_health");
    public static final FactoryGuiElement EXPERIENCE_BAR = new FactoryGuiElement("experience_bar");
    //? if >=1.21.6 {
    public static final FactoryGuiElement LOCATOR_BAR = new FactoryGuiElement("locator_bar");
    //?}
    public static final FactoryGuiElement JUMP_METER = new FactoryGuiElement("jump_meter");
    public static final FactoryGuiElement SELECTED_ITEM_NAME = new FactoryGuiElement("selected_item_name");
    public static final FactoryGuiElement SPECTATOR_TOOLTIP = new FactoryGuiElement("spectator_tooltip");
    public static final FactoryGuiElement BOSSHEALTH = new FactoryGuiElement("bosshealth",false);
    public static final FactoryGuiElement VIGNETTE = new FactoryGuiElement("vignette",false);

    public FactoryGuiElement(String name, boolean isHud){
        this(name, isHud, GuiRender.createEvent(), GuiRender.createEvent(), GuiRender.createEvent(), GuiRender.createEvent());
    }

    public FactoryGuiElement(String name){
        this(name, true);
    }

    public interface GuiRender {
        void render(GuiGraphics graphics);

        static FactoryEvent<GuiRender> createEvent(){
            return new FactoryEvent<>(e-> graphics -> e.invokeAll(guiRender -> guiRender.render(graphics)));
        }
    }

    public void prepareMixin(GuiGraphics graphics, CallbackInfo info){
        prepareMixin(graphics, FactoryScreenUtil.getGuiAccessor(), info);
    }

    public void finalizeMixin(GuiGraphics graphics){
        finalizeMixin(graphics, FactoryScreenUtil.getGuiAccessor());
    }

    public void prepareMixin(GuiGraphics graphics, UIAccessor accessor, CallbackInfo info){
        if (!isVisible(accessor)) {
            info.cancel();
            return;
        }
        prepareRender(graphics, accessor);
    }

    public void finalizeMixin(GuiGraphics graphics, UIAccessor accessor){
        if (!isVisible(accessor)) {
            return;
        }
        finalizeRender(graphics, accessor);
    }

    public float getOffset(String offsetName, UIAccessor accessor){
        return accessor.getFloat(name+"."+offsetName,0f) + (isHud() && accessor.getBoolean(name+".hud."+offsetName, true) ? accessor.getFloat("hud."+offsetName,0f) : 0);
    }

    public float getScale(String scaleName, UIAccessor accessor){
        return accessor.getFloat(name+"."+scaleName,1f) * (isHud() && accessor.getBoolean(name+".hud.scale", true) ? accessor.getFloat("hud."+scaleName,1f) : 1);
    }

    public int getColor(UIAccessor accessor){
        return ColorUtil.mergeColors(accessor.getInteger(name+".renderColor", -1), (isHud() && accessor.getBoolean(name+".hud.renderColor", true) ? accessor.getInteger("hud.renderColor", -1) : -1));
    }

    public void prepareRender(GuiGraphics graphics, UIAccessor accessor){
        pre.invoker.render(graphics);
        FactoryGuiMatrixStack.of(graphics.pose()).pushPose();
        FactoryScreenUtil.applyOffset(graphics, getOffset( "translateX", accessor), getOffset("translateY", accessor), getOffset("translateZ", accessor));
        FactoryScreenUtil.applyScale(graphics, getScale("scaleX", accessor), getScale("scaleY", accessor), getScale("scaleZ", accessor));
        FactoryScreenUtil.applyOffset(graphics, getOffset("scaledTranslateX", accessor), getOffset("scaledTranslateY", accessor), getOffset("scaledTranslateZ", accessor));
        FactoryScreenUtil.applyColor(graphics, getColor(accessor));
        modifiedPre.invoker.render(graphics);
    }

    public void finalizeRender(GuiGraphics graphics, UIAccessor accessor){
        modifiedPost.invoker.render(graphics);
        int color = getColor(accessor);
        FactoryGuiMatrixStack.of(graphics.pose()).popPose();
        //? if >=1.21.6 {
        if (color != -1) FactoryGuiGraphics.of(graphics).clearBlitColor();
         //?} else
        //if (color != -1) FactoryGuiGraphics.of(graphics).clearColor(true);
        post.invoker.render(graphics);
    }

    public boolean isVisible(UIAccessor accessor){
        return accessor.getBoolean(name+".isVisible", true) && accessor.getBoolean("isGuiVisible", true);
    }
}
