package wily.factoryapi.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Optional;

public class CompoundTagUtil {

    public static CompoundTag getCompoundTagOrEmpty(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getCompoundOrEmpty(name)*//*?} else {*/tag.getCompound(name)/*?}*/;
    }

    public static Optional<CompoundTag> getCompoundTag(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getCompound(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getCompound(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<Boolean> getBoolean(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getBoolean(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getBoolean(name)): Optional.empty()/*?}*/;
    }

    public static Optional<Byte> getByte(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getByte(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getByte(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<Integer> getInt(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getInt(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getInt(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<Long> getLong(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getLong(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getLong(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<Float> getFloat(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getFloat(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getFloat(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<Double> getDouble(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getDouble(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getDouble(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<byte[]> getByteArray(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getByteArray(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getByteArray(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<int[]> getIntArray(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getIntArray(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getIntArray(name)) : Optional.empty()/*?}*/;
    }

    public static Optional<long[]> getLongArray(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getLongArray(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getLongArray(name)) : Optional.empty()/*?}*/;
    }

    public static byte[] getByteArrayOrEmpty(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getByteArray(name).orElseGet(()->new byte[0])*//*?} else {*/tag.getByteArray(name)/*?}*/;
    }

    public static int[] getIntArrayOrEmpty(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getIntArray(name).orElseGet(()->new int[0])*//*?} else {*/tag.getIntArray(name)/*?}*/;
    }

    public static long[] getLongArrayOrEmpty(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getLongArray(name).orElseGet(()->new long[0])*//*?} else {*/tag.getLongArray(name)/*?}*/;
    }

    public static Optional<String> getString(CompoundTag tag, String name){
        return /*? if >1.21.4 {*//*tag.getString(name)*//*?} else {*/tag.contains(name) ? Optional.of(tag.getString(name)) : Optional.empty()/*?}*/;
    }
}
