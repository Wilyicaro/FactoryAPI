package wily.factoryapi.base;

//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;
import wily.factoryapi.util.ListMap;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface FactoryIngredient extends Predicate<ItemStack>,CommonNetwork.Payload {
    ListMap<ResourceLocation, CommonNetwork.Identifier<? extends FactoryIngredient>> map = new ListMap<>();
    //? if >=1.20.5 {
    /*StreamCodec<RegistryFriendlyByteBuf,FactoryIngredient> CODEC = StreamCodec.of((b,i)-> encode(()->b,i), b-> decode(()->b));
    *///?}
    CommonNetwork.Identifier<FactoryIngredient> DEFAULT_ID = CommonNetwork.Identifier.create(FactoryAPI.createVanillaLocation("ingredient"),FactoryIngredient::decodeDefaultIngredient);

    static void init() {
        register(StackIngredient.ID);
    }

    ItemStack[] getStacks();

    static void register(CommonNetwork.Identifier<? extends FactoryIngredient> id){
        map.put(id.location(), id);
    }

    static FactoryIngredient of(Ingredient ing){
        return (FactoryIngredient) ing;
    }
    static FactoryIngredient of(ItemStack... stacks){
        return of(Ingredient.of(Arrays.stream(stacks).map(ItemStack::getItem).toArray(Item[]::new)));
    }

    default Ingredient toIngredient(){
        return (Ingredient) this;
    }

    default void apply(CommonNetwork.SecureExecutor executor, Supplier<Player> player) {
    }

    int getCount();

    static void encode(CommonNetwork.PlayBuf buf, FactoryIngredient ingredient){
        buf.get().writeResourceLocation(ingredient.identifier().location());
        ingredient.encode(buf);
    }
    static FactoryIngredient decode(CommonNetwork.PlayBuf buf){
        CommonNetwork.Identifier<? extends FactoryIngredient> id = map.getOrDefault(buf.get().readResourceLocation(),DEFAULT_ID);
        return id.decode(buf.get());
    }
    static FactoryIngredient decodeDefaultIngredient(CommonNetwork.PlayBuf buf){
        return FactoryIngredient.of(/*? >=1.20.5 {*//*Ingredient.CONTENTS_STREAM_CODEC.decode(buf.get())*//*?} else {*/ Ingredient.fromNetwork(buf.get())/*?}*/);
    }

}
