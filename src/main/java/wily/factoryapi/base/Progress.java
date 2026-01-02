package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.ArrayUtils;
import wily.factoryapi.util.CompoundTagUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Progress implements ITagSerializable<CompoundTag> {

    public ResourceLocation identifier;
    private final List<ProgressEntry> entries;

    public Progress(ResourceLocation identifier, List<ProgressEntry> entries){
        this.identifier = identifier;
        this.entries = entries;
    }
    public Progress(ResourceLocation identifier) {
        this(identifier, new ArrayList<>());
    }
    public Progress(ResourceLocation identifier, int x, int y, int initialMaxProgress){
        this(identifier, List.of(new ProgressEntry(x,y,initialMaxProgress)));
    }
    public Progress(ResourceLocation identifier, int entries, int defaultMaxProgress){
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
            int[] array = CompoundTagUtil.getIntArrayOrEmpty(tag, identifier.name);
            if (i * 2 < array.length) {
                p.set(array[i * 2]);
                p.maxProgress = array[i * 2 + 1];
            }
        }
    }
    public static class ProgressEntry extends Stocker<Integer>{

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
    public record ResourceLocation(String name) {
        public static ResourceLocation DEFAULT = new ResourceLocation("progress");
        public static ResourceLocation ENERGY_STORAGE = new ResourceLocation("energyStorage");
        public static ResourceLocation TANK = new ResourceLocation("tank");
        public static ResourceLocation BURN_TIME = new ResourceLocation("burnTime");
        public static ResourceLocation GENERATING = new ResourceLocation("gen");
        public static ResourceLocation MATTER = new ResourceLocation("matter");

    }
}
