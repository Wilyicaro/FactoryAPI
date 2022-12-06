package wily.factoryapi.base;

import net.minecraft.nbt.Tag;

public interface ITagSerializable<T extends Tag>
{
    T serializeTag();
    void deserializeTag(T nbt);
}