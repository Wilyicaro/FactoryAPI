package wily.factoryapi;

//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;
//?} else if forge {
/*import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
*///?} else if neoforge {
/*import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
*///?}
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.util.FluidInstance;

public interface ItemContainerPlatform {

    record ItemFluidContext(FluidInstance fluidInstance, ItemStack container) {
        public ItemFluidContext(ItemStack container){
            this(FluidInstance.empty(),container);
        }
    }
    record ItemEnergyContext(int contextEnergy, ItemStack container) {
        public ItemEnergyContext(ItemStack container){
            this(0,container);
        }
    }

    static boolean isBlockItem(ItemStack s){return s.getItem() instanceof BlockItem;}

    static boolean isFluidContainer(ItemStack stack){
        //? if fabric {
        return modifiableStackContext(stack).find(FluidStorage.ITEM) != null;
        //?} elif forge || neoforge {
        /*return !stack.isEmpty() && getItemFluidHandler(stack) != null;
        *///?} else
        /*throw new AssertionError();*/
    }

    static boolean isEnergyContainer(ItemStack stack){
        //? if fabric {
        return modifiableStackContext(stack).find(EnergyStorage.ITEM) != null;
        //?} elif forge || neoforge {
        /*return !stack.isEmpty() && getItemEnergyStorage(stack) != null;
        *///?} else
        /*throw new AssertionError();*/
    }

    static FluidInstance getFluid(ItemStack stack){
        //? if fabric {
        return getFluid(modifiableStackContext(stack));
        //?} elif forge || neoforge {
        /*return isFluidContainer(stack) ? FactoryAPIPlatform.fluidStackToInstance(getItemFluidHandler(stack).getFluidInTank(0)) : FluidInstance.empty();
        *///?} else
        /*throw new AssertionError();*/
    }

    static FluidInstance getFluid(Player player, InteractionHand hand){
        //? if fabric {
        return getFluid(ContainerItemContext.ofPlayerHand(player, hand));
        //?} elif forge || neoforge {
        /*return getFluid(player.getItemInHand(hand));
        *///?} else
        /*throw new AssertionError();*/
    }

    static int fillItem(FluidInstance fluidInstance, Player player, InteractionHand hand){
        //? if fabric {
        return fillItem(fluidInstance, player.getItemInHand(hand), ContainerItemContext.ofPlayerHand(player, hand), player).fluidInstance().getAmount();
         //?} elif forge || neoforge {
        /*return fillItem(player.getItemInHand(hand), player,hand,fluidInstance).fluidInstance().getAmount();
        *///?} else
        /*throw new AssertionError();*/
    }

    static ItemFluidContext fillItem(ItemStack stack, FluidInstance fluidInstance){
        //? if fabric {
        return fillItem(fluidInstance, stack, modifiableStackContext(stack), null);
         //?} elif forge || neoforge {
        /*return fillItem(stack, null, null,fluidInstance);
        *///?} else
        /*throw new AssertionError();*/
    }

    static FluidInstance drainItem(int maxDrain, Player player, InteractionHand hand){
        //? if fabric {
        return drainItem(maxDrain,player.getItemInHand(hand), ContainerItemContext.ofPlayerHand(player, hand), player).fluidInstance();
         //?} elif forge || neoforge {
        /*return drainItem(maxDrain, player.getItemInHand(hand), player, hand).fluidInstance();
        *///?} else
        /*throw new AssertionError();*/
    }

    static ItemFluidContext drainItem(int maxDrain, ItemStack stack){
        //? if fabric {
        return drainItem(maxDrain, stack, modifiableStackContext(stack),null);
         //?} elif forge || neoforge {
        /*return drainItem(maxDrain, stack, null, null);
        *///?} else
        /*throw new AssertionError();*/
    }

    static int insertEnergy(int energy, Player player, InteractionHand hand){
        //? if fabric {
        return insertEnergy(energy,ContainerItemContext.ofPlayerHand(player,hand),player).contextEnergy();
         //?} elif forge || neoforge {
        /*return insertEnergy(energy, player.getItemInHand(hand)).contextEnergy();
        *///?} else
        /*throw new AssertionError();*/
    }

    static ItemEnergyContext insertEnergy(int energy, ItemStack stack){
        //? if fabric {
        return insertEnergy(energy, modifiableStackContext(stack),null);
         //?} elif forge || neoforge {
        /*return new ItemEnergyContext(getItemEnergyStorage(stack).receiveEnergy(energy,false),stack);
        *///?} else
        /*throw new AssertionError();*/
    }

    static int extractEnergy(int energy, Player player, InteractionHand hand){
        //? if fabric {
        return extractEnergy(energy,ContainerItemContext.ofPlayerHand(player,hand),player).contextEnergy();
         //?} elif forge || neoforge {
        /*return extractEnergy(energy, player.getItemInHand(hand)).contextEnergy();
        *///?} else
        /*throw new AssertionError();*/
    }

    static ItemEnergyContext extractEnergy(int energy, ItemStack stack){
        //? if fabric {
        return extractEnergy(energy, modifiableStackContext(stack),null);
         //?} elif forge || neoforge {
        /*return new ItemEnergyContext(getItemEnergyStorage(stack).extractEnergy(energy,false),stack);
        *///?} else
        /*throw new AssertionError();*/
    }

    static int getEnergy(ItemStack stack){
        //? if fabric {
        EnergyStorage handStorage = modifiableStackContext(stack).find(EnergyStorage.ITEM);
        if (handStorage != null)
            return (int) handStorage.getAmount();
        return 0;
         //?} elif forge || neoforge {
        /*return isEnergyContainer(stack) ? 0 : getItemEnergyStorage(stack).getEnergyStored();
        *///?} else
        /*throw new AssertionError();*/
    }

    //? if fabric {
    static ContainerItemContext modifiableStackContext(ItemStack stack) {
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
    static FluidInstance getFluid(ContainerItemContext context){
        Storage<FluidVariant> handStorage = context.find(FluidStorage.ITEM);
        if (handStorage != null) {
            for (StorageView<FluidVariant> view : handStorage )
                return FluidInstance.create(view.getResource().getFluid(),view.getAmount());
        }
        return FluidInstance.empty();
    }
        static ItemFluidContext fillItem(FluidInstance fluidInstance, ItemStack stack, ContainerItemContext context, @Nullable Player player){
        StoragePreconditions.notBlankNotNegative(FluidVariant.of(fluidInstance.getFluid()), fluidInstance.getAmount());
        Storage<FluidVariant> handStorage = FluidStorage.ITEM.find(stack,context);

        if (handStorage != null)
            try (Transaction transaction = Transaction.openOuter()) {
                try (Transaction nested = transaction.openNested()) {
                    fluidInstance.setAmount(handStorage.insert(FluidVariant.of(fluidInstance.getFluid()), fluidInstance.getAmount(), nested));
                    if (player != null) {
                         player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getFillSound(FluidVariant.of(fluidInstance.getFluid())), SoundSource.PLAYERS, 1.0F, 1.0F);
                        if (!player.isCreative()) nested.commit();
                    }else nested.commit();
                }
                transaction.commit();
                return new ItemFluidContext(fluidInstance, context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemFluidContext( context.getItemVariant().toStack((int) context.getAmount()));
    }
    static ItemFluidContext drainItem(int maxDrain, ItemStack stack, ContainerItemContext context, @Nullable Player player){
        Storage<FluidVariant> handStorage = FluidStorage.ITEM.find(stack,context);

       if (handStorage != null){
            for (StorageView<FluidVariant> view : handStorage) {
                if (view.isResourceBlank()) continue;
                FluidVariant storedResource = view.getResource();
                try (Transaction transaction = Transaction.openOuter()) {
                    long amount;
                    try (Transaction nested = transaction.openNested()) {
                        amount = view.extract(storedResource, FluidInstance.getPlatformFluidAmount(maxDrain), nested);
                        if (player != null) {
                            if (amount > 0) player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), FluidVariantAttributes.getEmptySound(storedResource), SoundSource.BLOCKS, 1.0F, 1.0F);
                            if (!player.isCreative()) nested.commit();
                        } else nested.commit();
                    }
                    transaction.commit();
                    return new ItemFluidContext(FluidInstance.create(storedResource.getFluid(), amount), context.getItemVariant().toStack((int) context.getAmount()));
                }
            }
        }
        return new ItemFluidContext(context.getItemVariant().toStack((int) context.getAmount()));
    }
      static ItemEnergyContext insertEnergy(int energy, ContainerItemContext context, Player player) {
        StoragePreconditions.notNegative(energy);
        EnergyStorage handStorage = context.find(EnergyStorage.ITEM);
        if (handStorage != null)
            try (Transaction transaction = Transaction.openOuter()) {
                try (Transaction nested = transaction.openNested()) {
                    energy = (int) handStorage.insert(energy, nested);
                    if (player == null ||!player.isCreative()) nested.commit();
                }
                transaction.commit();
                return new ItemEnergyContext(energy,context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemEnergyContext(0,context.getItemVariant().toStack((int) context.getAmount()));
    }
    static ItemEnergyContext extractEnergy(int energy, ContainerItemContext context, Player player) {
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
                return new ItemEnergyContext(amount,context.getItemVariant().toStack((int) context.getAmount()));
            }
        return new ItemEnergyContext(0,context.getItemVariant().toStack((int) context.getAmount()));
    }
    //?} elif forge {
    /*static IFluidHandlerItem getItemFluidHandler(ItemStack stack){
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
    }
    static IEnergyStorage getItemEnergyStorage(ItemStack stack){
        return stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
    }
    *///?} elif neoforge {
    /*static IFluidHandlerItem getItemFluidHandler(ItemStack stack){
        return stack.getCapability(Capabilities.FluidHandler.ITEM);
    }
    static IEnergyStorage getItemEnergyStorage(ItemStack stack){
        return stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }
    *///?}
    //? if forge || neoforge {
    /*static ItemFluidContext fillItem(ItemStack stack, Player player, InteractionHand hand, FluidInstance fluidInstance){
        if (!isFluidContainer(stack) || (player == null && stack.getCount() != 1)) return new ItemFluidContext(stack);
        IFluidHandler.FluidAction action = FactoryAPIPlatform.fluidActionOf((player !=null && player.isCreative()));
        ItemStack toFill = action.execute() ? stack.copyWithCount(1) : stack.copy();
        IFluidHandlerItem tank = getItemFluidHandler(toFill);
        int amount = tank.fill(fluidInstance, action);
        if(player != null && amount > 0) {
            if (action.execute()) {
                if (stack.getCount() > 1) {
                    player.setItemInHand(hand, stack.copyWithCount(stack.getCount() - 1));
                    player.addItem(tank.getContainer());
                } else player.setItemInHand(hand, tank.getContainer());
            }
            SoundEvent sound = fluidInstance.getFluid().getFluidType().getSound(fluidInstance, SoundActions.BUCKET_FILL);
            if (sound != null) player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),sound , SoundSource.PLAYERS, 0.6F, 0.8F);
        }
        return new ItemFluidContext(FluidInstance.create(fluidInstance.getFluid(),amount),tank.getContainer());
    }
    static ItemFluidContext drainItem(int maxDrain, ItemStack stack, Player player, InteractionHand hand){
        if (!isFluidContainer(stack) || (player == null && stack.getCount() != 1)) return new ItemFluidContext(stack);
        IFluidHandler.FluidAction action = FactoryAPIPlatform.fluidActionOf((player !=null && player.isCreative()));
        ItemStack toDrain = action.execute() ? stack.copyWithCount(1) : stack.copy();
        IFluidHandlerItem tank = getItemFluidHandler(toDrain);
        FluidStack fluidStack = tank.drain(maxDrain, action);
        if(player != null && fluidStack.getAmount() > 0) {
            if (action.execute()) {
                if (stack.getCount() > 1) {
                    player.setItemInHand(hand, stack.copyWithCount(stack.getCount() - 1));
                    player.addItem(tank.getContainer());
                } else player.setItemInHand(hand, tank.getContainer());
            }
            SoundEvent sound = fluidStack.getFluid().getFluidType().getSound(fluidStack, SoundActions.BUCKET_EMPTY);
            if (sound != null) player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),sound , SoundSource.PLAYERS, 0.6F, 0.8F);
        }

        return new ItemFluidContext(FluidInstance.create(fluidStack.getFluid(),fluidStack.getAmount()),tank.getContainer());
    }
    *///?}
}
