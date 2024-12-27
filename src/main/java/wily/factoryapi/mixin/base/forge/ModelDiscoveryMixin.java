//? if forge && >=1.21.2 {
/*package wily.factoryapi.mixin.base.forge;

import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;

import java.util.*;

@Mixin(ModelDiscovery.class)
public class ModelDiscoveryMixin {
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> referencedModels;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> inputModels;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void discoverDependencies(CallbackInfo ci){
        FactoryAPIClient.extraModels.forEach(r->{
            UnbakedModel model = inputModels.get(r.id());
            if (model != null) referencedModels.put(r.id(),model);
        });
    }
}
*///?}
