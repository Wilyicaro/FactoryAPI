//? if <1.21.2 {
/*package wily.factoryapi.mixin.base;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Invoker("byType")
    <R extends Recipe<?>> /^? if <1.20.5 {^/ /^Map<Identifier, /^¹? if >1.20.1 {¹^/RecipeHolder<R>/^¹?} else {¹^//^¹R¹^//^¹?}¹^/>^//^?} else {^/Collection<RecipeHolder<R>>/^?}^/ getRecipeByType(RecipeType<R> recipeType);
}
*///?}