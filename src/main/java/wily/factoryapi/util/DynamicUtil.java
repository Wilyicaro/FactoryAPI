package wily.factoryapi.util;

import com.mojang.serialization.Dynamic;
//? >1.20.5
/*import net.minecraft.core.component.DataComponentPatch;*/
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.ArbitrarySupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicUtil {
    public static final ListMap<ResourceLocation, ArbitrarySupplier<ItemStack>> COMMON_ITEMS = new ListMap<>();
    public static final Map<Dynamic<?>,ItemStack> DYNAMIC_ITEMS = new ConcurrentHashMap<>();

    public static ArbitrarySupplier<ItemStack> getItemFromDynamic(Dynamic<?> element, boolean /*? if <1.20.5 {*/allowNbt/*?} else {*/ /*allowComponents *//*?}*/){
        return element.get("common_item").asString().map(s->COMMON_ITEMS.get(ResourceLocation.tryParse(s))).result().orElse(()-> DYNAMIC_ITEMS.computeIfAbsent(element, d-> d.get("item").asString().result().or(()->d.asString().result()).map(s->BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(s)).getDefaultInstance()).map(i-> {
            //? if <1.20.5 {
            if (allowNbt) element.get("nbt").result().flatMap(d1->CompoundTag.CODEC.parse(d).result()).ifPresent(i::setTag);
            //?} else
            /*if (allowComponents) element.get("components").result().flatMap(d1->DataComponentPatch.CODEC.parse(d).result()).ifPresent(i::applyComponents);*/
            return i;
        }).orElse(null)));
    }
}
