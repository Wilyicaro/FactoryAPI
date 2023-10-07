package wily.factoryapi.fabric;


import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.hooks.FluidBucketHooks;
import me.shedaniel.architectury.hooks.fabric.FluidBucketHooksImpl;
import me.shedaniel.architectury.utils.Fraction;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.fabric.util.FluidStackUtil;

public class ItemContainerUtilImpl {
    protected static boolean bucket(ContainerItemContext c){ return  (c.getItemVariant().getItem() instanceof BucketItem) && FluidBucketHooksImpl.getFluid(((BucketItem)c.getItemVariant().getItem())) != null;}

    public static boolean isFluidContainer(ItemStack stack){
        ContainerItemContext context = slotContextFromItemStack(stack);
        return context.find(FluidStorage.ITEM) != null;
    }

    public static FluidStack getFluid(ContainerItemContext context){
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);
        if (handStorage != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                for (StorageView<FluidVariant> view : handStorage.iterable(transaction))
                    return FluidStackUtil.fromFabric(view);
            }
        }
        return FluidStack.empty();
    }
    public static FluidStack getFluid(Player player, InteractionHand hand){
        return getFluid(ContainerItemContext.ofPlayerHand(player, hand));
    }
    public static FluidStack getFluid(ItemStack stack){
        return getFluid(slotContextFromItemStack(stack));
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ItemStack stack, FluidStack fluidStack){
        return fillItem(slotContextFromItemStack(stack),fluidStack,null);
    }
    public static long fillItem(FluidStack fluidStack, Player player, InteractionHand hand){
        ContainerItemContext context = ContainerItemContext.ofPlayerHand(player, hand);
        return fillItem(context,fluidStack,player).fluidStack.getAmount().longValue();
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ContainerItemContext context, FluidStack fluidStack, @Nullable Player player){
        StoragePreconditions.notBlankNotNegative(FluidStackUtil.toFabric(fluidStack), fluidStack.getAmount().longValue()); // Defensive check, this is good practice.
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);

        if (handStorage != null)
            try (Transaction transaction = Transaction.openOuter()) {
                try (Transaction nested = transaction.openNested()) {
                    fluidStack.setAmount(Fraction.ofWhole(handStorage.insert(FluidStackUtil.toFabric(fluidStack), fluidStack.getAmount().longValue(), nested)));
                    if (player != null) {
                        player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), fluidStack.getFluid() == Fluids.LAVA ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!player.isCreative()) nested.commit();
                    }else nested.commit();
                }
                transaction.commit();
                return new ItemContainerUtil.ItemFluidContext(fluidStack, context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemContainerUtil.ItemFluidContext( context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ContainerItemContext context, @Nullable Player player){

        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);


        if (handStorage != null){
            try (Transaction transaction = Transaction.openOuter()) {
                for (StorageView<FluidVariant> view : handStorage.iterable(transaction)) {
                    if (view.isResourceBlank())
                        continue; // This means that the view contains no resource, represented by FluidVariant.blank().
                    FluidVariant storedResource = view.getResource(); // Current resource
                    try (Transaction nested = transaction.openNested()) {
                        long amount = view.extract(storedResource, maxDrain, nested);
                        if (player != null) {
                            if (amount > 0)
                                player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), storedResource.getFluid() == Fluids.LAVA ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                            if (!player.isCreative()) {
                                nested.commit();
                            }
                        } else nested.commit();
                        transaction.commit();
                        return new ItemContainerUtil.ItemFluidContext(FluidStack.create(storedResource.getFluid(), Fraction.ofWhole(amount)), context.getItemVariant().toStack((int) context.getAmount()));
                    }
                }
            }
        }
        return new ItemContainerUtil.ItemFluidContext(context.getItemVariant().toStack((int) context.getAmount()));
    }
    public static FluidStack drainItem(long maxDrain, Player player, InteractionHand hand){
        ContainerItemContext context = ContainerItemContext.ofPlayerHand(player, hand);
        return drainItem(maxDrain,context,player).fluidStack;
    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ItemStack stack){
        return drainItem(maxDrain, slotContextFromItemStack(stack),null);
    }

    public static boolean isEnergyContainer(ItemStack stack) {
        return ItemContainerEnergyCompat.isEnergyContainer(stack);
    }

    public static int insertEnergy(int energy, Player player, InteractionHand hand) {
        return ItemContainerEnergyCompat.insertEnergy(energy,player.getItemInHand(hand),player).contextEnergy;
    }


    public static ItemContainerUtil.ItemEnergyContext insertEnergy(int energy, ItemStack stack) {
        return ItemContainerEnergyCompat.insertEnergy(energy,stack,null);
    }


    public static int extractEnergy(int energy, Player player, InteractionHand hand) {
        return ItemContainerEnergyCompat.extractEnergy(energy,player.getItemInHand(hand),player).contextEnergy;
    }

    public static ItemContainerUtil.ItemEnergyContext extractEnergy(int energy, ItemStack stack) {
        return ItemContainerEnergyCompat.extractEnergy(energy,stack,null);
    }


    public static int getEnergy(ItemStack stack) {
        return ItemContainerEnergyCompat.getEnergy(stack);
    }

    public static ContainerItemContext slotContextFromItemStack(ItemStack stack){
        return ContainerItemContext.ofSingleSlot(InventoryStorage.of(new SimpleContainer(stack),null).getSlot(0));
    }

}
