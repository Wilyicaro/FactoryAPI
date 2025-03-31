//? if >=1.21.5 {
/*package wily.factoryapi.mixin.base;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {
    @Invoker("register")
    static RenderPipeline register(RenderPipeline renderPipeline) {
        return renderPipeline;
    }
}
*///?}
