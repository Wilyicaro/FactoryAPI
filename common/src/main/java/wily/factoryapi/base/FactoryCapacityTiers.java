package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import wily.factoryapi.FactoryAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum FactoryCapacityTiers {

    BURNED(new TranslatableComponent("tier."+ FactoryAPI.MOD_ID + ".burned").withStyle(ChatFormatting.DARK_RED),0, 0 ,0),
    BASIC(new TranslatableComponent("tier."+ FactoryAPI.MOD_ID + ".basic").withStyle(ChatFormatting.GRAY),0.2, 800 ,1),
    ADVANCED(new TranslatableComponent("tier."+FactoryAPI.MOD_ID + ".advanced").withStyle(ChatFormatting.RED),0.6, 2000,3),
    HIGH(new TranslatableComponent("tier."+ FactoryAPI.MOD_ID + ".high").withStyle(ChatFormatting.BLUE),0.5, 4000,8),
    ULTIMATE(new TranslatableComponent("tier."+FactoryAPI.MOD_ID + ".ultimate").withStyle(ChatFormatting.DARK_PURPLE),0.63, 6000,12),
    QUANTUM(new TranslatableComponent("tier."+FactoryAPI.MOD_ID + ".quantum").withStyle(ChatFormatting.DARK_AQUA),0.8, 10000,16),
    CREATIVE(new TranslatableComponent("tier."+FactoryAPI.MOD_ID + ".creative").withStyle(ChatFormatting.LIGHT_PURPLE),1.0, Integer.MAX_VALUE, Integer.MAX_VALUE);


    private final double conductivity;
    public final int initialCapacity;

    public final int capacityMultiplier;

    public final Component localizedName;

    FactoryCapacityTiers(Component name, double j, int initialCapacity, int multiplier) {
        this.localizedName = name;
        this.conductivity = j;
        this.initialCapacity = initialCapacity;
        this.capacityMultiplier = multiplier;
    }

    public MutableComponent getEnergyTierComponent(boolean isStored){
        return getPrefixComponent("energy",isStored).withStyle(ChatFormatting.AQUA).append(localizedName);
    }
    public MutableComponent getOutputTierComponent(){
        return getPrefixComponent("energy",I18n.get("tier.factory_api.output")).withStyle(ChatFormatting.AQUA).append(localizedName);
    }
    public MutableComponent getTierComponent(boolean isStored){
        return getPrefixComponent("capacity",isStored).withStyle(ChatFormatting.GRAY).append(localizedName);
    }
    public MutableComponent getPrefixComponent(String keyType, boolean isStored){
        if (isStored) return getPrefixComponent(keyType,I18n.get("tier.factory_api.stored"));
        else return getPrefixComponent(keyType,"");
    }
    public MutableComponent getPrefixComponent(String keyType, Object... objects){
        return new TranslatableComponent("tier.factory_api.display",I18n.get("tier.factory_api." + keyType, objects));
    }
    public boolean supportTier(FactoryCapacityTiers tier){return ordinal() >= tier.ordinal();}
    public double getConductivity() {
        return this.conductivity;
    }
    public double getPowFactor() {return Math.pow(conductivity,2);}
    public boolean isBurned(){return this == BURNED;}

    public int getDefaultCapacity() {
        return this.initialCapacity * 10;
    }
    public int getStorageCapacity(){
        return getDefaultCapacity() * capacityMultiplier;
    }

    public FactoryCapacityTiers increase(int ordinal){
        return FactoryCapacityTiers.values()[Math.min(values().length-1, ordinal() + ordinal)];
    }
    public int convertEnergyTo(int energy, FactoryCapacityTiers tier){
        return (int) Math.round(Math.max(energy + (getConductivity() - tier.getConductivity()) * energy * initialCapacity /tier.initialCapacity,0));
    }


}
