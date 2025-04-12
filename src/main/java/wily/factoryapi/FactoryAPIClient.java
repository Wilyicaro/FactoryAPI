package wily.factoryapi;

//? if >=1.21 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.client.DeltaTracker;
*///?}
import com.mojang.serialization.JsonOps;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
//? if >1.21.4 {
/*import net.minecraft.client.renderer.block.model.BlockStateModel;
*///?} else {
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
//?}
//? if <1.21.4 {
import net.minecraft.client.color.item.ItemColor;
//?}
//? if >=1.21.2 {
/*import net.minecraft.util.profiling.Profiler;
 *///?}
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import wily.factoryapi.base.FactoryExtraMenuSupplier;
import wily.factoryapi.base.client.screen.FactoryConfigScreen;
import wily.factoryapi.base.network.*;
import wily.factoryapi.mixin.base.MenuScreensAccessor;
import wily.factoryapi.mixin.base.MenuTypeAccessor;
//? if >=1.21.2 {
/*//? if <1.21.4 {
import net.minecraft.world.item.equipment.EquipmentModel;
 //?} else {
/^import net.minecraft.client.resources.model.EquipmentClientInfo;
import wily.factoryapi.mixin.base.SpecialModelRenderersAccessor;
^///?}
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.gui.components.toasts.ToastManager;
*///?} else {
import net.minecraft.client.gui.components.toasts.ToastComponent;
//?}
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
//? if fabric {
import wily.factoryapi.base.compat.client.FactoryAPIModMenuCompat;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?} else if forge {
/*import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
*///?} else if neoforge {
/*import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModList;
//? if <1.20.5 {
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
//?} else {
/^import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
^///?}
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForge;
*///?}
import org.jetbrains.annotations.Nullable;
//? if <=1.20.1 {
/*import wily.factoryapi.base.client.GuiSpriteManager;
*///?}
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.client.*;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.client.UIDefinition;
import wily.factoryapi.util.DynamicUtil;
import wily.factoryapi.util.FluidInstance;
import wily.factoryapi.util.FluidRenderUtil;import wily.factoryapi.util.ModInfo;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class FactoryAPIClient {
    public static final ResourceLocation BLOCK_ATLAS = FactoryAPI.createVanillaLocation("textures/atlas/blocks.png");
    public static final SecureExecutor SECURE_EXECUTOR = new SecureExecutor() {
        @Override
        public boolean isSecure() {
            return Minecraft.getInstance().player != null;
        }
    };
    private static final Map<String, Function<Screen,Screen>> defaultConfigScreens = new HashMap<>();

    public static UIDefinitionManager uiDefinitionManager;

    public static final Map<ResourceLocation, ExtraModelId> extraModels = new HashMap<>();

    private static final Set<String> playerMods = new HashSet<>();

    //? if <=1.20.1 {
    /*public static GuiSpriteManager sprites;
    *///?}

    public interface PlayerEvent extends Consumer<LocalPlayer>{
        FactoryEvent<PlayerEvent> JOIN_EVENT = new FactoryEvent<>(e-> (s -> e.invokeAll(t-> t.accept(s))));
        FactoryEvent<PlayerEvent> DISCONNECTED_EVENT = new FactoryEvent<>(e-> (s -> e.invokeAll(t-> t.accept(s))));
    }

    //? if >=1.21 {
    /*public static DeltaTracker getDeltaTracker(){
        return Minecraft.getInstance()./^? if <1.21.2 {^/getTimer/^?} else {^//^getDeltaTracker^//^?}^/();
    }
    *///?}
    //? if <1.21.5 {
    public static BakedModel getExtraModel(ResourceLocation resourceLocation){
        return Minecraft.getInstance().getModelManager().getModel(extraModels.get(resourceLocation).modelId());
    }
    //?} else {
    /*public static BlockStateModel getExtraModel(ResourceLocation resourceLocation){
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(extraModels.get(resourceLocation).blockState());
    }
    *///?}

    public static boolean hasAPIOnServer(){
        return hasModOnServer(FactoryAPI.MOD_ID);
    }

    public static boolean hasModOnServer(String id){
        return playerMods.contains(id);
    }

    public static boolean hasLevel(){
        return Minecraft.getInstance().level != null;
    }

    public static RegistryAccess getRegistryAccess(){
        return Minecraft.getInstance().level.registryAccess();
    }

    public static ProfilerFiller getProfiler(){
        return /*? if <1.21.2 {*/Minecraft.getInstance().getProfiler/*?} else {*//*Profiler.get*//*?}*/();
    }

    public static /*? if <1.21.2 {*/ToastComponent/*?} else {*//*ToastManager*//*?}*/getToasts(){
        return Minecraft.getInstance()./*? if <1.21.2 {*/getToasts/*?} else {*//*getToastManager*//*?}*/();
    }

    public static float getPartialTick(){
        return /*? if <1.20.5 {*/Minecraft.getInstance().getDeltaFrameTime()/*?} else {*//*FactoryAPIClient.getDeltaTracker().getRealtimeDeltaTicks()*//*?}*/;
    }

    public static float getGamePartialTick(boolean allowFrozen) {
        return /*? if <1.20.5 {*/(Minecraft.getInstance().isPaused() ? MinecraftAccessor.getInstance().getPausePartialTick() : /*? if >=1.20.3 {*/Minecraft.getInstance().level != null && Minecraft.getInstance().level.tickRateManager().runsNormally() || !allowFrozen ? Minecraft.getInstance().getFrameTime() : 1.0f/*?} else {*//*Minecraft.getInstance().getFrameTime()*//*?}*/)/*?} else {*/ /*FactoryAPIClient.getDeltaTracker().getGameTimeDeltaPartialTick(!allowFrozen)*//*?}*/;
    }

    //? if <1.21.2 {
    public static RecipeManager getRecipeManager(){
        return Minecraft.getInstance().level.getRecipeManager();
    }
    //?}

    public static Level getLevel(){
        return Minecraft.getInstance().level;
    }

    public static void init() {
        registerConfigScreen(FactoryAPIPlatform.getModInfo(FactoryAPI.MOD_ID), FactoryConfigScreen::createFactoryAPIConfigScreen);

        FactoryEvent.registerReloadListener(PackType.CLIENT_RESOURCES, uiDefinitionManager = new UIDefinitionManager());
        setup(m->{
            FactoryOptions.CLIENT_STORAGE.load();
        });
        preTick(m-> SECURE_EXECUTOR.executeAll());
        registerGuiPostRender(((guiGraphics, partialTicks) -> UIAccessor.of(Minecraft.getInstance().gui).getChildrenRenderables().forEach(r->r.render(guiGraphics,0,0,/*? if >=1.21 {*/ /*partialTicks.getGameTimeDeltaPartialTick(true)*//*?} else {*/ partialTicks /*?}*/))));
        PlayerEvent.JOIN_EVENT.register(l->{
            DynamicUtil.REGISTRY_OPS_CACHE.invalidateAll();
            DynamicUtil.DYNAMIC_ITEMS_CACHE.asMap().keySet().forEach(DynamicUtil.DYNAMIC_ITEMS_CACHE::refresh);
        });
        PlayerEvent.DISCONNECTED_EVENT.register(l->{
            DynamicUtil.REGISTRY_OPS_CACHE.invalidateAll();
            DynamicUtil.DYNAMIC_ITEMS_CACHE.asMap().keySet().forEach(DynamicUtil.DYNAMIC_ITEMS_CACHE::refresh);
            if (hasAPIOnServer()) FactoryConfig.COMMON_STORAGES.values().forEach(c-> {
                if (c.isServerOnly()) c.reset();
                else if (c.allowSync()) c.load();
            });
            playerMods.clear();
            //? if >=1.21.2 {
            /*CommonRecipeManager.clearRecipes();
            *///?}
        });
        //? if fabric {
        IFactoryItemClientExtension.map.forEach((i,c)-> ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel)-> c.getHumanoidArmorModel(entity,stack,slot,contextModel).renderToBuffer(matrices,vertexConsumers.getBuffer(RenderType.entityCutout(((IFactoryItem) i).getArmorLocation(stack,/*? if <1.21.2 {*/ entity, /*?}*/slot))), light, OverlayTexture.NO_OVERLAY/*? if <=1.20.6 {*/, 1.0F,1.0F,1.0F, 1.0F/*?}*/),i));
        if (FactoryAPI.isModLoaded("modmenu")) FactoryAPIModMenuCompat.init();
        //?} else if neoforge && >=1.20.5 {
        /*FactoryAPIPlatform.getModEventBus().addListener(RegisterClientExtensionsEvent.class,r->IFactoryItemClientExtension.map.forEach((i,c)->r.registerItem(new IClientItemExtensions() {
            @Override
            //? if <1.21.2 {
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return c.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
            }
            //?} else {
            /^public Model getHumanoidArmorModel(ItemStack itemStack, /^¹? if <1.21.4 {¹^/ EquipmentModel.LayerType/^¹?} else {¹^//^¹EquipmentClientInfo.LayerType¹^//^¹?}¹^/ layerType, Model original) {
                return c.getHumanoidArmorModel(itemStack, layerType, original);
            }
            ^///?}
        }, i)));
        *///?}
        //? if >=1.21.4 {
        /*IFactoryItemClientExtension.map.forEach(((item, iFactoryItemClientExtension) -> {
            IFactoryBlockEntityWLRenderer renderer = iFactoryItemClientExtension.getCustomRenderer();
            if (renderer == null) return;
            SpecialModelRenderersAccessor.getIdMapper().put(BuiltInRegistries.ITEM.getKey(item), renderer.createUnbakedCodec());
        }));
        *///?}
    }

    public static final FactoryEvent<Consumer<Minecraft>> STOPPING = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));
    public static final FactoryEvent<Consumer<Minecraft>> RESIZE_DISPLAY = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));

    //? if fabric {
    public static <T extends CommonNetwork.Payload> void registerPayload(CommonNetwork.Identifier<T> id){
        //? <1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(id.location(), (m, l, b, s) -> id.decode(b).applyClient());
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(id.type(), (payload, context) -> payload.applyClient());
        *///?}
    }
    //?} else if forge || neoforge {
    /*public static void registerReloadListener(PreparableReloadListener reloadListener){
        //? if >=1.21.4 && neoforge {
        /^FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, AddClientReloadListenersEvent.class, e-> e.addListener(FactoryAPI.createLocation(reloadListener.getName()), reloadListener));
        ^///?} else {
        ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(reloadListener);
        //?}
    }
    *///?}

    public static <T extends AbstractContainerMenu> void handleExtraMenu(SecureExecutor executor, Player player, MenuType<T> menuType, OpenExtraMenuPayload payload){
        var menu = ((MenuTypeAccessor)menuType).getConstructor() instanceof FactoryExtraMenuSupplier<?> supplier ? (T) supplier.create(payload.menuId(), player.getInventory(), payload.extra()) : menuType.create(payload.menuId(), player.getInventory());
        player.containerMenu = menu;
        executor.execute(()-> Minecraft.getInstance().setScreen(MenuScreensAccessor.getConstructor(menuType).create(menu, player.getInventory(), payload.component())));
    }

    public static void setup(Consumer<Minecraft> listener) {
        //? if fabric {
        ClientLifecycleEvents.CLIENT_STARTED.register(listener::accept);
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, FMLClientSetupEvent.class, e-> listener.accept(Minecraft.getInstance()));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void preTick(Consumer<Minecraft> listener) {
        //? if fabric {
        ClientTickEvents.START_CLIENT_TICK.register(listener::accept);
        //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, /^? if <1.21 {^/TickEvent.ClientTickEvent/^?} else {^//^TickEvent.ClientTickEvent.Pre^//^?}^/.class, e-> {
            if (e.phase == TickEvent.Phase.START) listener.accept(Minecraft.getInstance());
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(/^? if <1.20.5 {^/ TickEvent.ClientTickEvent.class/^?} else {^//^ClientTickEvent.Pre.class^//^?}^/, e-> {
            /^? if <1.20.5 {^/if (e.phase == TickEvent.Phase.START)/^?}^/ listener.accept(Minecraft.getInstance());
        });
         *///?} else
        /*throw new AssertionError();*/
    }

    public static void postTick(Consumer<Minecraft> listener) {
        //? if fabric {
        ClientTickEvents.END_CLIENT_TICK.register(listener::accept);
        //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, /^? if <1.21 {^/TickEvent.ClientTickEvent/^?} else {^//^TickEvent.ClientTickEvent.Post^//^?}^/.class, e-> {
            if (e.phase == TickEvent.Phase.END)  listener.accept(Minecraft.getInstance());
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(/^? if <1.20.5 {^/ TickEvent.ClientTickEvent.class/^?} else {^//^ClientTickEvent.Post.class^//^?}^/, e-> {
            /^? if <1.20.5 {^/if (e.phase == TickEvent.Phase.END)/^?}^/ listener.accept(Minecraft.getInstance());
        });
         *///?} else
        /*throw new AssertionError();*/
    }

    public static TextureAtlasSprite getFluidStillTexture(Fluid fluid) {
        //? if fabric {
        return FluidVariantRendering.getSprite(FluidVariant.of(fluid));
        //?} elif forge || neoforge {
        /*return Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid).getStillTexture());
        *///?} else
        /*throw new AssertionError();*/
    }

    public static TextureAtlasSprite getFluidFlowingTexture(Fluid fluid) {
        //? if fabric {
        return FluidVariantRendering.getSprites(FluidVariant.of(fluid))[1];
        //?} elif forge || neoforge {
        /*return Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid).getFlowingTexture());
        *///?} else
        /*throw new AssertionError();*/
    }

    public static int getFluidColor(Fluid fluid, BlockAndTintGetter view, BlockPos pos) {
        //? if fabric {
        return FluidVariantRendering.getColor(FluidVariant.of(fluid),view,pos);
        //?} elif forge || neoforge {
        /*return IClientFluidTypeExtensions.of(fluid).getTintColor(fluid.defaultFluidState(),view,pos);
        *///?} else
        /*throw new AssertionError();*/
    }

    public static int getFluidColor(FluidInstance fluid) {
        //? if fabric {
        return FluidVariantRendering.getColor(fluid.toVariant(), null, null);
         //?} elif forge || neoforge {
        /*return IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid.toStack());
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerKeyMapping(Consumer<Consumer<KeyMapping>> registry) {
        //? if fabric {
        registry.accept(KeyBindingHelper::registerKeyBinding);
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, RegisterKeyMappingsEvent.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Screen getConfigScreen(ModInfo mod, Screen screen) {
        if (defaultConfigScreens.containsKey(mod.getId())) return defaultConfigScreens.get(mod.getId()).apply(screen);
        //? if fabric {
        return FactoryAPI.isModLoaded("modmenu") ? FactoryAPIModMenuCompat.getConfigScreen(mod.getId(),screen) : null;
        //?} else if forge || neoforge && <1.20.5 {
        /*return ModList.get().getModContainerById(mod.getId()).flatMap(c-> ConfigScreenHandler.getScreenFactoryFor(c.getModInfo())).map(s-> s.apply(Minecraft.getInstance(), screen)).orElse(null);
        *///?} else if neoforge {
        /*return ModList.get().getModContainerById(mod.getId()).flatMap(m-> IConfigScreenFactory.getForMod(m.getModInfo()).map(s -> s.createScreen(m, screen))).orElse(null);
         *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerDefaultConfigScreen(String modId, Function<Screen,Screen> configScreenFactory) {
        defaultConfigScreens.put(modId, configScreenFactory);
    }

    public static void registerConfigScreen(ModInfo mod, Function<Screen,Screen> configScreenFactory) {
        registerDefaultConfigScreen(mod.getId(), configScreenFactory);
        //? if fabric {
        if (FactoryAPI.isModLoaded("modmenu")) setup(m-> FactoryAPIModMenuCompat.registerConfigScreen(mod.getId(), configScreenFactory));
         //?} else if forge || neoforge && <1.20.5 {
        /*ModList.get().getModContainerById(mod.getId()).ifPresent(c->c.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ()-> new ConfigScreenHandler.ConfigScreenFactory((m,s)->configScreenFactory.apply(s))));
        *///?} else if neoforge {
        /*ModList.get().getModContainerById(mod.getId()).ifPresent(c->c.registerExtensionPoint(IConfigScreenFactory.class, (m,s)-> configScreenFactory.apply(s)));
         *///?}
    }

    public interface MenuScreenRegister{
        <H extends AbstractContainerMenu, S extends Screen & MenuAccess<H>> void register(MenuType<? extends H> type, MenuScreens.ScreenConstructor<H, S> factory);
    }

    public static void registerMenuScreen(Consumer<MenuScreenRegister> registry) {
        //? if fabric {
        registry.accept(MenuScreens::register);;
        //?} elif forge || <1.20.4 && neoforge {
        /*setup(m-> registry.accept(MenuScreens::register));
        *///?} elif neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(RegisterMenuScreensEvent.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerGuiPostRender(BiConsumer<GuiGraphics,/*? if >=1.21 {*/ /*DeltaTracker *//*?} else {*/Float/*?}*/> registry) {
        //? if fabric {
        HudRenderCallback.EVENT.register(registry::accept);

        //?} elif forge {
        /*//? if <1.21 {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, RenderGuiEvent.Post.class, e-> registry.accept(e.getGuiGraphics(),e.getPartialTick()));
        //?} else {
        /^MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, CustomizeGuiOverlayEvent.class, e-> registry.accept(e.getGuiGraphics(), getDeltaTracker()));
        ^///?}
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(RenderGuiEvent.Post.class, e-> registry.accept(e.getGuiGraphics(),e.getPartialTick()));
         *///?} else
        /*throw new AssertionError();*/
    }


    public static void registerBlockColor(Consumer<BiConsumer<BlockColor, Block>> registry){
        //? if fabric {
        registry.accept(ColorProviderRegistry.BLOCK::register);
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, RegisterColorHandlersEvent.Block.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }

    //? if <1.21.4 {
    public static void registerItemColor(Consumer<BiConsumer<ItemColor, Item>> registry){
        //? if fabric {
        registry.accept(ColorProviderRegistry.ITEM::register);
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, RegisterColorHandlersEvent.Item.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }
    //?}

    public static void registerRenderType(RenderType renderType, Block... blocks) {
        //? if fabric {
        BlockRenderLayerMap.INSTANCE.putBlocks(renderType,blocks);
        //?} elif forge || neoforge {
        /*for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block,renderType);
        }
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerRenderType(RenderType renderType, Fluid... fluids) {
        //? if fabric {
        BlockRenderLayerMap.INSTANCE.putFluids(renderType,fluids);
        //?} elif forge || neoforge {
        /*for (Fluid fluid : fluids) {
            ItemBlockRenderTypes.setRenderLayer(fluid,renderType);
        }
        *///?} else
        /*throw new AssertionError();*/
    }

    public record ExtraModelId(StateDefinition<Block,BlockState> stateDefinition, BlockState blockState, ResourceLocation id/*? if <1.21.5 {*/, ModelResourceLocation modelId/*?}*/){
        public static ExtraModelId create(ResourceLocation id){
            StateDefinition<Block,BlockState> stateDefinition = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR).create(Block::defaultBlockState, BlockState::new);
            return new ExtraModelId(stateDefinition, stateDefinition.any(), id/*? if <1.21.5 {*/, BlockModelShaper.stateToModelLocation(id, stateDefinition.any())/*?}*/);
        }
    }

    public static void registerExtraModels(Consumer<Consumer<ResourceLocation>> registry){
        registry.accept(id-> extraModels.put(id, ExtraModelId.create(id)));
    }

    public static <T extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider){
        //? if fabric {
        EntityRendererRegistry.register(type.get(),provider);
        //?} else if forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, EntityRenderersEvent.RegisterRenderers.class, e-> e.registerEntityRenderer(type.get(),provider));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> definition){
        //? if fabric {
        EntityModelLayerRegistry.registerModelLayer(location,definition::get);
         //?} else if forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, EntityRenderersEvent.RegisterLayerDefinitions.class, e-> e.registerLayerDefinition(location,definition));
        *///?} else
        /*throw new AssertionError();*/
    }

    public interface FactoryRenderLayerRegistry {
        EntityRenderer<?/*? if >=1.21.2 {*//*, ? *//*?}*/> getEntityRenderer(EntityType<? extends LivingEntity> entityType);
        EntityModelSet getEntityModelSet();
        <T extends  /*? if >=1.21.2 {*/ /*LivingEntityRenderState, S extends *//*?}*/LivingEntity,M extends EntityModel<T>> void register(LivingEntityRenderer</*? if >=1.21.2 {*/ /*S, *//*?}*/T, M> renderer, RenderLayer<T, M> renderLayer);
    }

    public static void registerRenderLayer(Consumer<FactoryRenderLayerRegistry> registry){
        //? if fabric {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((a, b, c, d)-> registry.accept(new FactoryRenderLayerRegistry() {
            @Override
            public EntityRenderer<?/*? if >=1.21.2 {*//*, ? *//*?}*/> getEntityRenderer(EntityType<? extends LivingEntity> entityType) {
                return b;
            }

            @Override
            public EntityModelSet getEntityModelSet() {
                return d.getModelSet();
            }

            public <T extends  /*? if >=1.21.2 {*/ /*LivingEntityRenderState, S extends *//*?}*/LivingEntity,M extends EntityModel<T>> void register(LivingEntityRenderer</*? if >=1.21.2 {*/ /*S, *//*?}*/T, M> renderer, RenderLayer<T, M> renderLayer){
                c.register(renderLayer);
            }

            }));
         //?} else if forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, EntityRenderersEvent.AddLayers.class, e-> registry.accept(new FactoryRenderLayerRegistry() {
            @Override
            public EntityRenderer<?/^? if >=1.21.2 {^//^, ? ^//^?}^/> getEntityRenderer(EntityType<? extends LivingEntity> entityType) {
                return e./^? if >=1.20.2 && forge {^//^getEntityRenderer^//^?} else {^/getRenderer/^?}^/(entityType);
            }

            @Override
            public EntityModelSet getEntityModelSet() {
                return e.getEntityModels();
            }

            @Override
            public <T extends  /^? if >=1.21.2 {^/ /^LivingEntityRenderState, S extends ^//^?}^/LivingEntity,M extends EntityModel<T>> void register(LivingEntityRenderer</^? if >=1.21.2 {^/ /^S, ^//^?}^/T, M> renderer, RenderLayer<T, M> renderLayer){
                renderer.addLayer(renderLayer);
            }
        }));
        *///?} else
        /*throw new AssertionError();*/

    }

    public static void handleHelloPayload(HelloPayload payload){
        playerMods.addAll(payload.modIds());
    }
}
