package wily.factoryapi.mixin.base;

//? if >=1.21.2
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if forge {
/*import net.minecraftforge.client.extensions.common.IClientItemExtensions;
*///?} else if neoforge {
/*import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
*///?}
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

@Mixin(Item.class)
public class ClientItemMixin {

    //? if forge || neoforge && <1.20.5
    /*@Shadow(remap = false) private Object renderProperties;*/

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Item.Properties arg, CallbackInfo ci){
        if (this instanceof IFactoryItem i){
            i.clientExtension(c-> {
                IFactoryItemClientExtension.map.put((Item)(Object)this,c);
                //? if forge || neoforge && <1.20.5 {
                /*renderProperties = new IClientItemExtensions() {
                    @Override
                    public @NotNull HumanoidModel<?> getHumanoidArmorModel(/^? if >1.21.2 {^/LivingEntityRenderState livingEntityRenderState /^?} else {^//^LivingEntity livingEntity^//^?}^/, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                        return c.getHumanoidArmorModel(/^? if >1.21.2 {^/livingEntityRenderState /^?} else {^//^livingEntity^//^?}^/,itemStack,equipmentSlot,original);
                    }
                };
                *///?}
            });
        }
    }
}