package wily.factoryapi;

import dev.architectury.fluid.FluidStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemContainerUtil {

    public record ItemFluidContext(FluidStack fluidStack, ItemStack container) {

        }
    public record ItemEnergyContext(int contextEnergy, ItemStack container) {

    }
    @ExpectPlatform
    public static boolean isFluidContainer(ItemStack stack){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isEnergyContainer(ItemStack stack){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static FluidStack getFluid(ItemStack stack){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static FluidStack getFluid(Player player, InteractionHand hand){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static long fillItem(FluidStack fluidStack, Player player, InteractionHand hand){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static ItemFluidContext fillItem(ItemStack stack, FluidStack fluidStack){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static FluidStack drainItem(long maxDrain,Player player, InteractionHand hand){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static ItemFluidContext drainItem(long maxDrain, ItemStack stack){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int insertEnergy(int energy, Player player, InteractionHand hand){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static ItemEnergyContext insertEnergy(int energy, ItemStack stack){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static int extractEnergy(int energy,Player player, InteractionHand hand){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static ItemEnergyContext extractEnergy(int energy, ItemStack stack){
        throw new AssertionError();
    }
    @ExpectPlatform
    public static int getEnergy(ItemStack stack){
        throw new AssertionError();
    }
}
