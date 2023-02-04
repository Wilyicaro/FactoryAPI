package wily.factoryapi.util;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.IPlatformFluidHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StorageStringUtil {
    protected static String fluidMeasure = I18n.get("tooltip.factory_api.fluid"," ");

    public static String energyMeasure = I18n.get("tooltip.factory_api.energy", " ");

    public static String kiloEnergy = I18n.get("tooltip.factory_api.energy"," k");

    public static String millerEnergy = I18n.get("tooltip.factory_api.energy"," M");

    protected static String miliFluid = I18n.get("tooltip.factory_api.fluid"," m");


    public static MutableComponent getEnergyTooltip(String key, IPlatformEnergyStorage cell){
        return Component.translatable(key,  getStorageAmount( cell.getEnergyStored(), isShiftKeyDown(), millerEnergy,kiloEnergy, energyMeasure), getStorageAmount( cell.getMaxEnergyStored(), false,millerEnergy, kiloEnergy, energyMeasure)).withStyle(cell.getComponentStyle());
    }

    public static List<Component> getCompleteEnergyTooltip(String key, ICraftyEnergyStorage cell){
        List<Component> list = new ArrayList<>(List.of());
        list.add(getEnergyTooltip(key,cell));
        if (isShiftKeyDown() || cell.getStoredTier().isBurned()) list.add(cell.getStoredTier().getEnergyTierComponent());
        return list;
    }
    public static Component getFluidTooltip(String key, IPlatformFluidHandler tank){
        return getFluidTooltip(key, tank.getFluidStack(), tank.getMaxFluid()).withStyle(tank.identifier().color());
    }
    public static MutableComponent getFluidTooltip(String key, FluidStack stack, long maxFluid){
        if (stack.isFluidEqual(FluidStack.empty())) return Component.translatable("tooltip.factory_api.empty").withStyle(ChatFormatting.GRAY);
        return Component.translatable(key, stack.getName(), getStorageAmount((int) calculateFluid(stack.getAmount(),1000), isShiftKeyDown(),"", fluidMeasure, miliFluid), getStorageAmount((int) calculateFluid(maxFluid,1000), false,"", fluidMeasure, miliFluid));
    }
    public static String getStorageAmount(int content, boolean additionalBool, String minimal, String min, String max){

        String amount = formatMinAmount( (float) content / 1000) + min;
        if (content >= 1000000) amount = formatMinAmount( (float) content / 1000000) + minimal;
        if (additionalBool || content < 1000) amount = formatAmount(content) + max;
        return amount;
    }
    protected static String formatAmount(long i){
        return String.format( "%,d", i).replace(',', '.');
    }

    protected static String formatMinAmount(float i){ return  new DecimalFormat("0.####").format(Float.parseFloat(String.format(Locale.US,"%.1f",i)));}
    protected static long calculateFluid(long amount, int multiplier){
        return amount * multiplier / FluidStack.bucketAmount();
    }
    public static String getBetweenParenthesis(String name){
        String[] s = name.split(" ");
        return s[s.length -1].replaceAll("[()]","");
    }

    public static boolean isShiftKeyDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isKeyDown(int glfw) {
        InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(glfw);
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue()) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try {
                if (key.getType() == InputConstants.Type.KEYSYM) {
                    return InputConstants.isKeyDown(windowHandle, keyCode);
                } /**else if (key.getType() == InputMappings.Type.MOUSE) {
                 return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS;
                 }**/
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
