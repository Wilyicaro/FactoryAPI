package wily.factoryapi.forge;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.items.IItemHandler;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.forge.base.*;

import java.nio.file.Path;
import java.util.Optional;

public class FactoryAPIPlatformImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static IPlatformFluidHandler getItemFluidHandler(ItemStack container) {
        Optional<IFluidHandlerItem> opt = container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        return opt.isPresent() ? opt.get() instanceof IPlatformFluidHandler f ? f : (ForgeFluidHandlerPlatform) opt::get : null;
    }
    public static IPlatformEnergyStorage getItemEnergyStorage(ItemStack container) {
        Optional<IEnergyStorage> opt = container.getCapability(ForgeCapabilities.ENERGY).resolve();
        return opt.isPresent() ? opt.get() instanceof IPlatformEnergyStorage f ? f : (ForgeEnergyHandlerPlatform) opt::get : null;
    }

    public static Component getPlatformEnergyComponent() {
        return Component.literal("Forge Energy (FE)").withStyle(ChatFormatting.GREEN);
    }


    public static IFactoryStorage getPlatformFactoryStorage(BlockEntity be) {
        if (be instanceof IFactoryStorage st) return st;
        FactoryAPIPlatform.platformStorageWrappersCache.entrySet().removeIf(e->e.getKey().isRemoved());
        return FactoryAPIPlatform.platformStorageWrappersCache.computeIfAbsent(be, (be1)-> new IFactoryStorage() {
            @Override
            public <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
                Capability<?> capability = CapabilityUtil.storageToCapability(storage);
                if (capability != null && be.getCapability(capability, direction).isPresent()){
                    Object handler = be.getCapability(capability, direction).resolve().get();
                    if (storage == Storages.ENERGY)
                        return (()-> (T) (handler instanceof IPlatformEnergyStorage energyHandler ? energyHandler : (ForgeEnergyHandlerPlatform)()->(IEnergyStorage) handler));
                    else if (storage == Storages.CRAFTY_ENERGY && handler instanceof ICraftyEnergyStorage energyHandler)
                        return  (()-> (T) energyHandler);
                    else if (storage == Storages.ITEM)
                        return (()-> (T) (handler instanceof IPlatformItemHandler itemHandler ? itemHandler : (ForgeItemStoragePlatform) ()-> (IItemHandler) handler));
                    else if (storage == Storages.FLUID)
                        return (()-> (T) (handler instanceof IPlatformFluidHandler fluidHandler ? fluidHandler : (ForgeFluidHandlerPlatform) ()-> (IFluidHandler) handler));
                }
                return ArbitrarySupplier.empty();
            }
        });
    }

    public static ICraftyEnergyStorage getItemCraftyEnergyStorage(ItemStack container) {
        return container.getCapability(FactoryCapabilities.CRAFTY_ENERGY).orElse(null);
    }
}
