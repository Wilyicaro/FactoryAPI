/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package wily.factoryapi.base;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
//? if >=1.20.5 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component./*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/;
import net.minecraft.core.component.DataComponentType;
//?}
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.factoryapi.util.FactoryItemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Ingredient that matches the given items, performing either a {@link StackIngredient#isStrict() strict} or a partial NBT test.
 * <p>
 * Strict NBT ingredients will only match items that have <b>exactly</b> the provided tag, while partial ones will
 * match if the item's tags contain all the elements of the provided one, while allowing for additional elements to exist.
 */
public class StackIngredient extends Ingredient implements FactoryIngredient {
    public static final MapCodec<StackIngredient> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(),false).fieldOf("items").forGetter(StackIngredient::values),
                            /*? if >=1.20.5 {*//*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*//*?} else {*//*CompoundTag*//*?}*/.CODEC.fieldOf(/*? if >=1.20.5 {*/"components"/*?} else {*//*"nbt"*//*?}*/).forGetter(StackIngredient::/*? if >=1.20.5 {*/components/*?} else {*//*getTag*//*?}*/),
                            Codec.BOOL.optionalFieldOf("strict", false).forGetter(StackIngredient::isStrict),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(StackIngredient::getCount))
                    .apply(builder, StackIngredient::new));
    //? if >=1.20.5 {
    public static final StreamCodec<RegistryFriendlyByteBuf, StackIngredient> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    //?}
    public static final CommonNetwork.Identifier<StackIngredient> ID = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("stack_ingredient"),StackIngredient::decode);
    private final HolderSet<Item> values;
    //? if >=1.20.5 {
    private final /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/ components;
    //?} else {
    /*private final CompoundTag tag;
    *///?}
    private final boolean strict;
    protected final ItemStack[] stacks;
    private final int count;

    public StackIngredient(HolderSet<Item> values, /*? if >=1.20.5 {*/ /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/ components/*?} else {*//*CompoundTag tag*//*?}*/, boolean strict, int count) {
        super(/*? if <1.21.2 {*//*Stream.empty() *//*?} else {*/values/*?}*/);
        this.values = values;
        //? if >=1.20.5 {
        this.components = components;
        //?} else {
        /*this.tag = tag;
        *///?}
        this.strict = strict;
        this.stacks = values.stream().map(i ->{
            ItemStack stack = new ItemStack(i, count);
            //? if >=1.20.5 {
            stack.applyComponents(components.asPatch());
            //?} else
            /*stack.setTag(tag);*/
            return stack;
        }).filter(i -> !i.isEmpty()).toArray(ItemStack[]::new);
        this.count = count;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (strict) {
            for (ItemStack stack2 : this.stacks) {
                if (FactoryItemUtil.equalItems(stack, stack2)) return true;
            }
            return false;
        } else {
            return this.values.contains(stack.getItemHolder()) && /*? if >=1.20.5 {*/ this.components.test(stack) /*?} else {*/ /*NbtUtils.compareNbt(tag, stack.getTag(), true) *//*?}*/;
        }
    }

    //? if <1.21.2 {

    /*@Override
    public boolean isEmpty() {
        return stacks.length == 0;
    }
    @Override
    public ItemStack[] getItems() {
        return stacks;
    }
    *///?}

    public HolderSet<Item> values() {
        return values;
    }

    public ItemStack[] getStacks() {
        return stacks;
    }

    //? if >=1.20.5 {
    public /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/ components() {
        return components;
    }
    //?} else {
    /*public CompoundTag getTag(){
        return tag;
    }
    *///?}

    public boolean isStrict() {
        return strict;
    }
    /**
     * Creates a new ingredient matching the given item stack and the item stack count
     */
    public static StackIngredient of(boolean strict, ItemStack stack) {
        return of(strict,stack,stack.getCount());
    }

    /**
     * Creates a new ingredient matching the given item stack
     */
    public static StackIngredient of(boolean strict, ItemStack stack, int count) {
        //? if >=1.20.5 {
        return of(strict, stack.getComponents(), count, stack.getItem());
         //?} else {
        /*return new StackIngredient(HolderSet.direct(stack.getItemHolder()), stack.getTag(), strict, count);
        *///?}
    }
    //? if >=1.20.5 {
    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    public static <T> StackIngredient of(boolean strict, DataComponentType<? super T> type, T value, int count, ItemLike... items) {
        return of(strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/.builder().expect(type, value).build(), count, items);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    public static <T> StackIngredient of(boolean strict, Supplier<? extends DataComponentType<? super T>> type, T value, int count, ItemLike... items) {
        return of(strict, type.get(), value, count, items);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    public static StackIngredient of(boolean strict, DataComponentMap map, int count, ItemLike... items) {
        return of(strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/.allOf(map), count,items);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    @SafeVarargs
    public static StackIngredient of(boolean strict, DataComponentMap map, int count, Holder<Item>... items) {
        return of(strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/.allOf(map), count,items);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    public static StackIngredient of(boolean strict, DataComponentMap map, HolderSet<Item> items, int count) {
        return of(strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/.allOf(map), items, count);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    @SafeVarargs
    public static StackIngredient of(boolean strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/ predicate, int count, Holder<Item>... items) {
        return of(strict, predicate, HolderSet.direct(items), count);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    public static StackIngredient of(boolean strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/ predicate, int count, ItemLike... items) {
        return of(strict, predicate, HolderSet.direct(Arrays.stream(items).map(ItemLike::asItem).map(Item::builtInRegistryHolder).toList()), count);
    }

    /**
     * Creates a new ingredient matching any item from the list, containing the given components
     */
    public static StackIngredient of(boolean strict, /*? if >1.21.4 {*/DataComponentExactPredicate/*?} else {*//*DataComponentPredicate*//*?}*/ predicate, HolderSet<Item> items, int count) {
        return new StackIngredient(items, predicate, strict,count);
    }
    //?}

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        //? if <1.20.5 {
        /*buf.get().writeCollection(values.stream().toList(),(b,i)->b.writeId(BuiltInRegistries.ITEM,i.value()));
        buf.get().writeNbt(tag);
        buf.get().writeBoolean(isStrict());
        buf.get().writeByte(getCount());
        *///?} else
        STREAM_CODEC.encode(buf.get(),this);
    }
    public static StackIngredient decode(CommonNetwork.PlayBuf buf){
        //? if <1.20.5 {
        /*return new StackIngredient(HolderSet.direct((List<Holder<Item>>) buf.get().readCollection(ArrayList::new,(b)->b.readById(BuiltInRegistries.ITEM.asHolderIdMap()))), buf.get().readNbt(), buf.get().readBoolean(), buf.get().readByte());
        *///?} else
        return STREAM_CODEC.decode(buf.get());
    }
}
