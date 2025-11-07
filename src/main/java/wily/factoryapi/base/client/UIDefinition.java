package wily.factoryapi.base.client;

import wily.factoryapi.base.Bearer;

import java.util.*;
import java.util.function.*;

public interface UIDefinition extends Predicate<UIAccessor> {

    UIDefinition EMPTY = new UIDefinition() {};

    default void beforeInit(UIAccessor accessor) {
        getDefinitions().removeIf(d -> {
            if (d.test(accessor)) {
                d.beforeInit(accessor);
                return false;
            }
           return true;
        });
    }

    default void afterInit(UIAccessor accessor) {
        getDefinitions().forEach(d -> d.afterInit(accessor));
    }

    default void beforeTick(UIAccessor accessor) {
        getDefinitions().forEach(d -> d.beforeTick(accessor));
    }

    default void afterTick(UIAccessor accessor) {
        getDefinitions().forEach(d -> d.afterTick(accessor));
    }

    default List<UIDefinition> getDefinitions() {
        return Collections.emptyList();
    }

    default List<UIDefinition> getStaticDefinitions() {
        return Collections.emptyList();
    }
    
    default void addStatic(UIDefinition uiDefinition) {
        getStaticDefinitions().add(uiDefinition);
    }

    static UIDefinition createBeforeInit(Consumer<UIAccessor> beforeInit) {
        return new UIDefinition() {
            @Override
            public void beforeInit(UIAccessor accessor) {
                beforeInit.accept(accessor);
            }
        };
    }

    @Deprecated
    static UIDefinition createBeforeInit(String name, Consumer<UIAccessor> beforeInit) {
        return createBeforeInit(beforeInit);
    }

    static UIDefinition createAfterInit(Consumer<UIAccessor> afterInit) {
        return new UIDefinition() {
            @Override
            public void afterInit(UIAccessor accessor) {
                afterInit.accept(accessor);
            }
        };
    }

    @Deprecated
    static UIDefinition createAfterInit(String name, Consumer<UIAccessor> afterInit) {
        return createAfterInit(afterInit);
    }

    static UIDefinition createAfterInitWithAmount(String name, Consumer<UIAccessor> afterInit) {
        return createAfterInit(a -> {
            Bearer<Integer> bearer = Bearer.of(0);
            a.putBearer(name+".index", bearer);
            afterInit.accept(a);
        });
    }

    static UIDefinition createBeforeTick(Consumer<UIAccessor> beforeTick) {
        return new UIDefinition() {
            @Override
            public void beforeTick(UIAccessor accessor) {
                beforeTick.accept(accessor);
            }
        };
    }

    static UIDefinition createAfterTick(Consumer<UIAccessor> afterTick) {
        return new UIDefinition() {
            @Override
            public void afterTick(UIAccessor accessor) {
                afterTick.accept(accessor);
            }
        };
    }

    @Override
    default boolean test(UIAccessor accessor) {
        return true;
    }


    class Instance implements UIDefinition {
        private final Predicate<UIAccessor> applyCondition;
        protected List<UIDefinition> definitions = new ArrayList<>();
        protected List<UIDefinition> staticDefinitions = new ArrayList<>();

        public Instance(Predicate<UIAccessor> applyCondition) {
            this.applyCondition = applyCondition;
        }

        @Override
        public void beforeInit(UIAccessor accessor) {
            getDefinitions().clear();
            getDefinitions().addAll(getStaticDefinitions());
            UIDefinition.super.beforeInit(accessor);
        }

        @Override
        public boolean test(UIAccessor accessor) {
            return applyCondition.test(accessor);
        }

        @Override
        public List<UIDefinition> getStaticDefinitions() {
            return staticDefinitions;
        }

        @Override
        public List<UIDefinition> getDefinitions() {
            return definitions;
        }
    }
}