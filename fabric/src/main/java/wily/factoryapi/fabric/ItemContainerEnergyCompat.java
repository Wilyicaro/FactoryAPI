package wily.factoryapi.fabric;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;
import wily.factoryapi.ItemContainerUtil;

public class ItemContainerEnergyCompat {
    public static boolean isEnergyContainer(ItemStack stack) {
        return Energy.valid(stack);
    }

    public static ItemContainerUtil.ItemEnergyContext insertEnergy(int energy, ItemStack stack, Player player) {
        if (isEnergyContainer(stack)) {
            EnergyHandler handler = Energy.of(stack);
            if (player != null) {
                if (!player.isCreative()) handler.simulate();
            }
            energy -= handler.insert(energy);
            return new ItemContainerUtil.ItemEnergyContext(energy, stack);
        }
        return new ItemContainerUtil.ItemEnergyContext(0,stack);
    }
    public static ItemContainerUtil.ItemEnergyContext extractEnergy(int energy, ItemStack stack ,Player player) {
        if (isEnergyContainer(stack)) {
            EnergyHandler handler = Energy.of(stack);
            int amount;
            if (player != null) {
                if (!player.isCreative()) handler.simulate();
            }
            amount = (int) handler.extract(energy);
            return new ItemContainerUtil.ItemEnergyContext(amount, stack);
        }
        return new ItemContainerUtil.ItemEnergyContext(0,stack);
    }

    public static int getEnergy(ItemStack stack) {
        if (isEnergyContainer(stack))
            return (int) Energy.of(stack).getEnergy();
        return 0;
    }

}
