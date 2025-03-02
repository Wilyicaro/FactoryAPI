package wily.factoryapi.mixin.base;

import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MenuType.class)
public interface MenuTypeAccessor {
    @Accessor("constructor")
    MenuType.MenuSupplier<?> getConstructor();
}
