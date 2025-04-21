package wily.factoryapi.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.*;
//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
*///?}
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.init.FactoryRegistries;

import java.util.*;

public class FactoryItemUtil {
    //? if >=1.20.5 {
    /*public static final Codec<List<Item>> ITEM_COMPONENTS_CODEC = ResourceLocation.CODEC.xmap(r-> FactoryAPIPlatform.getRegistryValue(r,BuiltInRegistries.ITEM), BuiltInRegistries.ITEM::getKey).listOf();
    public static final StreamCodec<RegistryFriendlyByteBuf,List<Item>> ITEM_COMPONENTS_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(ITEM_COMPONENTS_CODEC);
    *///?}

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

    public static boolean compareItems(ItemStack itemStack, ItemStack itemStack1, boolean checkCount) {
        return compareItems(itemStack, itemStack1, checkCount, true);
    }

    public static boolean compareItems(ItemStack itemStack, ItemStack itemStack1, boolean checkCount, boolean strict){
        if (strict) {
            if (checkCount) return ItemStack.matches(itemStack, itemStack1);
            else return equalItems(itemStack, itemStack1);
        } else if (ItemStack.isSameItem(itemStack, itemStack1) && (!checkCount || itemStack.getCount() == itemStack1.getCount())){
            //? if <1.20.5 {
            return NbtUtils.compareNbt(itemStack.getTag(), itemStack1.getTag(), true);
            //?} else {
            /*for (TypedDataComponent<?> component : itemStack1.getComponents()) {
                if (!Objects.equals(itemStack.get(component.type()),component.value())) return false;
            }
            *///?}
        }
        return false;
    }

    public static boolean hasCustomName(ItemStack stack){
        return /*? if <1.20.5 {*/stack.hasCustomHoverName()/*?} else {*//*stack.has(DataComponents.CUSTOM_NAME)*//*?}*/;
    }

    public static void setCustomName(ItemStack stack, Component name){
        //? if <1.20.5 {
        if (name == null) stack.resetHoverName();
        else stack.setHoverName(name);
        //?} else {
        /*stack.set(DataComponents.CUSTOM_NAME,name);
        *///?}
    }

    public static int getEnchantmentLevel(ItemStack stack, /*? if >=1.21 {*/ /*ResourceKey<Enchantment> *//*?} else {*/Enchantment/*?}*/ enchantment){
        return getEnchantmentLevel(stack, enchantment, FactoryAPI.currentServer.registryAccess());
    }

    public static int getEnchantmentLevel(ItemStack stack, /*? if >=1.21 {*/ /*ResourceKey<Enchantment> *//*?} else {*/Enchantment/*?}*/ enchantment, RegistryAccess registryAccess){
        return /*? if <1.21 {*/EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack)/*?} else {*//*FactoryAPIPlatform.getRegistryValue(registryAccess, enchantment).map(e->stack.getEnchantments().getLevel(e)).orElse(0)*//*?}*/;
    }

}
