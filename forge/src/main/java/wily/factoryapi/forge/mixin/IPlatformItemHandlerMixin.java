package wily.factoryapi.forge.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformItemHandler;
@Mixin(IPlatformItemHandler.class)
public interface IPlatformItemHandlerMixin extends IPlatformItemHandler,IItemHandlerModifiable {
    @Override
    default int getSlots() {
        return getContainerSize();
    }

    @Override
    default @NotNull ItemStack getStackInSlot(int i) {
        return getItem(i);
    }

    @Override
    default void setStackInSlot(int i, @NotNull ItemStack arg) {
        setItem(i,arg);
    }

    @Override
    default int getSlotLimit(int i) {
        return getMaxStackSize();
    }

    @Override
    default boolean isItemValid(int i, @NotNull ItemStack arg) {
        return canPlaceItem(i,arg);
    }
}
