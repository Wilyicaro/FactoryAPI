package wily.factoryapi.base;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public interface IFactoryProcessableStorage extends IFactoryStorage {
    default boolean hasProgress(){
        return !getProgresses().isEmpty();
    }
    default List<Progress> getProgresses(){
        List<Progress> list = Lists.newArrayList();
        addProgresses(list);
        return list;
    }
    default Progress getProgressByType(Progress.Identifier identifier){
        for (Progress p : getProgresses()) {
            if (p.identifier == identifier) return p;
        }
        return null;
    }

    void addProgresses(List<Progress> list);

    @Override
    default void loadTag(CompoundTag compoundTag) {
        IFactoryStorage.super.loadTag(compoundTag);
        if(hasProgress()) {getProgresses().forEach(p -> p.deserializeTag(compoundTag));}
    }
    @Override
    default void saveTag(CompoundTag compoundTag) {
        IFactoryStorage.super.saveTag(compoundTag);
        if(hasProgress()) {getProgresses().forEach(p -> compoundTag.merge(p.serializeTag()));}
    }
}
