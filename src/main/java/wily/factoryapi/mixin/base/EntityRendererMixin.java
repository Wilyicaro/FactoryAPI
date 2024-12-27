//? if >=1.21.2 {
/*package wily.factoryapi.mixin.base;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryRenderStateExtension;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void extractRenderState(Entity entity, EntityRenderState entityRenderState, float f, CallbackInfo ci) {
        FactoryRenderStateExtension.Accessor.of(entityRenderState).getExtensions().forEach(e-> e.tryExtractToRenderState(entity,f));
    }
}
*///?}
