package wily.factoryapi.forge.mixin;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.FactoryItemFluidHandler;
import wily.factoryapi.base.IFluidHandlerItem;

@Mixin(IFluidHandlerItem.class)
public interface FluidHandlerItem extends IForgeItem {
    default IFluidHandlerItem<?> self(){
        return (IFluidHandlerItem<?>) this;
    }
    @Override
    @Nullable
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                IFluidHandlerItem<?> storageItem = self();
                if (capability == ForgeCapabilities.FLUID_HANDLER_ITEM)
                    return LazyOptional.of(()->new FactoryItemFluidHandler(storageItem.getCapacity(), stack,storageItem::isFluidValid,storageItem.getTransport())).cast();
                return LazyOptional.empty();
            }
        };
    }
}
