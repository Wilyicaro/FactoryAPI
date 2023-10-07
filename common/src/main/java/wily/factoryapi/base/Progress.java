package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Progress implements ITagSerializable<CompoundTag> {

    public Identifier identifier;
    private final List<ProgressEntry> entries;

    public Progress(Identifier identifier, List<ProgressEntry> entries){
        this.identifier = identifier;
        this.entries = entries;
    }
    public Progress(Identifier identifier) {
        this(identifier, new ArrayList<>());
    }
    public Progress(Identifier identifier, int x, int y, int initialMaxProgress){
        this(identifier, List.of(new ProgressEntry(x,y,initialMaxProgress)));
    }
    public Progress(Identifier identifier, int entries, int defaultMaxProgress){
        this(identifier);
        for (int i = 0; i < entries; i++)
            add(0,0,defaultMaxProgress);
    }
    public Progress add(int x, int y, int maxProgress){
        entries.add(new ProgressEntry(x,y,maxProgress));
        return this;
    }
    public List<ProgressEntry> getEntries() {
        return entries;
    }
    public ProgressEntry first() {
        return get(0);
    }
    public ProgressEntry get(int index) {
        return entries.get(index);
    }

    public void forEach(Consumer<ProgressEntry> consumer){
        getEntries().forEach(consumer);
    }

    public void setValues(int[] array) {
        for (int i = 0; i < entries.size(); i++) {
            ProgressEntry p = entries.get(i);
            p.set(array[i*2]);
            p.maxProgress = array[i*2 + 1];
        }
    }
    public int[] getValues(){
        int[] values = new int[0];
        for (int i = 0; i < entries.size(); i++) {
            ProgressEntry p = entries.get(i);
            values = ArrayUtils.addAll(values,p.get(),p.maxProgress);
        }
        return values;
    }
    @Override
    public CompoundTag serializeTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putIntArray(identifier.name, getValues());
        return compoundTag;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        for (int i = 0; i < entries.size(); i++) {
            ProgressEntry p = entries.get(i);
            int[] array = tag.getIntArray(identifier.name);
            if (i*2 < array.length) {
                p.set(array[i * 2]);
                p.maxProgress = array[i * 2 + 1];
            }
        }
    }
    public static class ProgressEntry extends Bearer<Integer>{

        public int maxProgress;

        public int x;
        public int y;
        public int minValue = 0;

        public ProgressEntry(int x, int y, int maxProgress){
            super(0);
            this.maxProgress = maxProgress;
            this.x = x;
            this.y = y;
        }
        public void set(int value){
            super.set(Math.max(minValue,Math.min(value,maxProgress)));
        }

        public int add(int value){
            int oldValue = get();
            set(get() + value);
            return get() - oldValue;
        }
        public int shrink(int value){
            int oldValue = get();
            set(get() - value);
            return oldValue - get();
        }
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
