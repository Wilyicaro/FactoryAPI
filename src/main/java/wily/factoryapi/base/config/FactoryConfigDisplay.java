package wily.factoryapi.base.config;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface FactoryConfigDisplay<T> {
    Component name();

    Function<T,Component> tooltip();

    BiFunction<Component,T,Component> captionFunction();

    static Instance<Boolean> createToggle(Component name, Function<Boolean, Component> tooltip){
        return new Instance<>(name, tooltip, (c,b)-> b ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    static Instance<Boolean> createToggle(Component name){
        return createToggle(name, b-> null);
    }

    record Instance<T>(Component name, Function<T,Component> tooltip, BiFunction<Component,T,Component> captionFunction) implements FactoryConfigDisplay<T> {
        public Instance(Component name, BiFunction<Component,T,Component> captionFunction){
            this(name, v-> null, captionFunction);
        }

        public Instance(Component name, Function<T,Component> tooltip){
            this(name, tooltip, (c,v)-> c);
        }

        public Instance(Component name){
            this(name, (c,v)-> c);
        }
    }
}
