package wily.factoryapi.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.init.FactoryRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactoryItemUtil {
    //? if >=1.20.5 {
    /*public static final Codec<List<Item>> ITEM_COMPONENTS_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.ITEM::get, BuiltInRegistries.ITEM::getKey).listOf();
    public static final StreamCodec<RegistryFriendlyByteBuf,List<Item>> ITEM_COMPONENTS_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(ITEM_COMPONENTS_CODEC);
    *///?}

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
        //? if <1.20.5 {
        List<Item> list = new ArrayList<>();
        if (itemStack.hasTag() && !itemStack.getTag().getList("Components",8).isEmpty())
            itemStack.getTag().getList("Components",8).forEach(t->list.add(BuiltInRegistries.ITEM.get(new ResourceLocation(t.getAsString()))));
        return list;
        //?} else {
        /*return itemStack.getOrDefault(FactoryRegistries.ITEM_COMPONENTS_COMPONENT.get(),new ArrayList<>());
        *///?}
    }
    public static boolean equalItems(ItemStack itemStack, ItemStack itemStack1){
        //? if <1.20.5 {
        return ItemStack.isSameItemSameTags(itemStack,itemStack1);
        //?} else
        /*return ItemStack.isSameItemSameComponents(itemStack,itemStack1);*/
    }

}
