package wily.factoryapi.mixin.common;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.20.1
/*import wily.factoryapi.base.client.FactorySpriteContents;*/

@Mixin(SpriteContents.class)
public class SpriteContentsMixin /*? if <=1.20.1 {*/ /*implements FactorySpriteContents *//*?}*/{
    //? if <=1.20.1 {
    /*ResourceMetadata metadata = ResourceMetadata.EMPTY;
    @Override
    public ResourceMetadata metadata() {
        return metadata;
    }

    @Override
    public void setMetadata(ResourceMetadata metadata) {
        this.metadata = metadata;
    }
    *///?}
}
