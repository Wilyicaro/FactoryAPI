package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Map;

public enum TransportState implements StringRepresentable, IHasIdentifier {
    EXTRACT("extract"),
    INSERT("insert"),
    EXTRACT_INSERT("extract_insert"),
    NONE("none");

    private final String name;

    private TransportState(String string2) {
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

public static TransportState ofBoolean(boolean canExtract, boolean canInsert){
        if (!canInsert && canExtract) return EXTRACT;
        if (!canExtract && canInsert) return INSERT;
        if (canExtract) return EXTRACT_INSERT;
        return  NONE;
}
    public TransportState nextStateSide(){
        return  TransportState.values()[ ordinal() < values().length - 1 ? ordinal() + 1 : 0];
    }

    public static CompoundTag serializeTag(Map<Direction, TransportState> sided) {

        CompoundTag sides = new CompoundTag();
        for (Direction direction : Direction.values())
            sides.putInt(direction.getName(), sided.get(direction).ordinal());
        return sides;
    }


    public static void deserializeNBT(CompoundTag nbt, Map<Direction, TransportState> sided) {
        for (Direction direction : Direction.values())
            sided.put(direction,  TransportState.values()[nbt.getInt(direction.getName())]);
    }

    @Override
    public SlotsIdentifier identifier() {
        return SlotsIdentifier.AQUA;
    }


}
