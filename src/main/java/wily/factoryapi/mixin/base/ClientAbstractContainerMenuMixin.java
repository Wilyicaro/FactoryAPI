package wily.factoryapi.mixin.base;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.util.DynamicUtil;

@Mixin(AbstractContainerMenu.class)
public abstract class ClientAbstractContainerMenuMixin {

    @Shadow @Final public NonNullList<Slot> slots;

    @Shadow @Final private MenuType<?> menuType;

    @Inject(method = "addSlot", at = @At("HEAD"))
    private void init(Slot slot, CallbackInfoReturnable<Slot> cir){
        if (menuType == null) return;
        ResourceLocation menuName = BuiltInRegistries.MENU.getKey(menuType);
        if (menuName == null) return;
        DynamicUtil.COMMON_ITEMS.put(menuName.withSuffix(".slot." + slots.size()), slot::getItem);
    }
}
