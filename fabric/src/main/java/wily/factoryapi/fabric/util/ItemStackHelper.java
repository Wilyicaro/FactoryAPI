package wily.factoryapi.fabric.util;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackHelper {
    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        return ItemStack.isSameItemSameTags(a,b);
    }
}
