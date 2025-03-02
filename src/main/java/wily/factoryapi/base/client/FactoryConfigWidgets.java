package wily.factoryapi.base.client;


import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.config.FactoryConfigControl;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class FactoryConfigWidgets {
    private static final List<WidgetOverride<?>> overrides = new ArrayList<>();
    public static final Function<Component,Tooltip> TOOLTIP_CACHE = Util.memoize(c->Tooltip.create(c));

    public static Tooltip getCachedTooltip(Component component){
        return component == null ? null : TOOLTIP_CACHE.apply(component);
    }

    public interface WidgetOverride<T> {
        AbstractWidget createWidget(FactoryConfig<T> config, Function<T,Tooltip> tooltip, int x, int y, int width, Consumer<T> afterSet);
    }

    public static <T> AbstractWidget getOverride(FactoryConfig<T> config, Function<T,Tooltip> tooltipFunction, int x, int y, int width, Consumer<T> afterSet){
        for (WidgetOverride<?> override : overrides) {
            AbstractWidget widgetOverride = ((WidgetOverride<T>)override).createWidget(config, tooltipFunction, x, y, width, afterSet);
            if (widgetOverride != null) return widgetOverride;
        }
        return null;
    }

    public static <T> AbstractWidget createWidget(FactoryConfig<T> config, int x, int y, int width, Consumer<T> afterSet) {
        Function<T,Tooltip> tooltipFunction = v-> getCachedTooltip(config.getDisplay().tooltip().apply(v));
        AbstractWidget override = getOverride(config, tooltipFunction, x, y, width, afterSet);
        if (override != null) return override;
        if (config.control().equals(FactoryConfigControl.TOGGLE)){
            return CycleButton.<Boolean>builder(b-> config.getDisplay().captionFunction().apply(config.getDisplay().name(), (T) b)).withValues(OptionInstance.BOOLEAN_VALUES.valueListSupplier()).withTooltip(((Function<Boolean, Tooltip>) tooltipFunction)::apply).withInitialValue((Boolean) config.get()).create(x, y, width, 20, config.getDisplay().name(), (cycleButton, object) -> FactoryConfig.saveOptionAndConsume(config, (T)object, afterSet));
        } else if (config.control() instanceof FactoryConfigControl.FromInt<T> c){
            return CycleButton.<T>builder(b-> config.getDisplay().captionFunction().apply(config.getDisplay().name(), b)).withValues(listSupplier(c.valueGetter(), c.valuesSize())).withTooltip(tooltipFunction::apply).withInitialValue(config.get()).create(x, y, width, 20, config.getDisplay().name(), (cycleButton, object) -> FactoryConfig.saveOptionAndConsume(config, object,afterSet));
        } else if (config.control() instanceof FactoryConfigControl.FromDouble<T> c){
            return new AbstractSliderButton(x, y, width, 16, config.getDisplay().captionFunction().apply(config.getDisplay().name(), config.get()), c.valueSetter().apply(config.get())) {
                @Override
                protected void updateMessage() {
                    setMessage(config.getDisplay().captionFunction().apply(config.getDisplay().name(), c.valueGetter().apply(value)));
                    setTooltip(tooltipFunction.apply(c.valueGetter().apply(value)));
                }

                @Override
                protected void applyValue() {
                    FactoryConfig.saveOptionAndConsume(config, c.valueGetter().apply(value), afterSet);
                    value = c.valueSetter().apply(config.get());
                }
            };
        } else if (config.control() instanceof FactoryConfigControl.Int c) {
            return CycleButton.<Integer>builder(b-> config.getDisplay().captionFunction().apply(config.getDisplay().name(), (T) b)).withValues(listSupplier(v-> c.min() + v, c.max()::getAsInt)).withTooltip(((Function<Integer, Tooltip>)tooltipFunction)::apply).withInitialValue((Integer) config.get()).create(x, y, width, 20, config.getDisplay().name(), (cycleButton, object) -> FactoryConfig.saveOptionAndConsume(config, (T)object, afterSet));
        } else if (config.control() instanceof FactoryConfigControl.TextEdit<T> c){
            EditBox editBox = new EditBox(Minecraft.getInstance().font, x, y, width, 20, config.getDisplay().name());

            c.codec().encodeStart(JsonOps.INSTANCE, config.get()).result().ifPresent(v-> editBox.setValue(v.toString()));
            editBox.setResponder(s->{
                DataResult<T> result;
                try {
                    result = c.codec().parse(JsonOps.INSTANCE, JsonParser.parseReader(new StringReader(s)));
                } catch (JsonIOException | JsonSyntaxException e) {
                    result = DataResult.error(e::getMessage);
                }
                if (result.result().isPresent()){
                    editBox.setTextColor(0xE0E0E0);
                    config.set(result.result().get());
                    config.save();
                    editBox.setTooltip(tooltipFunction.apply(config.get()));
                } else {
                    editBox.setTextColor(0xFF5555);
                    editBox.setTooltip(Tooltip.create(Component.literal(result.error().get().message())));
                }
            });
            return editBox;
        }
        return null;
    }

    public static <T> AbstractWidget createWidget(FactoryConfig<T> config) {
        return createWidget(config, 0, 0, 0, v-> {});
    }

    public static <T> CycleButton.ValueListSupplier<T> listSupplier(Function<Integer,T> valueGetter, Supplier<Integer> valuesSize){
        List<T> list = new ArrayList<>();
        for (int i = 0; i < valuesSize.get(); i++) {
            list.add(valueGetter.apply(i));
        }
        return CycleButton.ValueListSupplier.create(list);
    }
}