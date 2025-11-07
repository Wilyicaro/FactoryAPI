package wily.factoryapi.util;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A utility Map for slow operations with few entries, composed by two lists
 * <p>
 * The values and keys are obtained by iterations, so <b>don't</b> use this implementation if you only need a way to get a key by a value.
**/
public class ListMap<K,V> implements DualMap<K,V> {
    final Map<K, V> backendMap;
    final List<K> keys;
    final List<V> values;

    public ListMap(Builder<K,V> builder) {
        backendMap = new HashMap<>();
        keys = builder.keys;
        values = builder.values;
        if (keys.size() != values.size()) 
            throw new UnsupportedOperationException("Invalid ListMap Builder: It should have the same amount of keys and values!");
        else {
            for (int i = 0; i < keys.size(); i++) {
                backendMap.put(keys.get(i), values.get(i));
            }
        }
    }

    public ListMap() {
        this(new Builder<>());
    }

    final Set<Entry<K,V>> backendSet = new AbstractSet<>() {
        @Override
        public int size() {
            return ListMap.this.size();
        }

        @Override
        public @NotNull Iterator<Entry<K, V>> iterator() {
            return new Iterator<>() {
                int actual = 0;
                boolean canRemove = false;

                @Override
                public boolean hasNext() {
                    return actual < size();
                }

                @Override
                public Entry<K, V> next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more elements.");
                    }
                    canRemove = true;
                    Entry<K,V> entry = new AbstractMap.SimpleEntry<>(keys.get(actual), values.get(actual)) {
                        @Override
                        public V setValue(V value) {
                            return put(getKey(), value);
                        }
                    };
                    actual++;
                    return entry;
                }

                @Override
                public void remove() {
                    if (!canRemove) {
                        throw new IllegalStateException("Cannot remove without calling next()");
                    }
                    ListMap.this.remove(keys.get(--actual));
                    canRemove = false;
                }
            };
        }

        @Override
        public void clear() {
            ListMap.this.clear();
        }
    };

    public record Builder<K,V>(List<K> keys, List<V> values) {
        public Builder() {
            this(new ArrayList<>(),new ArrayList<>());
        }

        public Builder<K,V> put(K key, V value) {
            keys.add(key);
            values.add(value);
            return this;
        }

        public <OK,OV> Builder<OK,OV> mapEntries(Function<K,OK> keyMapper, Function<V,OV> valueMapper) {
            return new Builder<>(keys.stream().map(keyMapper).collect(Collectors.toList()),values.stream().map(valueMapper).collect(Collectors.toList()));
        }

        public <OK> Builder<OK,V> mapKeys(Function<K,OK> keyMapper) {
            return new Builder<>(keys.stream().map(keyMapper).collect(Collectors.toList()),values);
        }

        public <OV> Builder<K,OV> mapValues(Function<V,OV> valueMapper) {
            return new Builder<>(keys,values.stream().map(valueMapper).collect(Collectors.toList()));
        }

        public ListMap<K,V> build() {
            return new ListMap<>(this);
        }
    }

    public static <K,V> Builder<K,V> builder() {
        return new Builder<>();
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return keys.contains(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return values.contains(o);
    }

    @Override
    public V get(Object o) {
        return backendMap.get(o);
    }

    public V getByIndex(int i) {
        return values.get(i);
    }

    @Override
    public K getKey(V value) {
        return getKeyOrDefault(value,null);
    }

    public K getKeyByIndex(int i) {
        return keys.get(i);
    }

    public K getKeyOrDefault(V value, K defaultKey) {
        return containsValue(value) ? keys.get(values.indexOf(value)) : defaultKey;
    }

    public int indexOf(V value) {
        return values.indexOf(value);
    }

    public int indexOfKey(K key) {
        return keys.indexOf(key);
    }

    @Override
    public @Nullable V put(K k, V v) {
        V oldValue = null;
        if (!containsKey(k)) {
            keys.add(k);
            values.add(v);
            backendMap.put(k, v);
        }else oldValue = values.set(keys.indexOf(k),v);
        return oldValue;
    }

    @Override
    public V remove(Object o) {
        int index = keys.indexOf(o);
        if (index >= 0) {
            keys.remove(index);
            backendMap.remove(o);
            return values.remove(index);
        }
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        keys.clear();
        values.clear();
        backendMap.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return Set.copyOf(keys);
    }

    @Override
    public @NotNull Collection<V> values() {
        return values;
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return backendSet;
    }
}
