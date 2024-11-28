package wily.factoryapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.mojang.serialization.*;
//? >1.20.5
/*import net.minecraft.core.component.DataComponentPatch;*/
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if >1.20.1 {
import net.minecraft.network.chat.ComponentSerialization;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.ArbitrarySupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicUtil {
    public static final ListMap<ResourceLocation, ArbitrarySupplier<ItemStack>> COMMON_ITEMS = new ListMap<>();
    public static final Map<Dynamic<?>,ItemStack> DYNAMIC_ITEMS = new ConcurrentHashMap<>();
    public static final LoadingCache<DynamicOps<?>,RegistryOps<?>> REGISTRY_OPS_CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(o->RegistryOps.create(o, Minecraft.getInstance().level.registryAccess())));

    public static ArbitrarySupplier<ItemStack> getItemFromDynamic(Dynamic<?> element, boolean /*? if <1.20.5 {*/allowNbt/*?} else {*/ /*allowComponents *//*?}*/){
        return element.get("common_item").asString().map(s->COMMON_ITEMS.get(ResourceLocation.tryParse(s))).result().orElse(()-> DYNAMIC_ITEMS.computeIfAbsent(element, d-> d.get("item").asString().result().or(()->d.asString().result()).map(s->BuiltInRegistries.ITEM./*? if <1.21.2 {*/get/*?} else {*//*getValue*//*?}*/(ResourceLocation.tryParse(s)).getDefaultInstance()).map(i-> {
            //? if <1.20.5 {
            if (allowNbt) element.get("nbt").result().flatMap(d1->CompoundTag.CODEC.parse(d1).result()).ifPresent(i::setTag);
            //?} else
            /*if (allowComponents) convertToRegistryIfPossible(element).get("components").result().flatMap(d1->DataComponentPatch.CODEC.parse(d1).result()).ifPresent(i::applyComponents);*/
            return i;
        }).orElse(null)));
    }

    public static <T> Dynamic<T> convertToRegistryIfPossible(Dynamic<T> dynamic){
        return !(dynamic.getOps() instanceof RegistryOps<?>) && Minecraft.getInstance().level != null ? dynamic.convert(getActualRegistryOps(dynamic.getOps())) : dynamic;
    }

    public static <T> DynamicOps<T> getActualRegistryOps(DynamicOps<T> ops){
        return Minecraft.getInstance().level == null ? ops : (DynamicOps<T>) REGISTRY_OPS_CACHE.getUnchecked(ops);
    }

    public static Codec<Component> getComponentCodec(){
        return /*? if <=1.20.1 {*/ /*ExtraCodecs.COMPONENT *//*?} else {*/ ComponentSerialization.CODEC/*?}*/;
    }

}
