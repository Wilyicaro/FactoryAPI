//? if >=1.21.2 {
/*package wily.factoryapi.mixin.base;

import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.FuelValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FuelValues.class)
public interface FuelValuesAccessor {
    @Accessor
    Object2IntSortedMap<Item> getValues();
}
*///?}
