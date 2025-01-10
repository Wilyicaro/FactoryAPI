package wily.factoryapi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class VariablesMap<K, V> implements Map<K, V> {
    protected Pattern pattern;
    private final Map<K, V> map;

    protected int changes = 0;
    protected View actualView = new View(this);
    protected boolean patternNeedsChange = true;

    protected void setChanged(){
        patternNeedsChange = true;
        changes++;
        actualView = new View(this);
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

    public View getView(){
        return actualView;
    }

    public record View(Map<?,?> map, int version){
        public View(VariablesMap<?,?> variablesMap){
            this(variablesMap, variablesMap.changes);
        }
    }
}
