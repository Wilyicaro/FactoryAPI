package wily.factoryapi.forge.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.IFactoryItem;

@Mixin(IForgeItem.class)
public interface IForgeItemInjector extends IForgeItem{
    default String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this instanceof IFactoryItem i && i.getArmorTexture() != null)
            return i.getArmorTexture().toString();
        return null;
    }

}
