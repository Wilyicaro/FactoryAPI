package wily.factoryapi.base.client;

import wily.factoryapi.base.Bearer;

import java.util.*;
import java.util.function.*;

public interface UIDefinition extends Predicate<UIAccessor> {

    UIDefinition EMPTY = new UIDefinition(){};

    default void beforeInit(UIAccessor accessor){
        getDefinitions().forEach(d->d.beforeInit(accessor));
    }

    default void afterInit(UIAccessor accessor){
        getDefinitions().forEach(d->d.afterInit(accessor));
    }

    default void beforeTick(UIAccessor accessor){
        getDefinitions().forEach(d->d.beforeTick(accessor));
    }

    default void afterTick(UIAccessor accessor){
        getDefinitions().forEach(d->d.afterTick(accessor));
    }

    default List<UIDefinition> getDefinitions(){
        return Collections.emptyList();
    }



    static UIDefinition createBeforeInit(Consumer<UIAccessor> beforeInit){
        return new UIDefinition() {
            @Override
            public void beforeInit(UIAccessor accessor) {
                UIDefinition.super.beforeInit(accessor);
                beforeInit.accept(accessor);
            }
        };
    }
    static UIDefinition createBeforeInit(String name, Consumer<UIAccessor> beforeInit){
        return createBeforeInit(a->{
            if (a.getBoolean(name+".applyCondition",true)) beforeInit.accept(a);
        });
    }
    static UIDefinition createAfterInit(Consumer<UIAccessor> afterInit){
        return new UIDefinition() {
            @Override
            public void afterInit(UIAccessor accessor) {
                UIDefinition.super.afterInit(accessor);
                afterInit.accept(accessor);
            }
        };
    }
    static UIDefinition createAfterInit(String name, Consumer<UIAccessor> afterInit){
        return createAfterInit(a->{
            if (a.getBoolean(name+".applyCondition",true)) afterInit.accept(a);
        });
    }

    static UIDefinition createAfterInitWithAmount(String name, Consumer<UIAccessor> afterInit){
        return createAfterInit(a->{
            Bearer<Integer> bearer = Bearer.of(0);
            a.putBearer(name+".index", bearer);
            afterInit.accept(a);
        });
    }

    static UIDefinition createBeforeTick(Consumer<UIAccessor> beforeTick){
        return new UIDefinition() {
            @Override
            public void beforeInit(UIAccessor accessor) {
                UIDefinition.super.beforeTick(accessor);
                beforeTick.accept(accessor);
            }
        };
    }

    static UIDefinition createAfterTick(Consumer<UIAccessor> afterTick){
        return new UIDefinition() {
            @Override
            public void beforeInit(UIAccessor accessor) {
                UIDefinition.super.afterTick(accessor);
                afterTick.accept(accessor);
            }
        };
    }

    @Override
    default boolean test(UIAccessor accessor){
        return true;
    }

}