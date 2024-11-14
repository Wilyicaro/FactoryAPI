package wily.factoryapi;


import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
//? if fabric {
import wily.factoryapi.base.fabric.FabricStorages;
//?} else if forge {
/*import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
*///?} else if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
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
import wily.factoryapi.init.FactoryRegistries;
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
        FactoryEvent.registerPayload(r-> r.register( false, FactoryAPICommand.UIDefinitionPayload.ID));
        FactoryEvent.preServerTick(s-> SECURE_EXECUTOR.executeAll());
        FactoryEvent.registerCommands(((commandSourceStackCommandDispatcher, commandBuildContext, commandSelection) -> FactoryAPICommand.register(commandSourceStackCommandDispatcher,commandBuildContext)));
        FactoryRegistries.init();
        FactoryEvent.setup(()->FactoryAPIPlatform.registerByClassArgumentType(FactoryAPICommand.JsonArgument.class, FactoryRegistries.JSON_ARGUMENT_TYPE.get()));
        //? if fabric {
        FabricStorages.registerDefaultStorages();
        //?}
    }
    public static ResourceLocation createLocation(String namespace, String path){
        //? if <1.20.5 {
        return new ResourceLocation(namespace,path);
        //?} else
        /*return ResourceLocation.tryBuild(namespace,path);*/
    }
    public static ResourceLocation createLocation(String location){
        //? if <1.20.5 {
        return new ResourceLocation(location);
         //?} else
        /*return ResourceLocation.tryParse(location);*/
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

    public enum Loader {
        FABRIC,FORGE,NEOFORGE;
        public boolean isForgeLike(){
            return this == FORGE || this == NEOFORGE;
        }
        public boolean isFabric(){
            return this == FABRIC;
        }
    }
}
