//? if forge && >=1.21.2 {
/*package wily.factoryapi.mixin.common.forge_1212;

import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIClient;

import java.util.Set;

@Mixin(ModelDiscovery.class)
public class ModelDiscoveryMixin {
    @Inject(method = "listMandatoryModels", at = @At("RETURN"))
    private static void listMandatoryModels(CallbackInfoReturnable<Set<ModelResourceLocation>> cir){
        if (!FactoryAPIClient.extraModels.isEmpty()) cir.getReturnValue().addAll(FactoryAPIClient.extraModels);
    }
}
*///?}
