package wily.factoryapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VariablesMap<K, V> implements Map<K, V> {
    protected Pattern pattern;
    private final Map<K, V> map;

    protected boolean patternNeedsChange = true;

    protected void setChanged(){
        patternNeedsChange = true;
    }

    public Pattern getPattern(){
        return pattern;
    }

    public void updatePattern(){
        if (patternNeedsChange) {
            pattern = Pattern.compile("\\$\\{(" + String.join("|", keySet().toString()) + ")}");
            patternNeedsChange = false;
        }
    }

    public VariablesMap(){
        this(new HashMap<>());
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public VariablesMap(Map<K, V> map){
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public @Nullable V put(K key, V value) {
        setChanged();
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V removed = map.remove(key);
        if (removed != null) setChanged();
        return removed;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        setChanged();
        map.putAll(m);
    }

    @Override
    public void clear() {
        setChanged();
        map.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return map.values();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }


    @Override
    public String toString() {
        if (isEmpty()) return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        var iterator = entrySet().iterator();
        while (iterator.hasNext()){
            var entry = iterator.next();
            sb.append(entry.getKey() == this ? "(this Map)" : entry.getKey());
            sb.append('=');
            sb.append(entry.getValue() == this ? "(this Map)" : entry.getValue() instanceof Supplier<?> s ?"()->"+ s.get() : entry.getValue());
            if (iterator.hasNext()) sb.append(',').append(' ');
        }
        sb.append('}');
        return sb.toString();
    }
}
