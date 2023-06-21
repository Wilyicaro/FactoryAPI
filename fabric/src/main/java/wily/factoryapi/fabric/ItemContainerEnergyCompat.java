package wily.factoryapi.fabric;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;
import wily.factoryapi.ItemContainerUtil;

public class ItemContainerEnergyCompat {
    public static boolean isEnergyContainer(ItemStack stack) {
        ContainerItemContext context = ContainerItemContext.withConstant(stack);
        return context.find(EnergyStorage.ITEM) != null;
    }

    public static ItemContainerUtil.ItemEnergyContext insertEnergy(int energy, ContainerItemContext context, Player player) {
        StoragePreconditions.notNegative(energy);
        EnergyStorage handStorage = context.find(EnergyStorage.ITEM);
        if (handStorage != null)
            try (Transaction transaction = Transaction.openOuter()) {
                try (Transaction nested = transaction.openNested()) {
                    energy = (int) handStorage.insert(energy, nested);
                    if (player == null ||!player.isCreative()) nested.commit();
                }
                transaction.commit();
                return new ItemContainerUtil.ItemEnergyContext(energy,context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemContainerUtil.ItemEnergyContext(0,context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static ItemContainerUtil.ItemEnergyContext extractEnergy(int energy, ContainerItemContext context, Player player) {
        StoragePreconditions.notNegative(energy);
        EnergyStorage handStorage = context.find(EnergyStorage.ITEM);
        if (handStorage != null)
            try (Transaction transaction = Transaction.openOuter()) {
                int amount;
                try (Transaction nested = transaction.openNested()) {
                    amount = (int) handStorage.extract(energy, nested);
                    if (player == null ||!player.isCreative()) nested.commit();
                }
                transaction.commit();
                return new ItemContainerUtil.ItemEnergyContext(amount,context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemContainerUtil.ItemEnergyContext(0,context.getItemVariant().toStack((int) context.getAmount()));
    }

    public static int getEnergy(ItemStack stack) {
        EnergyStorage handStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
        if (handStorage != null)
            return (int) handStorage.getAmount();
        return 0;
    }

}
