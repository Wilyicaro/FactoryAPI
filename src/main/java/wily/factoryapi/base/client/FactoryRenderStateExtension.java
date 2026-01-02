//? if >=1.21.2 {
package wily.factoryapi.base.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface FactoryRenderStateExtension<E extends Entity> {
    List<Type<?,?>> types = new ArrayList<>();

    record Type<E extends Entity, S extends EntityRenderState>(Class<S> renderStateClass, Supplier<FactoryRenderStateExtension<E>> renderStateExtension){
    }

    Class<E> getEntityClass();

    default void tryExtractToRenderState(Entity entity, float partialTicks){
        if (getEntityClass().isInstance(entity)) extractToRenderState(getEntityClass().cast(entity),partialTicks);
    }

    void extractToRenderState(E entity, float partialTicks);

    interface Accessor {
        static Accessor of(EntityRenderState entityRenderState){
            return (Accessor) entityRenderState;
        }
        Iterable<FactoryRenderStateExtension<?>> getExtensions();

        <T extends FactoryRenderStateExtension<?>> T getExtension(Class<T> extensionClass);
    }
}
//?}
