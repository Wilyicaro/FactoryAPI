package wily.factoryapi.mixin.common;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import wily.factoryapi.base.FactoryIngredient;
import wily.factoryapi.base.network.CommonNetwork;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements FactoryIngredient {
    //? if >=1.21.2 {
    /*@Shadow @Final protected HolderSet<Item> values;
    @Unique
    private ItemStack[] stacks;
    *///?} else
    @Shadow public abstract ItemStack[] getItems();

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return FactoryIngredient.DEFAULT_ID;
    }

    @Override
    public ItemStack[] getStacks() {
        //? if >=1.21.2 {
        /*return stacks == null ? stacks = values.stream().map(ItemStack::new).toArray(ItemStack[]::new) : stacks;
        *///?} else
        return getItems();
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        //? if >=1.20.5 {
        /*Ingredient.CONTENTS_STREAM_CODEC.encode(buf.get(),(Ingredient) (Object) this);
        *///?} else
        ((Ingredient)(Object) this).toNetwork(buf.get());
    }

    @Override
    public int getCount() {
        return 1;
    }
}
