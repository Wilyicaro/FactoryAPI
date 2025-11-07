package wily.factoryapi.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

public class ElementId {
    public static final LoadingCache<String[], ElementId> CACHE = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(4)).build(CacheLoader.from(ElementId::create));
    protected static final LoadingCache<String[], ElementId> PROCESSED_CACHE = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build(CacheLoader.from(ElementId::new));

    protected final String[] parts;
    protected String toString;
    protected final int hash;

    protected ElementId(String[] parts) {
        this.parts = parts;
        this.hash = Arrays.hashCode(parts);
    }

    protected static ElementId create(String[] parts) {
        return new ElementId(processParts(parts));
    }

    protected static String[] processParts(String[] parts) {
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.contains(".")) {
                String[] newParts = part.split("\\.");
                parts = ArrayUtils.insert(i, ArrayUtils.remove(parts, i), newParts);
                i+= newParts.length-1;
            }
        }
        return parts;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = String.join(".", parts);
        }
        return toString;
    }

    public static ElementId of(String... parts) {
        return CACHE.getUnchecked(parts);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ElementId id && Arrays.equals(parts, id.parts);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static ElementId of(String path, @Nullable ElementId thisReplacement) {
        String[] parsed = of(path).parts;

        if (thisReplacement != null && parsed[0].equals("this")) {
            parsed = ArrayUtils.remove(parsed, 0);
            parsed = ArrayUtils.insert(0, parsed, thisReplacement.parts);
        }
        return PROCESSED_CACHE.getUnchecked(parsed);
    }

    public String get(int index) {
        return parts[index];
    }

    public String last() {
        return get(parts.length - 1);
    }

    public ElementId parent() {
        return PROCESSED_CACHE.getUnchecked(ArrayUtils.remove(parts, parts.length - 1));
    }

    public ElementId resolve(String child) {
        return PROCESSED_CACHE.getUnchecked(ArrayUtils.addAll(parts, ElementId.of(child).parts));
    }

}
