package wily.factoryapi.mixin.base;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
//? if >=1.21.2 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wily.factoryapi.base.FactoryIngredient;
import wily.factoryapi.base.network.CommonNetwork;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements FactoryIngredient {
    //? if >=1.21.2 {

    @Mutable
    @Shadow @Final public static StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_CONTENTS_STREAM_CODEC;
    @Unique
    private ItemStack[] stacks;
    //?} else
    /*@Shadow public abstract ItemStack[] getItems();*/

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return FactoryIngredient.DEFAULT_ID;
    }

    @Unique
    private Ingredient self(){
        return (Ingredient) (Object) this;
    }

    @Override
    public ItemStack[] getStacks() {
        //? if >=1.21.2 {
        return stacks == null ? stacks = self().items()/*? if <1.21.4 {*//*.stream()*//*?}*/.map(ItemStack::new).toArray(ItemStack[]::new) : stacks;
        //?} else
        /*return getItems();*/
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        //? if >=1.20.5 {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf.get(),(Ingredient) (Object) this);
        //?} else
        /*((Ingredient)(Object) this).toNetwork(buf.get());*/
    }

    @Override
    public int getCount() {
        return 1;
    }

    //? if >=1.21.2 && forge {
    /*@Redirect(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/crafting/Ingredient;OPTIONAL_CONTENTS_STREAM_CODEC:Lnet/minecraft/network/codec/StreamCodec;"))
    private static void fixOptionalIngredientStreamCodec(StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> value){
        OPTIONAL_CONTENTS_STREAM_CODEC = StreamCodec.of((b, o)-> b.writeOptional(o,(b1,i)->Ingredient.CONTENTS_STREAM_CODEC.encode(b,i)), b-> b.readOptional(b1->Ingredient.CONTENTS_STREAM_CODEC.decode(b)));
    }
    *///?}
}
