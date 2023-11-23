package wily.factoryapi.util;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.IPlatformFluidHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StorageStringUtil {

    public static final String DEFAULT_CRAFTY_ENERGY = "tooltip.factory_api.crafty_energy";

    public static final String DEFAULT_ENERGY_SUFFIX = getBetweenParenthesis(FactoryAPIPlatform.getPlatformEnergyComponent().getString());
    public static final String DEFAULT_FLUID = "tooltip.factory_api.fluid";
    protected static String fluidMeasure = I18n.get(DEFAULT_FLUID," ");
    protected static String milliFluid = I18n.get(DEFAULT_FLUID," m");

    protected static String kiloFluid = I18n.get(DEFAULT_FLUID," k");

    public static String energyMeasure = " " + DEFAULT_ENERGY_SUFFIX;

    public static String kiloEnergy = " k" + DEFAULT_ENERGY_SUFFIX;

    public static String megaEnergy = " M" + DEFAULT_ENERGY_SUFFIX;

    public static String CYMeasure = I18n.get(DEFAULT_CRAFTY_ENERGY, " ");

    public static String kiloCY = I18n.get(DEFAULT_CRAFTY_ENERGY," k");

    public static String megaCY = I18n.get(DEFAULT_CRAFTY_ENERGY," M");



    public static MutableComponent getEnergyTooltip(String key, IPlatformEnergyStorage<?> cell){
        return getEnergyTooltip(key,cell, energyMeasure, kiloEnergy, megaEnergy);
    }
    public static MutableComponent getEnergyTooltip(String key, ICraftyEnergyStorage cell){
        return getEnergyTooltip(key,cell, CYMeasure, kiloCY, megaCY);
    }
    public static MutableComponent getEnergyTooltip(String key, IPlatformEnergyStorage<?> cell, String... measures){
        return new TranslatableComponent(key,  getStorageAmount( cell.getEnergyStored(), isShiftKeyDown(), measures), getStorageAmount( cell.getMaxEnergyStored(), false,measures)).withStyle(cell.getComponentStyle());
    }
    public static MutableComponent getMaxCraftyTransferTooltip(int energyPerTick){
        return new TranslatableComponent("tooltip.factory_api.max_transfer",  getStorageAmount(energyPerTick, isShiftKeyDown(), CYMeasure, kiloCY, megaCY));
    }
    public static MutableComponent getMaxEnergyTransferTooltip(int energyPerTick){
        return new TranslatableComponent("tooltip.factory_api.max_transfer",  getStorageAmount(energyPerTick, isShiftKeyDown(), energyMeasure, kiloEnergy, megaEnergy)).withStyle(ChatFormatting.AQUA);
    }
    public static MutableComponent getMaxFluidTransferTooltip(long fluidPerTick){
        return new TranslatableComponent("tooltip.factory_api.max_transfer",  getStorageAmount(fluidPerTick, isShiftKeyDown(),milliFluid, fluidMeasure, kiloFluid)).withStyle(ChatFormatting.GRAY);
    }
    public static List<Component> getCompleteEnergyTooltip(String key, @Nullable Component burned, ICraftyEnergyStorage cell){
        List<Component> list = new ArrayList<>(List.of());
        if (burned == null || !cell.getStoredTier().isBurned())list.add(getEnergyTooltip(key,cell));
        else list.add(burned.copy().withStyle(ChatFormatting.DARK_RED));
        list.add(cell.getStoredTier().getEnergyTierComponent(true));
        if (isShiftKeyDown()) list.add(cell.getSupportedTier().getEnergyTierComponent(false));
        return list;
    }
    public static List<Component> getCompleteEnergyTooltip(String key, ICraftyEnergyStorage cell) {
        return getCompleteEnergyTooltip(key,null,cell);
    }
    public static Component getFluidTooltip(String key, IPlatformFluidHandler<?> tank){
        return getFluidTooltip(key,tank,true);
    }
    public static Component getFluidTooltip(String key, IPlatformFluidHandler<?> tank, boolean showEmpty){
        return getFluidTooltip(key, tank.getFluidStack(), tank.getMaxFluid(),showEmpty).withStyle(tank.identifier().color());
    }
    public static MutableComponent getFluidTooltip(String key, FluidStack stack, long maxFluid, boolean showEmpty){
        if (stack.isEmpty() && !isShiftKeyDown() && showEmpty) return new TranslatableComponent("tooltip.factory_api.empty").withStyle(ChatFormatting.GRAY);
        return new TranslatableComponent(key, stack.getName(), getStorageAmount(calculateFluid(stack.getAmount(),1000), isShiftKeyDown(),milliFluid, fluidMeasure, kiloFluid), getStorageAmount(calculateFluid(maxFluid,1000), false,milliFluid, fluidMeasure, kiloFluid));
    }
    public static String getStorageAmount(long content, boolean additionalBool, String... measures){
        if (content == Integer.MAX_VALUE) return "∞";
        String amount = "";
        for (int i = measures.length - 1; i >= 1; i--) {
            float min = (float) Math.pow(1000,i);
            if (content >= min){
                amount = formatMinAmount( content / min) + measures[i];
                break;
            }
        }
        if (additionalBool || content < 1000) amount = formatAmount(content) + measures[0];
        return amount;
    }
    public static String formatAmount(long i){
        return String.format( "%,d", i).replace(',', '.');
    }

    public static String formatMinAmount(float i){ return  new DecimalFormat("0.####").format(Float.parseFloat(String.format(Locale.US,"%.1f",i)));}
    public static long calculateFluid(long amount, int multiplier){
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
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
