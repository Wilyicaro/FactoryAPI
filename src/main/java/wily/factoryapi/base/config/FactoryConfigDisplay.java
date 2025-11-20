package wily.factoryapi.base.config;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import wily.factoryapi.util.FactoryComponents;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface FactoryConfigDisplay<T> {
    Component name();

    Function<T, Component> tooltip();

    @Deprecated
    BiFunction<Component, T, Component> captionFunction();

    Function<T, Component> valueToComponent();

    BiFunction<FactoryConfigDisplay<T>, T, Component> messageFunction();

    default Component getMessage(T value) {
        return messageFunction().apply(this, value);
    }

    static Instance<Boolean> createToggle(Component name, Function<Boolean, Component> tooltip) {
        return toggleBuilder().tooltip(tooltip).build(name);
    }

    static Instance<Boolean> createToggle(Component name) {
        return toggleBuilder().build(name);
    }

    record Instance<T>(Component name, Function<T, Component> tooltip, Function<T, Component> valueToComponent, BiFunction<FactoryConfigDisplay<T>, T, Component> messageFunction, BiFunction<Component, T, Component> captionFunction) implements FactoryConfigDisplay<T> {
        public Instance(Component name, Function<T, Component> tooltip, Function<T, Component> valueToComponent, BiFunction<FactoryConfigDisplay<T>, T, Component> messageFunction) {
            this(name, tooltip, valueToComponent, messageFunction, (n, value) -> valueToComponent.apply(value));
        }

        @Deprecated
        public Instance(Component name, Function<T, Component> tooltip, BiFunction<Component, T, Component> captionFunction) {
            this(name, tooltip, t -> Component.literal(t.toString()), (display, value) -> captionFunction.apply(display.name(), value), captionFunction);
        }

        @Deprecated
        public Instance(Component name, BiFunction<Component, T, Component> captionFunction) {
            this(name, v -> null, captionFunction);
        }

        @Deprecated
        public Instance(Component name, Function<T, Component> tooltip) {
            this(name, tooltip, (c, v) -> c);
        }

        @Deprecated
        public Instance(Component name) {
            this(name, (c, v) -> c);
        }
    }

    class Builder<T> {
        private Function<T, Component> valueToComponent = t -> Component.literal(t.toString());
        private BiFunction<FactoryConfigDisplay<T>, T, Component> messageFunction = (display, t) -> CommonComponents.optionNameValue(display.name(), display.valueToComponent().apply(t));
        private Function<T, Component> tooltip = value -> null;

        public Builder<T> valueToComponent(Function<T, Component> valueToComponent) {
            this.valueToComponent = valueToComponent;
            return this;
        }

        public Builder<T> messageFunction(BiFunction<FactoryConfigDisplay<T>, T, Component> messageFunction) {
            this.messageFunction = messageFunction;
            return this;
        }

        public Builder<T> messageFunctionLabel(BiFunction<Component, Component, Component> messageFunction) {
            return messageFunction((display, t) -> messageFunction.apply(display.name(), display.valueToComponent().apply(t)));
        }

        public Builder<T> messageFunctionCaption(BiFunction<Component, T, Component> messageFunction) {
            return messageFunction((display, t) -> messageFunction.apply(display.name(), t));
        }

        public Builder<T> tooltip(Function<T, Component> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Instance<T> build(Component name) {
            return new Instance<>(name, tooltip, valueToComponent, messageFunction);
        }
    }

    static <T> Builder<T> builder() {
        return new Builder<>();
    }

    static Builder<Boolean> toggleBuilder() {
        return new Builder<Boolean>().valueToComponent((b) -> b ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    static Builder<Integer> intBuilder() {
        return builder();
    }

    static Builder<Integer> intPercentBuilder() {
        return intBuilder().messageFunctionLabel(FactoryComponents::percentValueLabel);
    }

    static Builder<Double> doubleBuilder() {
        return builder();
    }

    static Builder<Double> percentBuilder() {
        return doubleBuilder().valueToComponent(value -> Component.literal(String.valueOf((int) (value * 100)))).messageFunctionLabel(FactoryComponents::percentValueLabel);
    }
}
