package wily.factoryapi.base;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//? if >=1.20.5
//import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import wily.factoryapi.FactoryAPI;

public enum FactoryCapacityTier implements StringRepresentable {
    BURNED("burned", ChatFormatting.DARK_RED,0, 0 ,0),
    BASIC("basic", ChatFormatting.GRAY,0.2, 800 ,1),
    ADVANCED("advanced", ChatFormatting.RED,0.6, 2000,3),
    HIGH("high", ChatFormatting.BLUE,0.5, 4000,8),
    ULTIMATE("ultimate", ChatFormatting.DARK_PURPLE,0.63, 6000,12),
    QUANTUM("quantum", ChatFormatting.DARK_AQUA,0.8, 10000,16),
    CREATIVE("creative", ChatFormatting.LIGHT_PURPLE,1.0, Integer.MAX_VALUE, Integer.MAX_VALUE);

    public static final Codec<FactoryCapacityTier> CODEC = StringRepresentable.fromEnum(FactoryCapacityTier::values);
    //? if >=1.20.5
    //public static final StreamCodec<FriendlyByteBuf,FactoryCapacityTier> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, b->b.readEnum(FactoryCapacityTier.class));

    public final ChatFormatting formatting;
    private final double conductivity;
    public final int initialCapacity;

    public final int capacityMultiplier;

    private final String name;
    public final Component localizedName;

    FactoryCapacityTier(String name, ChatFormatting formatting, double j, int initialCapacity, int multiplier) {
        this(name, Component.translatable("tier."+FactoryAPI.MOD_ID + name),formatting, j, initialCapacity, multiplier);
    }
    FactoryCapacityTier(String name, Component displayName, ChatFormatting formatting, double j, int initialCapacity, int multiplier) {
        this.name = name;
        this.localizedName = displayName;
        this.formatting = formatting;
        this.conductivity = j;
        this.initialCapacity = initialCapacity;
        this.capacityMultiplier = multiplier;
    }

    public MutableComponent getEnergyTierComponent(boolean isStored){
        return getPrefixComponent("energy",isStored).withStyle(ChatFormatting.AQUA).append(localizedName);
    }
    public MutableComponent getOutputTierComponent(){
        return getPrefixComponent("energy",Component.translatable("tier.factory_api.output")).withStyle(ChatFormatting.AQUA).append(localizedName);
    }
    public MutableComponent getTierComponent(boolean isStored){
        return getPrefixComponent("capacity",isStored).withStyle(ChatFormatting.GRAY).append(localizedName);
    }
    public MutableComponent getPrefixComponent(String keyType, boolean isStored){
        if (isStored) return getPrefixComponent(keyType,Component.translatable("tier.factory_api.stored"));
        else return getPrefixComponent(keyType,"");
    }
    public MutableComponent getPrefixComponent(String keyType, Object... objects){
        return Component.translatable("tier.factory_api.display",Component.translatable("tier.factory_api." + keyType, objects));
    }
    public boolean supportTier(FactoryCapacityTier tier){return ordinal() >= tier.ordinal();}
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

    public FactoryCapacityTier increase(int ordinal){
        return FactoryCapacityTier.values()[Math.min(values().length-1, ordinal() + ordinal)];
    }
    public int convertEnergyTo(int energy, FactoryCapacityTier tier){
        return (int) Math.round(Math.max(energy + (getConductivity() - tier.getConductivity()) * energy * initialCapacity /tier.initialCapacity,0));
    }


    @Override
    public String getSerializedName() {
        return name;
    }
}
