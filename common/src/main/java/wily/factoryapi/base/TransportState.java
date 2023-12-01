package wily.factoryapi.base;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum TransportState implements StringRepresentable {
    EXTRACT("extract"),
    INSERT("insert"),
    EXTRACT_INSERT("extract_insert"),
    NONE("none");

    private final String name;

    TransportState(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public static TransportState byOrdinal(int ordinal) {
        return values()[ordinal];
    }

    public String getSerializedName() {
        return this.name;
    }
    public Component getTooltip(){
        return Component.translatable("tooltip.factory_api.config.transport." + name);
    }

    public boolean isUsable() {
        return this != NONE;
    }

    public boolean canInsert() {
        return this == INSERT || this == EXTRACT_INSERT;
    }
    public boolean canExtract() {
        return this == EXTRACT || this == EXTRACT_INSERT;
    }

    public TransportState next(){
        return values()[ordinal() >= values().length - 1 ? 0 :  ordinal() + 1];
    }

    public static TransportState ofBoolean(boolean canExtract, boolean canInsert){
        if (!canInsert && canExtract) return EXTRACT;
        if (!canExtract && canInsert) return INSERT;
        if (canExtract) return EXTRACT_INSERT;
        return  NONE;
    }


}
