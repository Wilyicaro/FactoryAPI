package wily.factoryapi.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.Storages;
import wily.factoryapi.forge.base.CapabilityUtil;

@Mod(FactoryAPI.MOD_ID)
@Mod.EventBusSubscriber(modid = FactoryAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactoryAPIForge {
    public FactoryAPIForge() {
        EventBuses.registerModEventBus(FactoryAPI.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FactoryAPI.init();
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event){
        if (event.getObject() instanceof IFactoryStorage be){
            event.addCapability(new ResourceLocation(FactoryAPI.MOD_ID, "factory_api_capabilities"), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                    Storages.Storage<?> storage = CapabilityUtil.capabilityToStorage(capability);
                    ArbitrarySupplier<? extends IPlatformHandlerApi<?>> handler =  be.getStorage(storage,arg);
                    if (storage != null && handler.isPresent())
                        return LazyOptional.of(()->handler.get().getHandler()).cast();
                    return LazyOptional.empty();
                }
            });
        }
    }
}
