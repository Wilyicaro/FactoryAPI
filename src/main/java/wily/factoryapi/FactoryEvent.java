package wily.factoryapi;

//? if fabric {
//? if >=1.20.5 {
/*import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
*///?}
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
//?} elif forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.server.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
//? if >1.20.1 {
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.EventNetworkChannel;
//?} else {
/^import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
^///?}
//? if >=1.20.5 {
/^import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;
^///?}
*///?} elif neoforge {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.server.*;
import net.neoforged.bus.api.EventPriority;
//? if <1.20.5 {
/^import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
^///?} else {
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
//?}
*///?}

//? if >1.20.1
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

//? if >=1.20.5 {
/*import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.KnownPack;
*///?}
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.util.TriConsumer;
import wily.factoryapi.base.network.CommonNetwork;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FactoryEvent<T> {
    protected final List<T> listeners = new ArrayList<>();
    public final T invoker;

    public FactoryEvent(Function<FactoryEvent<T>,T> invoker){
        this.invoker = invoker.apply(this);
    }

    public void invokeAll(Consumer<T> invoker){
        listeners.forEach(invoker);
    }
    public void invokeAnyMatch(Predicate<T> invoker){
        for (T listener : listeners) {
            if (invoker.test(listener)) return;
        }
    }
    public void register(T listener){
        listeners.add(listener);
    }

    public interface ServerSave {
        FactoryEvent<ServerSave> EVENT = new FactoryEvent<>(e-> ((server, log, flush, force)-> e.invokeAll(t->t.run(server,log,flush,force))));
        void run(MinecraftServer server, boolean log, boolean flush, boolean force);
    }

    public interface PlayerEvent extends Consumer<ServerPlayer>{
        FactoryEvent<Consumer<PlayerList>> RELOAD_RESOURCES_EVENT = new FactoryEvent<>(e-> (s -> e.invokeAll(t-> t.accept(s))));
        FactoryEvent<PlayerEvent> JOIN_EVENT = new FactoryEvent<>(e-> (s -> e.invokeAll(t-> t.accept(s))));
        FactoryEvent<PlayerEvent> REMOVED_EVENT = new FactoryEvent<>(e-> (s -> e.invokeAll(t-> t.accept(s))));
    }


    public static void setup(Runnable run) {
        //? if fabric {
        run.run();
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, FMLCommonSetupEvent.class, e-> run.run());
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void tagsLoaded(Runnable run) {
        //? if fabric {
        CommonLifecycleEvents.TAGS_LOADED.register((s, t)-> run.run());
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getForgeEventBus().addListener(EventPriority.NORMAL,false, TagsUpdatedEvent.class, e-> run.run());
         *///?} else
        /*throw new AssertionError();*/
    }

    public static void serverStarted(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerLifecycleEvents.SERVER_STARTED.register(apply::accept);
        //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getForgeEventBus().addListener(EventPriority.NORMAL,false, ServerStartedEvent.class, e-> apply.accept(e.getServer()));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void serverStopping(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerLifecycleEvents.SERVER_STOPPING.register(apply::accept);
         //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getForgeEventBus().addListener(EventPriority.NORMAL,false, ServerStoppingEvent.class, e-> apply.accept(e.getServer()));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void serverStopped(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerLifecycleEvents.SERVER_STOPPED.register(apply::accept);
         //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getForgeEventBus().addListener(EventPriority.NORMAL,false, ServerStoppedEvent.class, e-> apply.accept(e.getServer()));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void preServerTick(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerTickEvents.START_SERVER_TICK.register(apply::accept);
        //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, /^? if <1.21 {^/TickEvent.ServerTickEvent/^?} else {^//^TickEvent.ServerTickEvent.Pre^//^?}^/.class, e-> {
            if (e.phase == TickEvent.Phase.START) apply.accept(e.getServer());
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(/^? if <1.20.5 {^/ /^TickEvent.ServerTickEvent.class^//^?} else {^/ServerTickEvent.Pre.class/^?}^/, e-> {
            /^? if <1.20.5 {^//^if (e.phase == TickEvent.Phase.START)^//^?}^/ apply.accept(e.getServer());
        });
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void afterServerTick(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerTickEvents.END_SERVER_TICK.register(apply::accept);
         //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, /^? if <1.21 {^/TickEvent.ServerTickEvent/^?} else {^//^TickEvent.ServerTickEvent.Post^//^?}^/.class, e-> {
            if (e.phase == TickEvent.Phase.END) apply.accept(e.getServer());
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(/^? if <1.20.5 {^/ /^TickEvent.ServerTickEvent.class^//^?} else {^/ServerTickEvent.Post.class/^?}^/, e-> {
            /^? if <1.20.5 {^//^if (e.phase == TickEvent.Phase.END)^//^?}^/ apply.accept(e.getServer());
        });
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerReloadListener(PackType type, PreparableReloadListener reloadListener){
        //? if fabric {
        ResourceLocation location = FactoryAPI.createLocation(reloadListener.getName());
        ResourceManagerHelper.get(type).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return location;
            }

            @Override
            public String getName() {
                return reloadListener.getName();
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, /*? if <1.21.2 {*/ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2,/*?}*/ Executor executor, Executor executor2) {
                return reloadListener.reload(preparationBarrier,resourceManager,/*? if <1.21.2 {*/profilerFiller, profilerFiller2,/*?}*/executor,executor2);
            }
        });
        //?} elif forge {
        /*if (type == PackType.CLIENT_RESOURCES) FactoryAPIClient.registerReloadListener(reloadListener);
        else MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, AddReloadListenerEvent.class, e-> e.addListener(reloadListener));
        *///?} elif neoforge {
        /*if (type == PackType.CLIENT_RESOURCES) FactoryAPIClient.registerReloadListener(reloadListener);
        else NeoForge.EVENT_BUS.addListener(/^? if <1.21.4 {^//^AddReloadListenerEvent^//^?} else {^/AddServerReloadListenersEvent/^?}^/.class, e-> e.addListener(/^? if >= 1.21.4 {^/FactoryAPI.createLocation(reloadListener.getName()), /^?}^/reloadListener));
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void registerCommands(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Commands.CommandSelection> register){
        //? if fabric {
        CommandRegistrationCallback.EVENT.register(register::accept);
         //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, RegisterCommandsEvent.class, e-> register.accept(e.getDispatcher(),e.getBuildContext(),e.getCommandSelection()));
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, e-> register.accept(e.getDispatcher(),e.getBuildContext(),e.getCommandSelection()));
        *///?} else
        /*throw new AssertionError();*/
    }

    @FunctionalInterface
    public interface PackRegistry {
        void register(String path, ResourceLocation name, Component component, Pack.Position position, boolean enabledByDefault);
        default void register(String path, ResourceLocation name, boolean enabledByDefault){
            register(path, name, Component.translatable(name.getNamespace() + ".builtin." + name.getPath()), Pack.Position.TOP, enabledByDefault);
        }
        default void registerResourcePack(ResourceLocation location, boolean enabledByDefault){
            register("resourcepacks/"+location.getPath(), location, enabledByDefault);
        }
        default void registerResourcePack(String pathName, boolean enabledByDefault){
            registerResourcePack(FactoryAPI.createVanillaLocation(pathName),enabledByDefault);
        }
    }

    public static Pack createBuiltInPack(ResourceLocation name, Component displayName, boolean defaultEnabled, PackType type, Pack.Position position, Path resourcePath){
        //? if <=1.20.1 {
        /*return Pack.readMetaAndCreate(name.toString(), displayName,false, s-> new PathPackResources(s, resourcePath,true), type, position, PackSource.create(PackSource.BUILT_IN::decorate, defaultEnabled));
        *///?} else if <1.20.5 {
        return Pack.readMetaAndCreate(name.toString(), displayName,false, new PathPackResources.PathResourcesSupplier(resourcePath,true), type, position, PackSource.create(PackSource.BUILT_IN::decorate, defaultEnabled));
        //?} else {
        /*return Pack.readMetaAndCreate(new PackLocationInfo( name.toString(), displayName,PackSource.create(PackSource.BUILT_IN::decorate, defaultEnabled), Optional.of(new KnownPack(name.getNamespace(),name.toString(), SharedConstants.getCurrentVersion().getId()))), new PathPackResources.PathResourcesSupplier(resourcePath), type, new PackSelectionConfig(false,position,false));
        *///?}
    }

    public static void registerBuiltInPacks(Consumer<PackRegistry> registry){
        //? if fabric {
        registry.accept(((path, name, component, position, enabledByDefault) -> ResourceManagerHelper.registerBuiltinResourcePack(name, FabricLoader.getInstance().getModContainer(name.getNamespace()).orElseThrow(), component, enabledByDefault ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL)));
         //?} elif forge || neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL, false, AddPackFindersEvent.class, event-> registry.accept((path, name, displayName, position, defaultEnabled) -> {
            Path resourcePath = ModList.get().getModFileById(name.getNamespace()).getFile().findResource(path);
            for (PackType type : PackType.values()) {
                if (event.getPackType() != type || !Files.isDirectory(resourcePath.resolve(type.getDirectory()))) continue;
                Pack pack = createBuiltInPack(name, displayName, defaultEnabled,type,position,resourcePath);
                event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
            }
        }));
        *///?} else
        /*throw new AssertionError();*/
    }

    public interface PayloadRegistry {
        <T extends CommonNetwork.Payload> void register(boolean c2s, CommonNetwork.Identifier<T> identifier);
    }

    public static void registerPayload(Consumer<PayloadRegistry> registry){
        //? if fabric {
        registry.accept(new FactoryEvent.PayloadRegistry() {
            @Override
            public <T extends CommonNetwork.Payload> void register(boolean c2s, CommonNetwork.Identifier<T> id) {
                //? <1.20.5 {
                if (c2s) ServerPlayNetworking.registerGlobalReceiver(id.location(), (m, l, h, b, s) -> id.decode(b).applyServer(()->h.player));
                else FactoryAPIClient.registerPayload(id);
                //?} else {
                /*if (c2s) {
                    PayloadTypeRegistry.playC2S().register(id.type(),id.codec());
                    ServerPlayNetworking.registerGlobalReceiver(id.type(), (payload, context)-> payload.applyServer(context::player));
                }else {
                    PayloadTypeRegistry.playS2C().register(id.type(),id.codec());
                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) FactoryAPIClient.registerPayload(id);
                }
                *///?}
            }
        });
         //?} elif forge {
        /*registry.accept(new FactoryEvent.PayloadRegistry() {
            @Override
            public <T extends CommonNetwork.Payload> void register(boolean c2s, CommonNetwork.Identifier<T> id) {
                //? if <1.20.5 {
                EventNetworkChannel NETWORK = /^? <=1.20.1 {^//^NetworkRegistry.^//^?}^/ChannelBuilder.named(id.location())./^? if <=1.20.1 {^//^networkProtocolVersion(()->"1").serverAcceptedVersions(s-> true).clientAcceptedVersions(s-> true).^//^?} else {^/optional()./^?}^/eventNetworkChannel();
                if (c2s || FMLEnvironment.dist.isClient()) NETWORK.addListener(p->{
                    var source = p.getSource()/^? <=1.20.1 {^//^.get()^//^?}^/;
                    if (/^? >1.20.1 {^/p.getChannel().equals(id.location()) && /^?}^/p.getPayload() != null) id.decode(p.getPayload()).applySided(/^? if <=1.20.1 {^//^p.getSource().get().getDirection().getReceptionSide().isClient()^//^?} else {^/ source.isClientSide()/^?}^/, source::getSender);
                    source.setPacketHandled(true);
                });
                //?} else {
                /^PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> protocol = ChannelBuilder.named(id.location()).payloadChannel().play();
                if (c2s) protocol.serverbound().addMain(id.type(),id.codec(),(m,c)-> m.applyServer(c::getSender)).build();
                else protocol.clientbound().addMain(id.type(),id.codec(),(m,c)-> m.applyClient()).build();
                ^///?}
            }
        });
        *///?} elif neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(/^? if <1.20.5 {^//^RegisterPayloadHandlerEvent^//^?} else {^/ RegisterPayloadHandlersEvent/^?}^/.class, e-> {
            registry.accept(new PayloadRegistry() {
                @Override
                public <T extends CommonNetwork.Payload> void register(boolean c2s, CommonNetwork.Identifier<T> id) {
                    /^? if <1.20.5 {^//^IPayloadRegistrar^//^?} else {^/ PayloadRegistrar/^?}^/ registrar = e.registrar(id.location().getNamespace()).optional();
                    //? if <1.20.5 {
                    /^if (c2s || FMLEnvironment.dist.isClient()) registrar.play(id.location(),id::decode,(h, arg)->h.apply(arg.flow().isClientbound() ? FactoryAPIClient.SECURE_EXECUTOR : FactoryAPI.SECURE_EXECUTOR,()->arg.flow().isClientbound() ? FactoryAPIClient.getClientPlayer() : arg.player().orElse(null)));
                    ^///?} else {
                    if (c2s) registrar.playToServer(id.type(),id.codec(),(h,arg)->h.applyServer(arg::player));
                    else registrar.playToClient(id.type(),id.codec(),(h,arg)->h.applyClient());
                    //?}
                }
            });
        });
         *///?} else
        /*throw new AssertionError();*/
    }

    //? if >=1.20.5 {
    /*public static <C> void setItemComponent(Item item, DataComponentType<C> type, C value){
        //? if fabric {
        DefaultItemComponentEvents.MODIFY.register(c->  c.modify(item, bc-> bc.set(type,value)));
        //?} else if forge {
        /^MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, GatherComponentsEvent.class, e-> {
            if (e.getOwner() == item) e.register(type,value);
        });
        ^///?} else if neoforge {
        /^FactoryAPIPlatform.getModEventBus().addListener(ModifyDefaultComponentsEvent.class, e-> e.modify(item, bc-> bc.set(type,value)));
        ^///?}
    }
    *///?}
}
