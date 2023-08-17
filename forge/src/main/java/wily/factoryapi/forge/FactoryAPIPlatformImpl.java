package wily.factoryapi.forge;

import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.forge.base.*;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

public class FactoryAPIPlatformImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
    public static IPlatformFluidHandler<?> getFluidHandlerApi(long Capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transportState) {

        return new ForgeFluidHandler(Capacity, be, validator, differential, transportState);
    }
    public static IPlatformItemHandler<?> getItemHandlerApi(int inventorySize, BlockEntity be) {

        return new ForgeItemHandler(inventorySize, be, TransportState.EXTRACT_INSERT);
    }

    public static IPlatformFluidHandler<?> getItemFluidHandler(ItemStack container) {
        return ItemContainerUtil.isFluidContainer(container)? (IPlatformFluidHandler<?>) container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().get() : null;
    }
    public static IPlatformEnergyStorage<?> getItemEnergyStorage(ItemStack container) {
        return ItemContainerUtil.isEnergyContainer(container)? (IPlatformEnergyStorage<?>) container.getCapability(ForgeCapabilities.ENERGY).resolve().get() : null;
    }


    public static IPlatformItemHandler<?> filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        return ForgeItemHandler.filtered(itemHandler,direction,slots,transportState);
    }


    public static IPlatformFluidHandler<?> filteredOf(IPlatformFluidHandler fluidHandler, TransportState transportState ) {
        return ForgeFluidHandler.filtered(fluidHandler,transportState);
    }

    public static IPlatformEnergyStorage<?> getEnergyStorageApi(int Capacity, BlockEntity be) {
        return new ForgeEnergyStorage(Capacity,be);
    }

    public static Component getPlatformEnergyComponent() {
        return Component.literal("Forge Energy (FE)").withStyle(ChatFormatting.GREEN);
    }

    public static IPlatformEnergyStorage<?> filteredOf(IPlatformEnergyStorage energyStorage, TransportState transportState) {
        return ForgeEnergyStorage.filtered(energyStorage,transportState);
    }

    public static IFactoryStorage getPlatformFactoryStorage(BlockEntity be) {
        if (be instanceof IFactoryStorage st) return st;
        return new IFactoryStorage() {
            @Override
            public <T extends IPlatformHandlerApi<?>> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction) {
                Capability<?> capability = CapabilityUtil.storageToCapability(storage);
                if (capability != null && be.getCapability(capability, direction).isPresent()){
                    Object handler = be.getCapability(capability, direction).resolve().get();
                    if (storage == Storages.ENERGY && handler instanceof IPlatformEnergyStorage<?> energyHandler)
                        return (Optional<T>) Optional.of(energyHandler);
                    if (storage == Storages.CRAFTY_ENERGY && handler instanceof ICraftyEnergyStorage energyHandler)
                        return (Optional<T>) Optional.of(energyHandler);
                    if (storage == Storages.ITEM && handler instanceof IPlatformItemHandler<?> itemHandler)
                        return (Optional<T>) Optional.of(itemHandler);
                    if (storage == Storages.FLUID && handler instanceof IPlatformFluidHandler<?> fluidHandler)
                        return (Optional<T>) Optional.of(fluidHandler);
                }
                return Optional.empty();
            }
        };
    }

    public static ICraftyEnergyStorage getItemCraftyEnergyStorage(ItemStack container) {
        return container.getCapability(FactoryCapabilities.CRAFTY_ENERGY).orElse(null);
    }
}
