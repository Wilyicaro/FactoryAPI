package wily.factoryapi;

//? if fabric {
//? if >=1.20.5
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;*/
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
//?} elif forge {
/*import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.EventNetworkChannel;
//? if >=1.20.5 {
import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;
//?}
*///?} elif neoforge {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.*;
//? if <1.20.5 {
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
//?} else {
/^import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
^///?}
*///?}
import net.minecraft.SharedConstants;
//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.KnownPack;
*///?}
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
import net.minecraft.util.profiling.ProfilerFiller;
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
        FactoryEvent<ServerSave> EVENT = new FactoryEvent<>(e-> ((server, log, flush, force) -> e.invokeAll(t->t.run(server,log,flush,force))));
        void run(MinecraftServer server, boolean log, boolean flush, boolean force);
    }


    public static void setup(Runnable run) {
        //? if fabric {
        CommonLifecycleEvents.TAGS_LOADED.register((s, t)-> run.run());
        //?} elif forge {
        /*FactoryAPIPlatform.getModEventBus().addListener(EventPriority.NORMAL,false, FMLCommonSetupEvent.class, e-> run.run());
        *///?} elif neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(FMLCommonSetupEvent.class, e-> run.run());
         *///?} else
        /*throw new AssertionError();*/
    }

    public static void preServerTick(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerTickEvents.START_SERVER_TICK.register(apply::accept);
        //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, TickEvent.ServerTickEvent.class, e-> {
            if (e.phase == TickEvent.Phase.START) apply.accept(e.getServer());
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(/^? if <1.20.5 {^/ TickEvent.ServerTickEvent.class/^?} else {^//^ServerTickEvent.Pre.class^//^?}^/, e-> {
            /^? if <1.20.5 {^/if (e.phase == TickEvent.Phase.START)/^?}^/ apply.accept(e.getServer());
        });
        *///?} else
        /*throw new AssertionError();*/
    }

    public static void afterServerTick(Consumer<MinecraftServer> apply) {
        //? if fabric {
        ServerTickEvents.END_SERVER_TICK.register(apply::accept);
         //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, TickEvent.ServerTickEvent.class, e-> {
            if (e.phase == TickEvent.Phase.END) apply.accept(e.getServer());
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(/^? if <1.20.5 {^/ TickEvent.ServerTickEvent.class/^?} else {^//^ServerTickEvent.Post.class^//^?}^/, e-> {
            /^? if <1.20.5 {^/if (e.phase == TickEvent.Phase.END)/^?}^/ apply.accept(e.getServer());
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
        else NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, e-> e.addListener(reloadListener));
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
            register(path,name,Component.translatable(name.getNamespace() + ".builtin." + name.getPath()), Pack.Position.TOP,enabledByDefault);
        }
        default void registerResourcePack(String pathName, boolean enabledByDefault){
            register("resourcepacks/"+pathName,FactoryAPI.createVanillaLocation(pathName),enabledByDefault);
        }
    }
    public static Pack createBuiltInPack(ResourceLocation name, Component displayName, boolean defaultEnabled, PackType type, Pack.Position position, Path resourcePath){
        //? if <1.20.5 {
        return Pack.readMetaAndCreate(name.toString(), displayName,false, new PathPackResources.PathResourcesSupplier(resourcePath,true), type, position, PackSource.create(PackSource.BUILT_IN::decorate, defaultEnabled));
        //?} else {
        /*return Pack.readMetaAndCreate(new PackLocationInfo( name.toString(), displayName,PackSource.create(PackSource.BUILT_IN::decorate, defaultEnabled), Optional.of(new KnownPack(name.getNamespace(),name.toString(), SharedConstants.getCurrentVersion().getId()))), new PathPackResources.PathResourcesSupplier(resourcePath), type, new PackSelectionConfig(false,position,false));
        *///?}
    }

    public static void registerBuiltInPacks(Consumer<PackRegistry> registry){
        //? if fabric {
         registry.accept(((path, name, component, position, enabledByDefault) -> ResourceManagerHelperImpl.registerBuiltinResourcePack(name,path, FabricLoader.getInstance().getModContainer(name.getNamespace()).orElseThrow(), component, enabledByDefault ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL)));
         //?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,false, AddPackFindersEvent.class, event-> registry.accept((path, name, displayName, position, defaultEnabled) -> {
            Path resourcePath = ModList.get().getModFileById(name.getPath()).getFile().findResource(path);
            for (PackType type : PackType.values()) {
                if (event.getPackType() != type || !Files.isDirectory(resourcePath.resolve(type.getDirectory()))) continue;
                Pack pack = createBuiltInPack(name, displayName, defaultEnabled,type,position,resourcePath);
                event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
            }
        }));
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(AddPackFindersEvent.class, event-> registry.accept((path, name, displayName, position, defaultEnabled) -> {
            Path resourcePath = ModList.get().getModFileById(name.getPath()).getFile().findResource(path);
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
                else ClientPlayNetworking.registerGlobalReceiver(id.location(), (m, l, b, s) -> id.decode(b).applyClient());
                //?} else {
                /*if (c2s) {
                    PayloadTypeRegistry.playC2S().register(id.type(),id.codec());
                    ServerPlayNetworking.registerGlobalReceiver(id.type(), (payload, context)-> payload.apply(FactoryAPI.SECURE_EXECUTOR, context::player));
                }else {
                    PayloadTypeRegistry.playS2C().register(id.type(),id.codec());
                    if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ClientPlayNetworking.registerGlobalReceiver(id.type(), (payload, context) -> payload.apply(FactoryAPIClient.SECURE_EXECUTOR,context::player));
                }
                *///?}
            }
        });
         //?} elif forge {
        /*registry.accept(new FactoryEvent.PayloadRegistry() {
            @Override
            public <T extends CommonNetwork.Payload> void register(boolean c2s, CommonNetwork.Identifier<T> id) {
                //? if <1.20.5 {
                /^EventNetworkChannel NETWORK = ChannelBuilder.named(id.location()).eventNetworkChannel();
                if (c2s || FMLEnvironment.dist.isClient()) NETWORK.addListener(p->{
                    if (p.getChannel().equals(id.location()) && p.getPayload() != null) id.decode(p.getPayload()).apply(p.getSource().isClientSide() ? FactoryAPIClient.SECURE_EXECUTOR : FactoryAPI.SECURE_EXECUTOR, () -> p.getSource().isClientSide() ? FactoryAPIClient.getClientPlayer() : p.getSource().getSender());
                    p.getSource().setPacketHandled(true);
                });
                ^///?} else {
                PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> protocol = ChannelBuilder.named(id.location().getNamespace()).payloadChannel().play();
                if (c2s) protocol.serverbound().addMain(id.type(),id.codec(),(m,c)-> m.applyServer(c::getSender)).build();
                else protocol.clientbound().addMain(id.type(),id.codec(),(m,c)-> m.applyClient()).build();
                //?}
            }
        });
        *///?} elif neoforge {
        /*FactoryAPIPlatform.getModEventBus().addListener(/^? if <1.20.5 {^/RegisterPayloadHandlerEvent/^?} else {^/ /^RegisterPayloadHandlersEvent^//^?}^/.class, e-> {
            registry.accept(new PayloadRegistry() {
                @Override
                public <T extends CommonNetwork.Payload> void register(boolean c2s, CommonNetwork.Identifier<T> id) {
                    /^? if <1.20.5 {^/IPayloadRegistrar/^?} else {^/ /^PayloadRegistrar^//^?}^/ registrar = e.registrar(id.location().getNamespace()).optional();
                    //? if <1.20.5 {
                    if (c2s || FMLEnvironment.dist.isClient()) registrar.play(id.location(),id::decode,(h, arg)->h.apply(arg.flow().isClientbound() ? FactoryAPIClient.SECURE_EXECUTOR : FactoryAPI.SECURE_EXECUTOR,()->arg.flow().isClientbound() ? FactoryAPIClient.getClientPlayer() : arg.player().orElse(null)));
                    //?} else {
                    /^if (c2s) registrar.playToServer(id.type(),id.codec(),(h,arg)->h.applyServer(arg::player));
                    else registrar.playToClient(id.type(),id.codec(),(h,arg)->h.applyClient());
                    ^///?}
                }
            });
        });
         *///?} else
        /*throw new AssertionError();*/
    }
}
