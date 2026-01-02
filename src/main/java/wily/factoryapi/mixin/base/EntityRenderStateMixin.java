//? if >=1.21.2 {
package wily.factoryapi.mixin.base;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.client.FactoryRenderStateExtension;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements FactoryRenderStateExtension.Accessor {
    Map<Class<FactoryRenderStateExtension<?>>,FactoryRenderStateExtension<?>> extensions = FactoryRenderStateExtension.types.stream().filter(type -> type.renderStateClass().isInstance(this)).map(type -> type.renderStateExtension().get()).collect(Collectors.toMap(f->(Class<FactoryRenderStateExtension<?>>)f.getClass(), Function.identity()));
    @Override
    public Iterable<FactoryRenderStateExtension<?>> getExtensions() {
        return extensions.values();
    }

    @Override
    public <T extends FactoryRenderStateExtension<?>> T getExtension(Class<T> extensionClass) {
        return (T) extensions.get(extensionClass);
    }
}
//?}
