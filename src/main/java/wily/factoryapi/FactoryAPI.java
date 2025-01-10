package wily.factoryapi;


import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.2 {
/*import net.minecraft.util.profiling.Profiler;
*///?}
import net.minecraft.util.profiling.ProfilerFiller;
//? if fabric {
import net.fabricmc.api.EnvType;
import wily.factoryapi.base.fabric.FabricStorages;
import net.fabricmc.loader.api.FabricLoader;
//?} else if forge {
/*import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
*///?} else if neoforge {
/*import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
*///?}
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.*;
import wily.factoryapi.base.network.CommonNetwork;
import wily.factoryapi.base.network.FactoryAPICommand;
import wily.factoryapi.base.network.HelloPayload;
import wily.factoryapi.init.FactoryRegistries;
import wily.factoryapi.util.DynamicUtil;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

//? if forge || neoforge
/*@Mod(FactoryAPI.MOD_ID)*/
public class FactoryAPI {
    public static final String MOD_ID = "factory_api";

    public static final CommonNetwork.SecureExecutor SECURE_EXECUTOR = new CommonNetwork.SecureExecutor() {
        @Override
        public boolean isSecure() {
            return true;
        }
    };
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FactoryAPI(){
        init();

        //? if forge {
        /*MinecraftForge.EVENT_BUS.addGenericListener(AttachCapabilitiesEvent.class, event->{
            AttachCapabilitiesEvent<?> e = (AttachCapabilitiesEvent<?>) event;
            if (e.getObject() instanceof IFactoryStorage be){
                e.addCapability(FactoryAPI.createModLocation( "factory_api_capabilities"), new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                        FactoryStorage<?> storage = FactoryAPIPlatform.BLOCK_CAPABILITY_MAP.get(capability);
                        ArbitrarySupplier<? extends IPlatformHandler> handler = be.getStorage(storage,arg);
                        if (storage != null && handler.isPresent())
                            return LazyOptional.of(handler::get).cast();
                        return LazyOptional.empty();
                    }
                });
            }else if (e.getObject() instanceof ItemStack s && s.getItem() instanceof IFactoryItem i){
                e.addCapability(FactoryAPI.createModLocation( "factory_api_capabilities"), new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                        FactoryStorage<?> storage = FactoryAPIPlatform.ITEM_CAPABILITY_MAP.get(capability);
                        ArbitrarySupplier<? extends IPlatformHandler> handler = i.getStorage(storage,s);
                        if (storage != null && handler.isPresent())
                            return LazyOptional.of(handler::get).cast();
                        return LazyOptional.empty();
                    }
                });
            }
        });
        *///?}
        //? if forge || neoforge
        /*if (FMLEnvironment.dist == Dist.CLIENT) FactoryAPIClient.init();*/
    }

    public static void init() {
        LOGGER.info("Initializing FactoryAPI!");
        FactoryEvent.registerPayload(r->{
            r.register( false, FactoryAPICommand.UIDefinitionPayload.ID);
            r.register( false, HelloPayload.ID_S2C);
            r.register( true, HelloPayload.ID_C2S);
        });
        FactoryEvent.preServerTick(s-> SECURE_EXECUTOR.executeAll());
        FactoryEvent.registerCommands(((commandSourceStackCommandDispatcher, commandBuildContext, commandSelection) -> FactoryAPICommand.register(commandSourceStackCommandDispatcher,commandBuildContext)));
        FactoryRegistries.init();
        FactoryIngredient.init();
        FactoryEvent.setup(()->FactoryAPIPlatform.registerByClassArgumentType(FactoryAPICommand.JsonArgument.class, FactoryRegistries.JSON_ARGUMENT_TYPE.get()));
        FactoryEvent.PlayerEvent.JOIN_EVENT.register(sp->CommonNetwork.forceEnabledPlayer(sp,()->CommonNetwork.sendToPlayer(sp, HelloPayload.createS2C())));
        FactoryEvent.PlayerEvent.REMOVED_EVENT.register(sp->CommonNetwork.ENABLED_PLAYERS.remove(sp.getUUID()));
        FactoryEvent.serverStopped(s-> SECURE_EXECUTOR.clear());
        //? if fabric {
        FabricStorages.registerDefaultStorages();
        //?}
    }

    public static ProfilerFiller getProfiler(){
        return /*? if <1.21.2 {*/Minecraft.getInstance().getProfiler/*?} else {*//*Profiler.get*//*?}*/();
    }

    public static ResourceLocation createLocation(String namespace, String path){
        return ResourceLocation.tryBuild(namespace,path);
    }
    public static ResourceLocation createLocation(String location){
        return ResourceLocation.tryParse(location);
    }
    public static ResourceLocation createModLocation(String path){
        return createLocation(MOD_ID,path);
    }
    public static ResourceLocation createVanillaLocation(String path){
        //? if <1.20.5 {
        return new ResourceLocation(path);
        //?} else
        /*return ResourceLocation.withDefaultNamespace(path);*/
    }

    public static Loader getLoader() {
        //? if fabric {
        return Loader.FABRIC;
        //?} elif forge {
        /*return Loader.FORGE;
        *///?} elif neoforge {
        /*return Loader.NEOFORGE;
        *///?} else
        /*return null;*/
    }

    public static boolean isLoadingMod(String modId) {
        //? if fabric {
        return isModLoaded(modId);
        //?} else if forge || neoforge {
        /*return LoadingModList.get().getModFileById(modId) != null;
         *///?} else
        /*throw new AssertionError();*/
    }

    public static boolean isModLoaded(String modId) {
        //? if fabric {
        return FabricLoader.getInstance().isModLoaded(modId);
        //?} else if forge || neoforge {
        /*return ModList.get().isLoaded(modId);
         *///?} else
        /*throw new AssertionError();*/
    }

    public enum Loader {
        FABRIC,FORGE,NEOFORGE;
        public boolean isForgeLike(){
            return this == FORGE || this == NEOFORGE;
        }
        public boolean isFabric(){
            return this == FABRIC;
        }
    }

    public static Path getConfigDirectory() {
        //? if fabric {
        return FabricLoader.getInstance().getConfigDir();
        //?} elif forge || neoforge {
        /*return FMLPaths.CONFIGDIR.get();
         *///?} else
        /*throw new AssertionError();*/
    }

    public static boolean isClient() {
        //? if fabric {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        //?} else if forge || neoforge {
        /*return FMLEnvironment.dist.isClient();
         *///?} else
        /*throw new AssertionError();*/
    }

    public static <T> Map<String, Field> getAccessibleFieldsMap(Class<T> fieldsClass, String... fields){
        Map<String,Field> map = new HashMap<>();
        for (String s : fields) {
            try {
                Field field = fieldsClass.getDeclaredField(s);
                field.setAccessible(true);
                map.put(s, field);
            } catch (NoSuchFieldException var1) {
                throw new IllegalStateException("Couldn't get field %s for %s".formatted(s, fieldsClass), var1);
            }
        }
        return map;
    }
}
