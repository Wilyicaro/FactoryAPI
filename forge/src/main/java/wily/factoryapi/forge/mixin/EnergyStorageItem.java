package wily.factoryapi.forge.mixin;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.ICraftyStorageItem;
import wily.factoryapi.base.IEnergyStorageItem;
import wily.factoryapi.base.SimpleItemCraftyStorage;
import wily.factoryapi.forge.base.FactoryCapabilities;
import wily.factoryapi.forge.base.ForgeItemEnergyStorage;

@Mixin(IEnergyStorageItem.class)
public interface EnergyStorageItem extends IForgeItem {
    default IEnergyStorageItem<?> self(){
        return (IEnergyStorageItem<?>) this;
    }
    @Override
    @Nullable
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                IEnergyStorageItem<?> storageItem = self();
                if (capability == ForgeCapabilities.ENERGY)
                    return LazyOptional.of(()->new ForgeItemEnergyStorage(stack,0,storageItem.getCapacity(),storageItem.getMaxReceive(), storageItem.getMaxConsume(), storageItem.getTransport())).cast();
                if (capability == FactoryCapabilities.CRAFTY_ENERGY && storageItem instanceof ICraftyStorageItem craftyItem)
                    return LazyOptional.of(()->new SimpleItemCraftyStorage(stack,0,storageItem.getCapacity(),storageItem.getMaxReceive(), storageItem.getMaxConsume(), storageItem.getTransport(), craftyItem.getSupportedEnergyTier() ,stack.getItem() instanceof BlockItem)).cast();
                return LazyOptional.empty();
            }
        };
    }
}
