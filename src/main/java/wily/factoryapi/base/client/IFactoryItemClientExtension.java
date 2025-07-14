package wily.factoryapi.base.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
//? if >1.21.2 {
/*import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//? if <1.21.4 {
import net.minecraft.world.item.equipment.EquipmentModel;
//?} else {
/^import net.minecraft.client.resources.model.EquipmentClientInfo;
^///?}
*///?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import wily.factoryapi.util.ListMap;

import java.util.Map;

public interface IFactoryItemClientExtension {
    // Map used only to store the IFactoryItem instances with client extensions, not meant to be modified externally, as can result in unexpected behaviour between mod loaders
    Map<Item, IFactoryItemClientExtension> map = new ListMap<>();
    /**
     * Its use is only for BlockItem instances
     * By default, returns vanilla's item renderer.
     */
    default IFactoryBlockEntityWLRenderer getCustomRenderer() {
        return null;
    }
    default <T extends Model> T getHumanoidArmorModel(/*? if >=1.21.2 && !neoforge {*//*LivingEntityRenderState livingEntityRenderState,*//*?} else if <1.21.2 {*/LivingEntity livingEntity,/*?}*/ ItemStack itemStack, EquipmentSlot equipmentSlot, T original){
        return original;
    }
    //? if >=1.21.2 && neoforge {
    /*default Model getHumanoidArmorModel(ItemStack stack, /^? if <1.21.4 {^/ EquipmentModel.LayerType/^?} else {^//^EquipmentClientInfo.LayerType^//^?}^/ layerType, Model original){
        return getHumanoidArmorModel(stack,layerType == /^? if <1.21.4 {^/ EquipmentModel.LayerType/^?} else {^//^EquipmentClientInfo.LayerType^//^?}^/.HUMANOID_LEGGINGS ? EquipmentSlot.LEGS : EquipmentSlot.CHEST, original);
    }
    *///?}

}
