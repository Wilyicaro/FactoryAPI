package wily.factoryapi;

//? if >=1.21 {
/*import net.minecraft.client.DeltaTracker;
*///?}
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.2
/*import net.minecraft.world.item.equipment.EquipmentModel;*/
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
//? if fabric {
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.client.rendering.v1.*;
//?} else if forge {
/*import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
*///?} else if neoforge {
/*import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//? if <1.20.5 {
import net.neoforged.neoforge.event.TickEvent;
//?} else {
/^import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
^///?}
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForge;
*///?}
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.client.IFactoryItemClientExtension;
import wily.factoryapi.base.network.CommonNetwork;
import wily.factoryapi.base.client.UIDefinition;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

public class FactoryAPIClient {
    public static final CommonNetwork.SecureExecutor SECURE_EXECUTOR = new CommonNetwork.SecureExecutor() {
        @Override
        public boolean isSecure() {
            return Minecraft.getInstance().player != null;
        }
    };

    public static final UIDefinition.Manager uiDefinitionManager = new UIDefinition.Manager();

    //? if >=1.21.2 && forge {
    /*public static final List<ModelResourceLocation> extraModels = new ArrayList<>();
    *///?}

    public FactoryAPIClient(){
        init();
    }

    public static void init() {
       FactoryEvent.registerReloadListener(PackType.CLIENT_RESOURCES, uiDefinitionManager);
       preTick(m-> SECURE_EXECUTOR.executeAll());
       registerGuiPostRender(((guiGraphics, partialTicks) -> UIDefinition.Accessor.Accessor.of(Minecraft.getInstance().gui).getRenderables().forEach(r->r.render(guiGraphics,0,0,/*? if >=1.21 {*/ /*partialTicks.getGameTimeDeltaPartialTick(true)*//*?} else {*/ partialTicks /*?}*/))));
       //? if fabric {
       IFactoryItemClientExtension.map.forEach((i,c)-> ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel)-> c.getHumanoidArmorModel(entity,stack,slot,contextModel).renderToBuffer(matrices,vertexConsumers.getBuffer(RenderType.entityCutout(((IFactoryItem) i).getArmorLocation(stack,/*? if <1.21.2 {*/ entity, /*?}*/slot))), light, OverlayTexture.NO_OVERLAY/*? if <=1.20.6 {*/, 1.0F,1.0F,1.0F, 1.0F/*?}*/),i));
       //?} else if neoforge && >=1.20.5 {
        /*FactoryAPIPlatform.getModEventBus().addListener(RegisterClientExtensionsEvent.class,r->IFactoryItemClientExtension.map.forEach((i,c)->r.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return c.getCustomRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            }
            @Override
            //? if <1.21.2 {
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return c.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
            }
            //?} else {
            /^public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentModel.LayerType layerType, Model original) {
                return c.getHumanoidArmorModel(itemStack, layerType, original);
            }
            ^///?}
        }, i)));
        *///?}
    }

    public static final FactoryEvent<Consumer<Minecraft>> STOPPING = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));

    //? if fabric {

    //?} else if forge || neoforge {
    /*public static void registerReloadListener(PreparableReloadListener reloadListener){
        ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(reloadListener);
    }
    *///?}

    public static void setup(Consumer<Minecraft> listener) {
        //? if fabric {
        ClientLifecycleEvents.CLIENT_STARTED.register(listener::accept);
        //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, FMLClientSetupEvent.class, e-> listener.accept(Minecraft.getInstance()));
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(FMLClientSetupEvent.class, e-> listener.accept(Minecraft.getInstance()));
         *///?} else
        /*throw new AssertionError();*/
    }

    public static void preTick(Consumer<Minecraft> listener) {
        //? if fabric {
        ClientTickEvents.START_CLIENT_TICK.register(listener::accept);
        //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, TickEvent.ClientTickEvent.class, e-> {
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
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, TickEvent.ClientTickEvent.class, e-> {
            if (e.phase == TickEvent.Phase.END) listener.accept(Minecraft.getInstance());
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

    public static int getFluidColor(Fluid fluid, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
        //? if fabric {
        return FluidVariantRendering.getColor(FluidVariant.of(fluid),view,pos);
        //?} elif forge || neoforge {
        /*return IClientFluidTypeExtensions.of(fluid).getTintColor(fluid.defaultFluidState(),view,pos);
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

    public interface MenuScreenRegister{
        <H extends AbstractContainerMenu, S extends Screen & MenuAccess<H>> void register(MenuType<? extends H> type, MenuScreens.ScreenConstructor<H, S> factory);
    }

    public static void registerMenuScreen(Consumer<MenuScreenRegister> registry) {
        //? if fabric {
        registry.accept(MenuScreens::register);;
        //?} elif forge || <1.20.4 && neoforge {
        /*registry.accept(MenuScreens::register);
        *///?} elif neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(RegisterMenuScreensEvent.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }


    public static void registerGuiPostRender(BiConsumer<GuiGraphics,/*? if >=1.21 {*/ /*DeltaTracker *//*?} else {*/Float/*?}*/> registry) {
        //? if fabric {
        HudRenderCallback.EVENT.register(registry::accept);
        //?} elif forge {
        /*//? if <1.20.5 {
        /^MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, RenderGuiEvent.Post.class, e-> registry.accept(e.getGuiGraphics(),e.getPartialTick()));
        ^///?} else
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, CustomizeGuiOverlayEvent.class, e-> registry.accept(e.getGuiGraphics(),Minecraft.getInstance().^//^? if <1.21.2 {^/getTimer/^?} else {^//^getDeltaTracker^//^?}^/()));
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

    public static void registerItemColor(Consumer<BiConsumer<ItemColor, Item>> registry){
        //? if fabric {
        registry.accept(ColorProviderRegistry.ITEM::register);
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, RegisterColorHandlersEvent.Item.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }

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

    public static void registerExtraModels(Consumer<Consumer<ModelResourceLocation>> registry){
        //? if fabric {
        ModelLoadingPlugin.register(pluginContext -> registry.accept(p->pluginContext.addModels(FactoryAPI.createLocation(p.toString()))));
        //?} elif forge || neoforge {
        /*//? if forge && >1.21.2 {
        /^registry.accept(extraModels::add);
        ^///?} else
        FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, ModelEvent.RegisterAdditional.class, e->registry.accept(e::register));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static <T extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider){
        //? if fabric {
        EntityRendererRegistry.register(type.get(),provider);
        //?} else if forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, EntityRenderersEvent.RegisterRenderers.class, e-> e.registerEntityRenderer(type.get(),provider));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerLayerDefinitionRegistry(ModelLayerLocation location, Supplier<LayerDefinition> definition){
        //? if fabric {
        EntityModelLayerRegistry.registerModelLayer(location,definition::get);
         //?} else if forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, EntityRenderersEvent.RegisterLayerDefinitions.class, e-> e.registerLayerDefinition(location,definition));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerRenderLayerRegistry(TriConsumer<Function<EntityType<? extends LivingEntity>, EntityRenderer<?>>, EntityModelSet, FactoryRenderLayerRegistry> registry){
        //? if fabric {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((a, b, c, d)-> registry.accept((type) -> b, d.getModelSet(), new FactoryRenderLayerRegistry() {
                @Override
                public <T extends LivingEntity, M extends EntityModel<T>> void register(LivingEntityRenderer<T, M> renderer, RenderLayer<T, M> renderLayer) {
                    c.register(renderLayer);
                }
            }));
         //?} else if forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, EntityRenderersEvent.AddLayers.class, e-> registry.accept(e::getRenderer, e.getEntityModels(), new FactoryRenderLayerRegistry() {
            @Override
            public <T extends LivingEntity, M extends EntityModel<T>> void register(LivingEntityRenderer<T, M> renderer, RenderLayer<T, M> renderLayer) {
                renderer.addLayer(renderLayer);
            }
        }));
        *///?} else
        /*throw new AssertionError();*/

    }

    public interface FactoryModelLayerRegistry {
        void register(ModelLayerLocation location, Supplier<LayerDefinition> definition);
    }
    public interface FactoryRenderLayerRegistry {
        <T extends LivingEntity, M extends EntityModel<T>>void register(LivingEntityRenderer<T,  M> renderer, RenderLayer< T, M> renderLayer);
    }
}
