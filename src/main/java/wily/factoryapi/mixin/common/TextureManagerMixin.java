package wily.factoryapi.mixin.common;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <=1.20.1 {
/*import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.GuiSpriteManager;
*///?}

@Mixin(TextureManager.class)
public class TextureManagerMixin {
    //? if <=1.20.1 {
    /*@Inject(method = "<init>", at = @At("RETURN"))
    public void init(ResourceManager arg, CallbackInfo ci){
        ((ReloadableResourceManager)arg).registerReloadListener(FactoryAPIClient.sprites = new GuiSpriteManager((TextureManager) (Object) this));
    }
    *///?}
}
