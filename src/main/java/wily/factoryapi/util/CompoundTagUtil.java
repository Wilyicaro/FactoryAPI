package wily.factoryapi.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundTagUtil {

    public static boolean compoundContains(CompoundTag comparator, CompoundTag contains){
        Map<String, Tag> map = new HashMap<>(comparator.tags);

        for (String key : contains.getAllKeys()) {
            if (map.containsKey(key)){
                if (map.get(key).getAsString().equals(contains.get(key).getAsString()) || (map.get(key) instanceof ListTag l1 && contains.get(key) instanceof ListTag l2 && l2.containsAll(l1))||(map.get(key) instanceof CompoundTag ct1 && contains.get(key) instanceof CompoundTag ct2 && compoundContains(ct1,ct2)) )map.remove(key);
            }
        }

        return map.isEmpty();
    }

    public static CompoundTag getFromJson(JsonObject obj){
        return CompoundTag.CODEC.parse(JsonOps.INSTANCE,obj).result().orElseGet(CompoundTag::new);
    }

    public static List<Item> getItemComponents(ItemStack itemStack){
        List<Item> list = new ArrayList<>();
        if (itemStack.hasTag() && !itemStack.getTag().getList("Components",8).isEmpty())
            itemStack.getTag().getList("Components",8).forEach(t->list.add(BuiltInRegistries.ITEM.get(new ResourceLocation(t.getAsString()))));
        return list;
    }

}
