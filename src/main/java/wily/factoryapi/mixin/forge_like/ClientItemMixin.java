//? if forge || neoforge
/*package wily.factoryapi.mixin.forge_like;*/
//? if forge {
/*import net.minecraftforge.client.extensions.common.IClientItemExtensions;
*///?} else if neoforge
/*import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;*/
//? if forge || neoforge {
/*import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.IFactoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;

@Mixin(Item.class)
public class ClientItemMixin {
    @Inject(method = ("initializeClient"), at = @At("HEAD"),remap = false)
    private void initializeClient(Consumer<IClientItemExtensions> consumer, CallbackInfo info){
        if (this instanceof IFactoryItem i){
            i.clientExtension(c-> consumer.accept(new IClientItemExtensions() {
                @Override
                public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                    return c.getHumanoidArmorModel(livingEntity,itemStack,equipmentSlot,original);
                }
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return c.getCustomRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
            }));
        }
    }
}
*///?}