package wily.factoryapi.fabric;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidBucketHooks;
import dev.architectury.hooks.fluid.fabric.FluidBucketHooksImpl;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.context.SingleSlotContainerItemContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.ItemContainerUtil;



public class ItemContainerUtilImpl {
    protected static boolean bucket(ContainerItemContext context){ return  (context.getItemVariant().getItem() instanceof BucketItem bucketItem) && FluidBucketHooksImpl.getFluid(bucketItem) != null;}

    public static boolean isFluidContainer(ItemStack stack){
        ContainerItemContext context = ContainerItemContext.withConstant(stack);
        return (bucket(context)) || context.find(FluidStorage.ITEM) != null;
    }

    public static FluidStack getFluid(ContainerItemContext context){
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);

        if(bucket(context)){
            BucketItem bucketItem = (BucketItem) context.getItemVariant().getItem();
            return FluidStack.create(FluidBucketHooks.getFluid(bucketItem), FluidConstants.BUCKET);
        }else if (handStorage != null) {
            for (StorageView<FluidVariant> view : handStorage ) {
                return FluidStackHooksFabric.fromFabric(view);
            }
        }
        return FluidStack.empty();
    }
    public static FluidStack getFluid(Player player, InteractionHand hand){
        return getFluid(ContainerItemContext.ofPlayerHand(player, hand));
    }
    public static FluidStack getFluid(ItemStack stack){
        return getFluid(ContainerItemContext.withConstant(stack));
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ItemStack stack, FluidStack fluidStack){
        ContainerItemContext context = ContainerItemContext.withInitial(stack);
        return fillItem(context,fluidStack,null);
    }
    public static long fillItem(FluidStack fluidStack, Player player, InteractionHand hand){
        ContainerItemContext context = ContainerItemContext.ofPlayerHand(player, hand);
        return fillItem(context,fluidStack,player).fluidStack().getAmount();
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ContainerItemContext context, FluidStack fluidStack, @Nullable Player player){
        StoragePreconditions.notBlankNotNegative(FluidStackHooksFabric.toFabric(fluidStack), fluidStack.getAmount()); // Defensive check, this is good practice.
        boolean bucket = context.getItemVariant().isOf(Items.BUCKET);
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);

        if(bucket) {
            if (fluidStack.getAmount() >= FluidStack.bucketAmount())
                try (Transaction transaction = Transaction.openOuter()) {
                    context.exchange(ItemVariant.of(fluidStack.getFluid().getBucket()), 1, transaction);
                    if (player != null) {
                        player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getFillSound(FluidStackHooksFabric.toFabric(fluidStack)), SoundSource.PLAYERS, 1.0F, 1.0F);

                        if (!player.isCreative()) transaction.commit();
                    }
                    fluidStack.setAmount(FluidConstants.BUCKET);
                    return new ItemContainerUtil.ItemFluidContext(fluidStack, context.getItemVariant().toStack((int) context.getAmount()));
                }

        }
        else if (handStorage != null)
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
        return new ItemContainerUtil.ItemFluidContext(FluidStack.empty(), context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ContainerItemContext context, @Nullable Player player){

        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);



        if(bucket(context)) {
            Fluid bucketFluid = FluidBucketHooks.getFluid((BucketItem)context.getItemVariant().getItem());
                try (Transaction transaction = Transaction.openOuter()) {
                    context.exchange(ItemVariant.of(Items.BUCKET), 1, transaction);
                    if (player != null) {
                        player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getEmptySound(FluidVariant.of(bucketFluid)), SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.isCreative()) {
                            transaction.commit();
                        }
                    }else transaction.commit();
                }
            return new ItemContainerUtil.ItemFluidContext(FluidStack.create(bucketFluid,FluidConstants.BUCKET), context.getItemVariant().toStack());

        }
        else if (handStorage != null){
            for (StorageView<FluidVariant> view : handStorage ) {
                if (view.isResourceBlank()) continue; // This means that the view contains no resource, represented by FluidVariant.blank().
                FluidVariant storedResource = view.getResource(); // Current resource
                try (Transaction transaction = Transaction.openOuter()) {
                    long amount = view.extract(storedResource ,maxDrain, transaction);
                    if (player != null) {
                        if (amount > 0)player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getEmptySound(storedResource), SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.isCreative()) {
                            transaction.commit();
                        }
                    }else transaction.commit();
                    return new ItemContainerUtil.ItemFluidContext(FluidStack.create(storedResource.getFluid(), amount),context.getItemVariant().toStack((int) context.getAmount()));

                }
            }
        }
        return new ItemContainerUtil.ItemFluidContext( FluidStack.empty(),context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static FluidStack drainItem(long maxDrain, Player player, InteractionHand hand){
        ContainerItemContext context = ContainerItemContext.ofPlayerHand(player, hand);
        return drainItem(maxDrain,context,player).fluidStack();
    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ItemStack stack){
        // While Fabric API don't add any context method that might alter itemStack, withInitial will be used instead
        ContainerItemContext context = ContainerItemContext.withInitial(stack);
        return drainItem(maxDrain, context,null);
    }

    public static boolean isEnergyContainer(ItemStack stack) {
        return ItemContainerEnergyCompat.isEnergyContainer(stack);
    }

    public static int insertEnergy(int energy, Player player, InteractionHand hand) {
        return ItemContainerEnergyCompat.insertEnergy(energy,ContainerItemContext.ofPlayerHand(player,hand),player).contextEnergy();
    }


    public static ItemContainerUtil.ItemEnergyContext insertEnergy(int energy, ItemStack stack) {
        return ItemContainerEnergyCompat.insertEnergy(energy,ContainerItemContext.withInitial(stack),null);
    }


    public static int extractEnergy(int energy, Player player, InteractionHand hand) {
        return ItemContainerEnergyCompat.extractEnergy(energy,ContainerItemContext.ofPlayerHand(player,hand),player).contextEnergy();
    }

    public static ItemContainerUtil.ItemEnergyContext extractEnergy(int energy, ItemStack stack) {
        return ItemContainerEnergyCompat.extractEnergy(energy,ContainerItemContext.withInitial(stack),null);
    }


    public static int getEnergy(ItemStack stack) {
        return ItemContainerEnergyCompat.getEnergy(stack);
    }


}
