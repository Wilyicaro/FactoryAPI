package wily.factoryapi.base.config;

import com.mojang.serialization.Codec;

import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public interface FactoryConfigControl<T> {
    Codec<T> codec();
    FactoryConfigControl<Boolean> TOGGLE = ()->Codec.BOOL;

    record FromInt<T>(Codec<T> codec, Function<Integer,T> valueGetter, Function<T,Integer> valueSetter, Supplier<Integer> valuesSize) implements FactoryConfigControl<T> {
        public FromInt(Function<Integer,T> valueGetter, Function<T,Integer> valueSetter, Supplier<Integer> valuesSize){
            this(Codec.INT.xmap(valueGetter,valueSetter), valueGetter, valueSetter, valuesSize);
        }
    }

    record FromDouble<T>(Codec<T> codec, Function<Double,T> valueGetter, Function<T,Double> valueSetter) implements FactoryConfigControl<T> {
    }

    static FromDouble<Double> createDouble(){
        return new FromDouble<>(Codec.DOUBLE, v-> v, v-> v);
    }

    record Int(Codec<Integer> codec, int min, IntSupplier max, int maxEncodable) implements FactoryConfigControl<Integer> {
        public Int(int min, IntSupplier max, int maxEncodable){
            this(Codec.intRange(min,maxEncodable), min, max, maxEncodable);
        }
    }

    record TextEdit<T>(Codec<T> codec) implements FactoryConfigControl<T> {

    }
}
