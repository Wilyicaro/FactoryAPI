package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import wily.factoryapi.FactoryAPI;

public enum FactoryCapacityTiers {

    BURNED(Component.translatable("tier."+ FactoryAPI.MOD_ID + ".burned").withStyle(ChatFormatting.DARK_RED),0, 0 ,0),
    BASIC(Component.translatable("tier."+ FactoryAPI.MOD_ID + ".basic").withStyle(ChatFormatting.GRAY),0.2, 800 ,1),
    ADVANCED(Component.translatable("tier."+FactoryAPI.MOD_ID + ".advanced").withStyle(ChatFormatting.RED),0.6, 2000,3),
    HIGH(Component.translatable("tier."+ FactoryAPI.MOD_ID + ".high").withStyle(ChatFormatting.BLUE),0.5, 4000,8),
    ULTIMATE(Component.translatable("tier."+FactoryAPI.MOD_ID + ".ultimate").withStyle(ChatFormatting.DARK_PURPLE),0.63, 6000,12),
    QUANTUM(Component.translatable("tier."+FactoryAPI.MOD_ID + ".quantum").withStyle(ChatFormatting.DARK_AQUA),0.8, 10000,16),
    CREATIVE(Component.translatable("tier."+FactoryAPI.MOD_ID + ".creative").withStyle(ChatFormatting.LIGHT_PURPLE),1.0, Integer.MAX_VALUE, Integer.MAX_VALUE);

    
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
        return getComponent("energy",isStored);
    }
    public MutableComponent getTierComponent(boolean isStored){
        return getComponent("capacity",isStored);
    }
    public MutableComponent getComponent(String keyType, boolean isStored){
        String stored = isStored ? I18n.get("tier.factory_api.stored") : "";
        return Component.translatable("tier.factory_api.display",I18n.get("tier.factory_api." + keyType,stored)).withStyle(ChatFormatting.AQUA).append(localizedName);
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


    public int convertEnergyTo(int energy, FactoryCapacityTiers tier){
        return (int) Math.round(Math.max(energy + (getConductivity() - tier.getConductivity()) * energy * initialCapacity /tier.initialCapacity,0));
    }


}
