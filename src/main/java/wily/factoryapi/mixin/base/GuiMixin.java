package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.client.GuiAccessor;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.base.client.UIDefinition;
import wily.factoryapi.util.FactoryGuiElement;
import wily.factoryapi.util.VariablesMap;
//? if >=1.21 {
import net.minecraft.client.DeltaTracker;
//?}

import java.util.*;

@Mixin(Gui.class)
public abstract class GuiMixin implements UIAccessor, GuiAccessor {
    @Shadow private int overlayMessageTime;

    @Shadow @Nullable private Component overlayMessageString;

    @Unique private final List<Renderable> renderables = new ArrayList<>();
    @Unique private final VariablesMap<String, ArbitrarySupplier<?>> elements = new VariablesMap<>();
    @Unique private final List<UIDefinition> definitions = new ArrayList<>();
    @Unique private final List<UIDefinition> staticDefinitions = new ArrayList<>();
    @Override
    public @Nullable Screen getScreen() {
        return null;
    }

    @Override
    public boolean initialized() {
        return true;
    }

    @Override
    public List<UIDefinition> getDefinitions() {
        return definitions;
    }

    @Override
    public List<UIDefinition> getStaticDefinitions() {
        return staticDefinitions;
    }

    @Override
    public List<GuiEventListener> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public List<Renderable> getChildrenRenderables() {
        return renderables;
    }

    @Override
    public <T extends GuiEventListener> T removeChild(T listener) {
        if (listener instanceof Renderable r) renderables.remove(r);
        return listener;
    }

    @Override
    public <T extends GuiEventListener> T addChild(int renderableIndex, T listener, boolean isRenderable, boolean isNarratable) {
        if (listener instanceof Renderable r) addRenderable(renderableIndex,r);
        return listener;
    }

    @Override
    public void beforeInit(UIAccessor accessor) {
        UIAccessor.super.beforeInit(accessor);
        putSupplierComponent("overlayMessage.component", ()->overlayMessageString == null ? CommonComponents.EMPTY : overlayMessageString);
        getElements().put("overlayMessage.time", Bearer.of(()->overlayMessageTime, i-> overlayMessageTime = i));
    }

    @Override
    public VariablesMap<String, ArbitrarySupplier<?>> getElements() {
        return elements;
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void beforeTick(CallbackInfo ci) {
        beforeTick();
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    public void afterTick(CallbackInfo ci) {
        afterTick();
    }

    @Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
    public void renderVignette(GuiGraphics guiGraphics, Entity entity, CallbackInfo ci) {
        FactoryGuiElement.VIGNETTE.prepareMixin(guiGraphics, this, ci);
    }

    @Inject(method = "renderVignette", at = @At("RETURN"))
    public void renderVignetteReturn(GuiGraphics guiGraphics, Entity entity, CallbackInfo ci) {
        FactoryGuiElement.VIGNETTE.finalizeMixin(guiGraphics, this);
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void renderCrosshair(GuiGraphics guiGraphics/*? if >=1.21 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiElement.CROSSHAIR.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    public void renderCrosshairReturn(GuiGraphics guiGraphics/*? if >=1.21 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiElement.CROSSHAIR.finalizeMixin(guiGraphics, this);
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    public void renderEffects(GuiGraphics guiGraphics/*? if >=1.21 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiElement.EFFECTS.prepareMixin(guiGraphics, this, ci);
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    public void renderEffectsReturn(GuiGraphics guiGraphics/*? if >=1.21 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiElement.EFFECTS.finalizeMixin(guiGraphics, this);
    }

    @Inject(method = /*? if >=1.20.5 {*/"renderItemHotbar"/*?} else {*//*"renderHotbar"*//*?}*/, at = @At("HEAD"), cancellable = true)
    public void renderHotbar(/*? if <1.20.5 {*//*float f, *//*?}*/GuiGraphics guiGraphics/*? if >=1.20.5 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiElement.HOTBAR.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = /*? if >=1.20.5 {*/"renderItemHotbar"/*?} else {*//*"renderHotbar"*//*?}*/, at = @At("RETURN"))
    public void renderHotbarReturn(/*? if <1.20.5 {*//*float f, *//*?}*/GuiGraphics guiGraphics/*? if >=1.21 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiElement.HOTBAR.finalizeMixin(guiGraphics, this);
    }
    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void displayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        FactoryGuiElement.SCOREBOARD.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
    private void displayScoreboardSidebarReturn(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        FactoryGuiElement.SCOREBOARD.finalizeMixin(guiGraphics, this);
    }
    //? if >=1.20.5 {
    @Inject(method = "renderOverlayMessage", at = @At(value = "HEAD"), cancellable = true)
    public void renderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        FactoryGuiElement.OVERLAY_MESSAGE.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = "renderOverlayMessage", at = @At(value = "RETURN"))
    public void renderOverlayMessageReturn(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        FactoryGuiElement.OVERLAY_MESSAGE.finalizeMixin(guiGraphics, this);
    }
    //?} else if fabric {

    /*@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;overlayMessageTime:I", ordinal = 0, opcode = Opcodes.GETFIELD))
    public int renderOverlayMessage(Gui instance) {
        if (!FactoryGuiElement.OVERLAY_MESSAGE.isVisible(this)) return 0;
        return overlayMessageTime;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0))
    public void renderOverlayMessage(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (FactoryGuiElement.OVERLAY_MESSAGE.isVisible(this)) FactoryGuiElement.OVERLAY_MESSAGE.prepareRender(guiGraphics, this);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 0, shift = At.Shift.AFTER))
    public void renderOverlayMessageReturn(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        FactoryGuiElement.OVERLAY_MESSAGE.finalizeMixin(guiGraphics, this);
    }
    *///?}
    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    public void renderHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = "renderPlayerHealth", at = @At("RETURN"))
    public void renderHealthReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.finalizeMixin(guiGraphics, this);
    }
    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    public void renderVehicleHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.VEHICLE_HEALTH.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = "renderVehicleHealth", at = @At("RETURN"))
    public void renderVehicleHealthReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.VEHICLE_HEALTH.finalizeMixin(guiGraphics, this);
    }
    //? if >=1.20.5 && neoforge {
    /*@Inject(method = {"renderHealthLevel","renderArmorLevel","renderFoodLevel","renderAirLevel"}, at = @At("HEAD"), cancellable = true, remap = false)
    public void renderNeoForgeHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
       FactoryGuiElement.PLAYER_HEALTH.prepareMixin(guiGraphics, this, ci);
    }
    @Inject(method = {"renderHealthLevel","renderArmorLevel","renderFoodLevel","renderAirLevel"}, at = @At("RETURN"), remap = false)
    public void renderNeoForgeHealthReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.finalizeMixin(guiGraphics, this);
    }
    *///?}

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true, require = 0)
    public void renderExperienceBar(GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        FactoryGuiElement.EXPERIENCE_BAR.prepareMixin(guiGraphics, this, ci);
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"), require = 0)
    public void renderExperienceBarReturn(GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        FactoryGuiElement.EXPERIENCE_BAR.finalizeMixin(guiGraphics, this);
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true, require = 0)
    public void renderJumpMeter(PlayerRideableJumping playerRideableJumping, GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        FactoryGuiElement.JUMP_METER.prepareMixin(guiGraphics, this, ci);
    }

    @Inject(method = "renderJumpMeter", at = @At("RETURN"), require = 0)
    public void renderJumpMeterReturn(PlayerRideableJumping playerRideableJumping, GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        FactoryGuiElement.JUMP_METER.finalizeMixin(guiGraphics, this);
    }

    @Inject(method = /*? if forge || neoforge {*/ /*"renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V" *//*?} else {*/"renderSelectedItemName"/*?}*/, at = @At("HEAD"), cancellable = true/*? if forge || neoforge {*//*, remap = false*//*?}*/)
    public void renderSelectedItemName(GuiGraphics guiGraphics, /*? if forge || neoforge {*/ /*int shift, *//*?}*/ CallbackInfo ci) {
        FactoryGuiElement.SELECTED_ITEM_NAME.prepareMixin(guiGraphics, this, ci);
    }

    @Inject(method = /*? if forge || neoforge {*/ /*"renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V" *//*?} else {*/"renderSelectedItemName"/*?}*/, at = @At("RETURN")/*? if forge || neoforge {*//*, remap = false*//*?}*/)
    public void renderSelectedItemNameReturn(GuiGraphics guiGraphics, /*? if forge || neoforge {*/ /*int shift, *//*?}*/ CallbackInfo ci) {
        FactoryGuiElement.SELECTED_ITEM_NAME.finalizeMixin(guiGraphics, this);
    }


    @Accessor
    public abstract SpectatorGui getSpectatorGui();

    @Accessor
    public abstract ItemStack getLastToolHighlight();

    @Accessor
    public abstract int getToolHighlightTimer();
}
