package wily.factoryapi.forge;


import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.forge.utils.FluidStackUtil;

public class ItemContainerUtilImpl {
    public static boolean isFluidContainer(ItemStack stack){
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
    }

    public static IFluidHandlerItem getItemFluidHandler(ItemStack stack){
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseThrow((() ->  new RuntimeException("Non FluidContainer")));
    }

    public static ItemStack getFluidContainer(ItemStack stack) {
        if (!isFluidContainer(stack)) return stack;
        return getItemFluidHandler(stack).getContainer();
    }

    public static FluidStack getFluid(Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);
        return getFluid(stack);
    }
    public static FluidStack getFluid(ItemStack stack){
        if (isFluidContainer(stack)) return FluidStackUtil.fromForge(stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().get().getFluidInTank(0));
        return FluidStack.empty();
    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ItemStack stack, Player player, InteractionHand hand, FluidStack fluidStack){
        if (!isFluidContainer(stack)) return new ItemContainerUtil.ItemFluidContext(FluidStack.empty(),stack);
        IFluidHandlerItem tank = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().get();
        long amount = tank.fill(FluidStackUtil.toForge(fluidStack), IFluidHandler.FluidAction.EXECUTE);
        if(player != null) {
            player.setItemInHand(hand, tank.getContainer());
            if(stack.getItem() instanceof BucketItem && fluidStack.getAmount().longValue() == FactoryAPIPlatformImpl.getBucketAmount())
                player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), fluidStack.getFluid().getAttributes().getFillSound(), SoundSource.PLAYERS, 0.6F, 0.8F);
}
        return new ItemContainerUtil.ItemFluidContext(FluidStack.create(fluidStack.getFluid(), Fraction.ofWhole(amount)),tank.getContainer());
    }
    public static long fillItem(FluidStack fluidStack, Player player, InteractionHand hand){
        return fillItem(player.getItemInHand(hand), player,hand,fluidStack).fluidStack.getAmount().longValue();

    }
    public static ItemContainerUtil.ItemFluidContext fillItem(ItemStack stack, FluidStack fluidStack){
        return fillItem(stack, null,null,fluidStack);
    }

    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ItemStack stack, Player player, InteractionHand hand){
        if (!isFluidContainer(stack)) return new ItemContainerUtil.ItemFluidContext(FluidStack.empty(),stack);
        IFluidHandlerItem tank = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().get();
        FluidStack fluidStack;

        IFluidHandler.FluidAction action = FluidStackUtil.FluidActionof((player !=null && player.isCreative()));
        fluidStack = FluidStackUtil.fromForge(tank.drain((int) maxDrain, action));

        if(player !=null) {
            if (stack.getCount() > 1  ) {
                net.minecraftforge.fluids.FluidStack fs = tank.getFluidInTank(0);
                int fluidAmount = (int) (Math.max(fs.getAmount() , maxDrain - fs.getAmount()));
                fluidStack = FluidStack.create(fluidAmount == 0 ? Fluids.EMPTY : fs.getFluid(), Fraction.ofWhole(fluidAmount));
                if (action.execute()) {
                    ItemStack newStack = stack.split(1);
                    player.setItemInHand(hand,stack);
                    getItemFluidHandler(newStack).drain(FluidStackUtil.toForge(fluidStack), action);
                    player.addItem(getItemFluidHandler(newStack).getContainer());
                }
            }else {
                player.setItemInHand(hand, tank.getContainer());
            }
            if (!fluidStack.isEmpty())player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), fluidStack.getFluid().getAttributes().getEmptySound(FluidStackUtil.toForge(fluidStack)), SoundSource.PLAYERS, 0.6F, 0.8F);
        }
        return new ItemContainerUtil.ItemFluidContext(fluidStack,tank.getContainer());
    }
    public static FluidStack drainItem(long maxDrain, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);
        return drainItem(maxDrain, stack, player, hand).fluidStack;

    }
    public static ItemContainerUtil.ItemFluidContext drainItem(long maxDrain, ItemStack stack){
        return drainItem(maxDrain, stack, null, null);
    }

    public static boolean isEnergyContainer(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
    }

    public static int insertEnergy(int energy, Player player, InteractionHand hand) {
        return  insertEnergy(energy, player.getItemInHand(hand)).contextEnergy;
    }

        public static ItemContainerUtil.ItemEnergyContext insertEnergy(int energy, ItemStack stack) {
            IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).resolve().get();
            return new ItemContainerUtil.ItemEnergyContext(energyStorage.receiveEnergy(energy,false),stack) ;
    }

    public static int extractEnergy(int energy, Player player, InteractionHand hand) {
        return extractEnergy(energy,player.getItemInHand(hand)).contextEnergy;
    }

    public static ItemContainerUtil.ItemEnergyContext extractEnergy(int energy, ItemStack stack) {
        if (!isEnergyContainer(stack)) return new ItemContainerUtil.ItemEnergyContext(0,stack);
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).resolve().get();
        return new ItemContainerUtil.ItemEnergyContext(energyStorage.extractEnergy(energy,false),stack) ;
    }

    public static int getEnergy(ItemStack stack) {
        if (!isEnergyContainer(stack)) return 0;
        return stack.getCapability(CapabilityEnergy.ENERGY).resolve().get().getEnergyStored();
    }
}
