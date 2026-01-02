package wily.factoryapi.base;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface IFactoryProgressiveStorage extends IFactoryExpandedStorage {
    default boolean hasProgress(){
        return !getProgresses().isEmpty();
    }
    default List<Progress> getProgresses(){
        return Collections.emptyList();
    }
    default @Nullable Progress getProgressByType(Progress.ResourceLocation identifier){
        for (Progress p : getProgresses()) {
            if (p.identifier == identifier) return p;
        }
        return null;
    }


    @Override
    default void loadTag(CompoundTag compoundTag) {
        IFactoryExpandedStorage.super.loadTag(compoundTag);
        if(hasProgress()) {getProgresses().forEach(p -> p.deserializeTag(compoundTag));}
    }
    @Override
    default void saveTag(CompoundTag compoundTag) {
        IFactoryExpandedStorage.super.saveTag(compoundTag);
        if(hasProgress()) {getProgresses().forEach(p -> compoundTag.merge(p.serializeTag()));}
    }
}
