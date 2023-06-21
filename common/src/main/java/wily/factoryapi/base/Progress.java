package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;

public class Progress implements ITagSerializable<CompoundTag> {
    private int[] progress;

    public int maxProgress;
    public Identifier identifier;

    public Progress(Identifier identifier, int progressSize, int maxProgress){
        this.identifier = identifier;
        this.maxProgress = maxProgress;
        progress = new int[progressSize];
    }
    public void setInt(int ordinal, int value){
        progress[ordinal] = value;
    }
    public int getInt(int ordinal){
        return progress[ordinal];
    }

    public void set(int[] value){
        progress = value;
    }
    public int[] get(){
        return progress;
    }

    public String getMaxName(){
        return "actual" + (identifier.name.substring(0,1).toUpperCase() + identifier.name.substring(1));
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag compoundTag = new CompoundTag();
        if(progress.length == 1){
            compoundTag.putInt(identifier.name, getInt(0));
        }else if (progress.length > 1) compoundTag.putIntArray(identifier.name, get());
        compoundTag.putInt(getMaxName(), maxProgress);
        return compoundTag;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {

        if(progress.length == 1){
            setInt(0, tag.getInt(identifier.name));
        }else if (progress.length > 1) set(tag.getIntArray(identifier.name));
        maxProgress = tag.getInt(getMaxName());
    }
    public record Identifier(String name) {
        public static Identifier DEFAULT = new Identifier("progress");
        public static Identifier ENERGY_STORAGE = new Identifier("energyStorage");
        public static Identifier TANK = new Identifier("tank");
        public static Identifier BURN_TIME = new Identifier("burnTime");
        public static Identifier GENERATING = new Identifier("gen");
        public static Identifier MATTER = new Identifier("matter");

    }
}
