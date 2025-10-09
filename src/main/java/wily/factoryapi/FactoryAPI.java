package wily.factoryapi;


import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.config.FactoryCommonOptions;
import wily.factoryapi.base.config.FactoryConfig;
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
//? if >=1.21.9 {
/^import net.neoforged.fml.loading.FMLLoader;
^///?}
*///?}
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.*;
import wily.factoryapi.base.network.*;
import wily.factoryapi.init.FactoryRegistries;
import wily.factoryapi.util.ModInfo;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

//? if forge || neoforge
/*@Mod(FactoryAPI.MOD_ID)*/
public class FactoryAPI {
    public static final String MOD_ID = "factory_api";

    public static final SecureExecutor SECURE_EXECUTOR = new SecureExecutor() {
        @Override
        public boolean isSecure() {
            return true;
        }
    };
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static MinecraftServer currentServer;


    public FactoryAPI(){
        init();

        //? if forge && <1.21.6 {
        /*MinecraftForge.EVENT_BUS.<AttachCapabilitiesEvent<BlockEntity>, BlockEntity>addGenericListener(BlockEntity.class, event->{
            if (event.getObject() instanceof IFactoryStorage be){
                event.addCapability(FactoryAPI.createModLocation("fallback_capabilities"), new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                        FactoryStorage<?> storage = FactoryAPIPlatform.BLOCK_CAPABILITY_MAP.get(capability);
                        ArbitrarySupplier<? extends IPlatformHandler> handler = be.getStorage(storage,arg);
                        if (storage != null && handler.isPresent())
                            return LazyOptional.of(handler::get).cast();
                        return LazyOptional.empty();
                    }
                });
            }
        });
        MinecraftForge.EVENT_BUS.<AttachCapabilitiesEvent<ItemStack>, ItemStack>addGenericListener(ItemStack.class,event->{
            if (event.getObject().getItem() instanceof IFactoryItem i){
                event.addCapability(FactoryAPI.createModLocation("item_fallback_capabilities"), new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                        FactoryStorage<?> storage = FactoryAPIPlatform.ITEM_CAPABILITY_MAP.get(capability);
                        ArbitrarySupplier<? extends IPlatformHandler> handler = i.getStorage(storage, event.getObject());
                        if (storage != null && handler.isPresent())
                            return LazyOptional.of(handler::get).cast();
                        return LazyOptional.empty();
                    }
                });
            }
        });
        *///?} else if forge {
        /*AttachCapabilitiesEvent.BlockEntities.BUS.addListener(event->{
            if (event.getObject() instanceof IFactoryStorage be){
                event.addCapability(FactoryAPI.createModLocation("fallback_capabilities"), new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                        FactoryStorage<?> storage = FactoryAPIPlatform.BLOCK_CAPABILITY_MAP.get(capability);
                        ArbitrarySupplier<? extends IPlatformHandler> handler = be.getStorage(storage,arg);
                        if (storage != null && handler.isPresent())
                            return LazyOptional.of(handler::get).cast();
                        return LazyOptional.empty();
                    }
                });
            }
        });
        AttachCapabilitiesEvent.ItemStacks.BUS.addListener(event->{
            if (event.getObject().getItem() instanceof IFactoryItem i){
                event.addCapability(FactoryAPI.createModLocation("item_fallback_capabilities"), new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                        FactoryStorage<?> storage = FactoryAPIPlatform.ITEM_CAPABILITY_MAP.get(capability);
                        ArbitrarySupplier<? extends IPlatformHandler> handler = i.getStorage(storage, event.getObject());
                        if (storage != null && handler.isPresent())
                            return LazyOptional.of(handler::get).cast();
                        return LazyOptional.empty();
                    }
                });
            }
        });
        *///?}
        //? if forge || neoforge
        /*if (isClient()) FactoryAPIClient.init();*/
    }

    public static void init() {
        LOGGER.info("Initializing FactoryAPI!");
        FactoryConfig.registerCommonStorage(createModLocation("common"), FactoryCommonOptions.COMMON_STORAGE);
        FactoryEvent.registerPayload(r->{
            r.register( false, FactoryAPICommand.UIDefinitionPayload.ID);
            r.register( false, HelloPayload.ID_S2C);
            r.register( true, HelloPayload.ID_C2S);
            r.register( false, CommonConfigSyncPayload.ID_S2C);
            r.register( true, CommonConfigSyncPayload.ID_C2S);
            r.register(false, OpenExtraMenuPayload.ID);
            //? if >=1.21.2 {
            /*r.register(false, CommonRecipeManager.ClientPayload.ID);
            *///?}
        });
        FactoryEvent.preServerTick(s-> SECURE_EXECUTOR.executeAll());
        FactoryEvent.registerCommands(((commandSourceStackCommandDispatcher, commandBuildContext, commandSelection) -> FactoryAPICommand.register(commandSourceStackCommandDispatcher,commandBuildContext)));
        FactoryRegistries.init();
        FactoryIngredient.init();
        FactoryEvent.setup(()->{
            FactoryAPIPlatform.registerByClassArgumentType(FactoryAPICommand.JsonArgument.class, FactoryRegistries.JSON_ARGUMENT_TYPE.get());
            FactoryCommonOptions.COMMON_STORAGE.load();
        });
        FactoryEvent.PlayerEvent.JOIN_EVENT.register(HelloPayload::sendInitialPayloads);
        //? if >=1.21.2 {
        /*Consumer<MinecraftServer> updateRecipes = server -> CommonRecipeManager.updateRecipes(server.getRecipeManager());
        FactoryEvent.PlayerEvent.RELOAD_RESOURCES_EVENT.register(playerList-> {
            updateRecipes.accept(playerList.getServer());
            CommonNetwork.sendToPlayers(playerList.getPlayers(), CommonRecipeManager.ClientPayload.getInstance());
        });
        FactoryEvent.serverStarted(updateRecipes);
        *///?}
        FactoryEvent.PlayerEvent.REMOVED_EVENT.register(sp->CommonNetwork.ENABLED_PLAYERS.removeAll(sp.getUUID()));
        FactoryEvent.serverStopped(s-> {
            SECURE_EXECUTOR.clear();
            CommonNetwork.ENABLED_PLAYERS.clear();
            //? if >=1.21.2 {
            /*CommonRecipeManager.clearRecipes();
            *///?}
            currentServer = null;
        });
        //? if fabric {
        FabricStorages.registerDefaultStorages();
        //?}
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
        //?} else if forge || (neoforge && <1.21.9) {
        /*return LoadingModList.get().getModFileById(modId) != null;
        *///?} else if neoforge {
        /*return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
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
         //?} else if forge || (neoforge && <1.21.9) {
        /*return FMLEnvironment.dist.isClient();
         *///?} else if neoforge {
        /*return FMLEnvironment.getDist().isClient();
        *///?} else
        /*throw new AssertionError();*/
    }

    public static <T> Field getAccessibleField(Class<T> fieldClass, String field){
        try {
            Field f = fieldClass.getDeclaredField(field);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field %s for %s".formatted(field, fieldClass), var1);
        }
    }

    public static <T> Map<String, Field> getAccessibleFieldsMap(Class<T> fieldsClass, String... fields){
        Map<String,Field> map = new HashMap<>();
        for (String s : fields) {
            map.put(s, getAccessibleField(fieldsClass, s));
        }
        return map;
    }
}
