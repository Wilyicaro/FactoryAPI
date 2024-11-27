package wily.factoryapi.mixin.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.util.DynamicUtil;

@Mixin(InventoryMenu.class)
public abstract class ClientInventoryMenuMixin extends RecipeBookMenu {
    public ClientInventoryMenuMixin(MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Inventory inventory, boolean bl, Player player, CallbackInfo ci){
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            DynamicUtil.COMMON_ITEMS.put(FactoryAPI.createVanillaLocation("inventory_menu.slot." + i), s::getItem);
        }
    }
}
