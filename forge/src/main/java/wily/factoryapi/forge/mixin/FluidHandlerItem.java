package wily.factoryapi.forge.mixin;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IFluidHandlerItem;
import wily.factoryapi.forge.base.ForgeItemFluidHandler;

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
                if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                    return LazyOptional.of(()->new ForgeItemFluidHandler(storageItem.getCapacity(), stack,storageItem::isFluidValid,storageItem.getTransport())).cast();
                return LazyOptional.empty();
            }
        };
    }
}
