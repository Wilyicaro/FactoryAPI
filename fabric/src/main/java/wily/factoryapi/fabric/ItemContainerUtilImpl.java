package wily.factoryapi.fabric;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidBucketHooksImpl;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.ItemContainerUtil;


public class ItemContainerUtilImpl {
    protected static boolean bucket(ContainerItemContext context){ return  (context.getItemVariant().getItem() instanceof BucketItem bucketItem) && FluidBucketHooksImpl.getFluid(bucketItem) != null;}

    public static boolean isFluidContainer(ItemStack stack){
        ContainerItemContext context = modifiableStackContext(stack);
        return context.find(FluidStorage.ITEM) != null;
    }

    public static FluidStack getFluid(ContainerItemContext context){
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);
        if (handStorage != null) {
            for (StorageView<FluidVariant> view : handStorage )
                return FluidStackHooksFabric.fromFabric(view);
        }
        return FluidStack.empty();
    }
    public static FluidStack getFluid(Player player, InteractionHand hand){
        return getFluid(ContainerItemContext.ofPlayerHand(player, hand));
    }
    public static FluidStack getFluid(ItemStack stack){
        return getFluid(modifiableStackContext(stack));
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ItemStack stack, FluidStack fluidStack){
        return fillItem(fluidStack, stack, modifiableStackContext(stack), null);
    }
    public static long fillItem(FluidStack fluidStack, Player player, InteractionHand hand){
        ContainerItemContext context = ContainerItemContext.ofPlayerHand(player, hand);
        return fillItem(fluidStack, player.getItemInHand(hand), context, player).fluidStack().getAmount();
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(FluidStack fluidStack, ItemStack stack, ContainerItemContext context, @Nullable Player player){
        StoragePreconditions.notBlankNotNegative(FluidStackHooksFabric.toFabric(fluidStack), fluidStack.getAmount());
        Storage<FluidVariant> handStorage = FluidStorage.ITEM.find(stack,context);

        if (handStorage != null)
            try (Transaction transaction = Transaction.openOuter()) {
                try (Transaction nested = transaction.openNested()) {
                    fluidStack.setAmount(handStorage.insert(FluidStackHooksFabric.toFabric(fluidStack), fluidStack.getAmount(), nested));
                    if (player != null) {
                         player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getFillSound(FluidStackHooksFabric.toFabric(fluidStack)), SoundSource.PLAYERS, 1.0F, 1.0F);
                        if (!player.isCreative()) nested.commit();
                    }else nested.commit();
                }
                transaction.commit();
                return new ItemContainerUtil.ItemFluidContext(fluidStack, context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemContainerUtil.ItemFluidContext( context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ItemStack stack, ContainerItemContext context, @Nullable Player player){
        Storage<FluidVariant> handStorage = FluidStorage.ITEM.find(stack,context);

       if (handStorage != null){
            for (StorageView<FluidVariant> view : handStorage) {
                if (view.isResourceBlank()) continue;
                FluidVariant storedResource = view.getResource();
                try (Transaction transaction = Transaction.openOuter()) {
                    long amount;
                    try (Transaction nested = transaction.openNested()) {
                        amount = view.extract(storedResource, maxDrain, nested);
                        if (player != null) {
                            if (amount > 0) player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getEmptySound(storedResource), SoundSource.BLOCKS, 1.0F, 1.0F);
                            if (!player.isCreative()) nested.commit();
                        } else nested.commit();
                    }
                    transaction.commit();
                    return new ItemContainerUtil.ItemFluidContext(FluidStack.create(storedResource.getFluid(), amount), context.getItemVariant().toStack((int) context.getAmount()));
                }
            }
        }
        return new ItemContainerUtil.ItemFluidContext(context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static FluidStack drainItem(long maxDrain, Player player, InteractionHand hand){
        ContainerItemContext context = ContainerItemContext.ofPlayerHand(player, hand);
        return drainItem(maxDrain,player.getItemInHand(hand), context,player).fluidStack();
    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ItemStack stack){
        return drainItem(maxDrain, stack, modifiableStackContext(stack),null);
    }

    public static boolean isEnergyContainer(ItemStack stack) {
        return ItemContainerEnergyCompat.isEnergyContainer(stack);
    }

    public static int insertEnergy(int energy, Player player, InteractionHand hand) {
        return ItemContainerEnergyCompat.insertEnergy(energy,ContainerItemContext.ofPlayerHand(player,hand),player).contextEnergy();
    }


    public static ItemContainerUtil.ItemEnergyContext insertEnergy(int energy, ItemStack stack) {
        return ItemContainerEnergyCompat.insertEnergy(energy, modifiableStackContext(stack),null);
    }


    public static int extractEnergy(int energy, Player player, InteractionHand hand) {
        return ItemContainerEnergyCompat.extractEnergy(energy,ContainerItemContext.ofPlayerHand(player,hand),player).contextEnergy();
    }

    public static ItemContainerUtil.ItemEnergyContext extractEnergy(int energy, ItemStack stack) {
        return ItemContainerEnergyCompat.extractEnergy(energy, modifiableStackContext(stack),null);
    }

    public static int getEnergy(ItemStack stack) {
        return ItemContainerEnergyCompat.getEnergy(stack);
    }

    public static ContainerItemContext modifiableStackContext(ItemStack stack) {
        return ContainerItemContext.ofSingleSlot(new SingleItemStorage() {
            ItemStack itemStack = stack;
            @Override
            public ItemVariant getResource() {
                return ItemVariant.of(itemStack);
            }
            public long getAmount() {
                return itemStack.getCount();
            }
            @Override
            public boolean isResourceBlank() {
                return itemStack.isEmpty();
            }
            @Override
            protected long getCapacity(ItemVariant variant) {
                return itemStack.getMaxStackSize();
            }
            @Override
            public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
                if (insertedVariant.isOf(itemStack.getItem())) {
                    int oldCount = itemStack.getCount();
                    itemStack.grow(Math.min(stack.getMaxStackSize(), (int) maxAmount));
                    return itemStack.getCount() - oldCount;
                }else if (itemStack.isEmpty())
                    return (itemStack = insertedVariant.toStack(Math.min(insertedVariant.getItem().getMaxStackSize(),(int) maxAmount))).getCount();
                return 0;
            }
            @Override
            public long extract(ItemVariant extractedVariant, long maxAmount, TransactionContext transaction) {
                StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);
                if (extractedVariant.isOf(itemStack.getItem())) {
                    int oldCount = itemStack.getCount();
                    itemStack.shrink(Math.max(itemStack.getCount(),(int)maxAmount));
                    return oldCount - itemStack.getCount();
                }
                return 0;
            }
        });
    }
}
