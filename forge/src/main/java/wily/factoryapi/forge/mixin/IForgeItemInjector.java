package wily.factoryapi.forge.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.IFactoryItem;

@Mixin(IFactoryItem.class)
public interface IForgeItemInjector extends IForgeItem{



    @Override
    default BlockEntityWithoutLevelRenderer getItemStackTileEntityRenderer() {
        Bearer<BlockEntityWithoutLevelRenderer> bewlr = Bearer.of(null);
        if (this instanceof IFactoryItem)
            ((IFactoryItem)this).clientExtension(c-> bewlr.set(c.getCustomRenderer()));
        return bewlr.get();
    }

    @Nullable
    @Override
    default  <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        if (this instanceof IFactoryItem){
            Bearer<HumanoidModel<?>> model = Bearer.of(null);
            ((IFactoryItem)this).clientExtension(c-> model.set(c.getHumanoidArmorModel(entityLiving,itemStack,armorSlot,_default)));
            if (model.isPresent()) return (A) model.get();
        }
        return null;
    }

    @Override
    default @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this instanceof IFactoryItem){
            Bearer<String> s = Bearer.of("");
            ((IFactoryItem)this).clientExtension(c->{if (c.getArmorTexture() != null) s.set(c.getArmorTexture().toString());});
            if (!s.get().isEmpty()) return s.get();
        }
        return IForgeItem.super.getArmorTexture(stack, entity, slot, type);
    }

}
