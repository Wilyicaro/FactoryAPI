package wily.factoryapi.util;

import com.mojang.serialization.Codec;

import java.util.*;

public interface DualMap<K,V> extends Map<K,V> {
    /**
     * Returns the key to which the specified value is mapped, or {@code null} if this map contains no mapping for the value.
     *
     * <p>More formally, if this map contains a mapping from a value
     * {@link V v} to a key {@link K k} such that
     * {@link java.util.Objects#equals(V value, V v)},
     * then this method returns {@link K k}; otherwise
     * it returns null. (There can be at most one such mapping.)
     *
     * <p> If this map permits null values, then a return value of null does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to null. The {@link #containsValue
     * containsValue} operation may be used to distinguish these two cases.
     *
     * @param value the value whose associated key is to be returned
     * @return the key to which the specified value is mapped, or {@code null} if this map contains no mapping for the value
     * @throws ClassCastException – if the key is of an inappropriate type for this map (optional)
     * @throws NullPointerException – if the specified key is null and this map does not permit null keys (optional)
     */
    K getKey(V value);

    /**
     * Returns the key to which the specified value is mapped, or {@link K defaultKey} if this map contains no mapping for the value.
     *
     * <p>More formally, if this map contains a mapping from a value
     * {@link V v} to a key {@link K k} such that
     * {@link java.util.Objects#equals(V value, V v)},
     * then this method returns {@link K k}; otherwise
     * it returns {@link K defaultKey}. (There can be at most one such mapping.)
     *
     * @param value the value whose associated key is to be returned
     * @param defaultKey the value whose associated key is to be returned if this map contains no mapping for the key
     * @return the key to which the specified value is mapped, or {@link K defaultKey} if this map contains no mapping for the value
     * @throws ClassCastException – if the key is of an inappropriate type for this map (optional)
     * @throws NullPointerException – if the specified key is null and this map does not permit null keys (optional)
     */
    K getKeyOrDefault(V value, K defaultKey);

    default Codec<V> createCodec(Codec<K> keyCodec){
        return keyCodec.xmap(this::get,this::getKey);
    }

    default Codec<K> createKeyCodec(Codec<V> codec){
        return codec.xmap(this::getKey,this::get);
    }

    default Codec<V> createCodec(Codec<K> keyCodec, K defaultKey, V defaultValue){
        return keyCodec.xmap(k-> getOrDefault(k, defaultValue), v-> getKeyOrDefault(v, defaultKey));
    }

    default Codec<K> createKeyCodec(Codec<V> codec, K defaultKey, V defaultValue){
        return codec.xmap(v-> getKeyOrDefault(v, defaultKey), k-> getOrDefault(k, defaultValue));
    }

}
