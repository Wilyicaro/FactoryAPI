package wily.factoryapi.fabriclike.util;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackHelper {
    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (!a.isEmpty() && a.sameItem(b) && a.hasTag() == b.hasTag()) {
            return (!a.hasTag() || a.getTag().equals(b.getTag()));
        } else {
            return false;
        }
    }
}
