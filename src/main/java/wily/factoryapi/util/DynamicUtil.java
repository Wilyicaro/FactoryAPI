package wily.factoryapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
//? >1.20.5
//import net.minecraft.core.component.DataComponentPatch;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if >1.20.1 {
import net.minecraft.network.chat.ComponentSerialization;
//?}
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ArbitrarySupplier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DynamicUtil {
    public static final ListMap<ResourceLocation, ArbitrarySupplier<ItemStack>> COMMON_ITEMS = new ListMap<>();
    public static final LoadingCache<Pair<Dynamic<?>,Boolean>,ItemStack> DYNAMIC_ITEMS_CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(pair-> pair.getFirst().get("item").asString().result().or(()->pair.getFirst().asString().result()).map(s->BuiltInRegistries.ITEM./*? if <1.21.2 {*/get/*?} else {*//*getValue*//*?}*/(ResourceLocation.tryParse(s)).getDefaultInstance()).map(i-> {
        pair.getFirst().get("count").result().flatMap(d1-> Codec.INT.parse(d1).result()).ifPresent(i::setCount);
        //? if <1.20.5 {
        if (pair.getSecond()) pair.getFirst().get("nbt").result().flatMap(d1-> CompoundTag.CODEC.parse(d1).result()).ifPresent(i::setTag);
        //?} else
        //if (pair.getSecond()) convertToRegistryIfPossible(pair.getFirst()).get("components").result().flatMap(d1->DataComponentPatch.CODEC.parse(d1).result()).ifPresent(i::applyComponents);
        return i;
    }).orElse(ItemStack.EMPTY)));
    public static final LoadingCache<DynamicOps<?>,RegistryOps<?>> REGISTRY_OPS_CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(o->RegistryOps.create(o, FactoryAPIPlatform.getRegistryAccess())));

    public static final Codec<Vec3> VEC3_OPTIONAL_CODEC = RecordCodecBuilder.create(i -> i.group(Codec.DOUBLE.fieldOf("x").orElse(0d).forGetter(Vec3::x),Codec.DOUBLE.fieldOf("y").orElse(0d).forGetter(Vec3::y),Codec.DOUBLE.fieldOf("z").orElse(0d).forGetter(Vec3::z)).apply(i, Vec3::new));
    public static final Codec<Vec3> VEC3_OBJECT_CODEC = Codec.either(VEC3_OPTIONAL_CODEC, Vec3.CODEC).xmap(e-> e.map(v->v,v->v), Either::right);
    public static final Codec<Vec2> VEC2_OPTIONAL_CODEC = RecordCodecBuilder.create(i -> i.group(Codec.FLOAT.fieldOf("x").orElse(0f).forGetter(vec2 -> vec2.x), Codec.FLOAT.fieldOf("y").orElse(0f).forGetter(vec2 -> vec2.y)).apply(i, Vec2::new));
    public static final Codec<Vec2> VEC2_CODEC = Codec.either(VEC2_OPTIONAL_CODEC, Codec.FLOAT.listOf().comapFlatMap((list) -> Util.fixedSize(list, 2).map((listx) -> new Vec2(listx.get(0), listx.get(1))), (vec3) -> List.of(vec3.x, vec3.y))).xmap(e -> e.map(v->v,v->v), Either::right);

    public static final Codec<ItemStack> COMPLETE_ITEM_CODEC = RecordCodecBuilder.create(i -> i.group(BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(ItemStack::getItemHolder), Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount), /*? if <1.20.5 {*/CompoundTag.CODEC.optionalFieldOf("nbt").forGetter((itemStack) -> Optional.ofNullable(itemStack.getTag()))/*?} else {*//*DataComponentPatch.CODEC.fieldOf("components").orElse(DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)*//*?}*/).apply(i, /*? if >1.20.1 {*/ItemStack::new/*?} else {*//*(item,count,nbt)->{ItemStack stack = new ItemStack(item,count); stack.setTag(nbt.orElse(null)); return stack;}*//*?}*/));
    public static final Codec<ItemStack> ITEM_CODEC = Codec.either(BuiltInRegistries.ITEM.holderByNameCodec().xmap(ItemStack::new,ItemStack::getItemHolder), COMPLETE_ITEM_CODEC).xmap(e->e.right().orElseGet(e.left()::get), Either::right);
    public static Codec<ArbitrarySupplier<ItemStack>> ITEM_WITHOUT_DATA_SUPPLIER_CODEC = Codec.of(ITEM_CODEC.xmap(ArbitrarySupplier::of,ArbitrarySupplier::get),DynamicUtil::getItemWithoutDataFromDynamic);
    public static Codec<ArbitrarySupplier<ItemStack>> ITEM_SUPPLIER_CODEC = Codec.of(ITEM_CODEC.xmap(ArbitrarySupplier::of,ArbitrarySupplier::get),DynamicUtil::getItemFromDynamic);

    public static <T> DataResult<Pair<ArbitrarySupplier<ItemStack>,T>> getItemWithoutDataFromDynamic(DynamicOps<T> ops, T input) {
        return getItemFromDynamic(ops, input, false);
    }
    public static <T> DataResult<Pair<ArbitrarySupplier<ItemStack>,T>> getItemFromDynamic(DynamicOps<T> ops, T input) {
        return getItemFromDynamic(ops, input, true);
    }
    public static <T> DataResult<Pair<ArbitrarySupplier<ItemStack>,T>> getItemFromDynamic(DynamicOps<T> ops, T input, boolean allowData) {
        return DataResult.success(Pair.of(getItemFromDynamic(new Dynamic<>(ops,input),allowData),input));
    }

    public static ArbitrarySupplier<ItemStack> getItemFromDynamic(Dynamic<?> element, boolean allowData) {
        return element.get("common_item").asString().map(s->COMMON_ITEMS.get(ResourceLocation.tryParse(s))).result().orElseGet(()-> {
            Pair<Dynamic<?>,Boolean> pair = Pair.of(element,allowData);
            DYNAMIC_ITEMS_CACHE.refresh(pair);
            return ()-> DYNAMIC_ITEMS_CACHE.getUnchecked(pair);
        });
    }

    public static <T> Dynamic<T> convertToRegistryIfPossible(Dynamic<T> dynamic) {
        return !(dynamic.getOps() instanceof RegistryOps<?>) && (!FactoryAPI.isClient() || FactoryAPIClient.hasLevel()) ? dynamic.convert(getActualRegistryOps(dynamic.getOps())) : dynamic;
    }

    public static <T> DynamicOps<T> getActualRegistryOps(DynamicOps<T> ops) {
        return FactoryAPI.isClient() && !FactoryAPIClient.hasLevel() ? ops : (DynamicOps<T>) REGISTRY_OPS_CACHE.getUnchecked(ops);
    }

    public static Codec<Component> getComponentCodec() {
        return /*? if <=1.20.1 {*/ /*ExtraCodecs.COMPONENT *//*?} else {*/ ComponentSerialization.CODEC/*?}*/;
    }

}
