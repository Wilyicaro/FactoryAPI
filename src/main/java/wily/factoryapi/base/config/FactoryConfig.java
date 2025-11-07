package wily.factoryapi.base.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.FactoryEvent;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.MinecraftServerAccessor;
import wily.factoryapi.base.network.CommonConfigSyncPayload;
import wily.factoryapi.base.network.CommonNetwork;
import wily.factoryapi.util.ListMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * An interface used for highly customizable server and client side configs.
 * <b>Don't</b> use this in the mixin loading context, as this can cause conflicts with mods that modify {@link com.mojang.serialization.Codec} or related, {@link FactoryMixinToggle} is meant to be used instead of that
 * <p>
 * This can be easily used for wrapping other configs to use in the FactoryAPI context, like vanilla's OptionInstance in the client side
 * @param <T> Type stored in the config instance
 */
public interface FactoryConfig<T> extends Bearer<T> {
    ListMap<ResourceLocation, StorageHandler> COMMON_STORAGES = new ListMap<>();

    static StorageHandler registerCommonStorage(ResourceLocation location, StorageHandler handler) {
        COMMON_STORAGES.put(location, handler);
        return handler;
    }

    Logger LOGGER = LogManager.getLogger("factory_config");

    String getKey();

    T defaultValue();
    
    default void setDefault(T value) {
    }

    default void reset() {
        set(defaultValue());
    }

    FactoryConfigControl<T> control();

    StorageAccess getStorageAccess();

    FactoryConfigDisplay<T> getDisplay();

    interface StorageAccess {
        void save();

        default <T,E> DataResult<E> encode(FactoryConfig<T> config, DynamicOps<E> ops) {
            return encode(config.control(), config.get(), ops);
        }

        default <T,E> DataResult<E> encode(FactoryConfigControl<T> control, T value, DynamicOps<E> ops) {
            return control.codec().encodeStart(ops, value);
        }

        default <T> DataResult<T> decode(FactoryConfig<T> config, Dynamic<?> dynamic) {
            return decode(config.control(), v -> whenParsed(config, v), dynamic);
        }

        default <T> DataResult<T> decode(FactoryConfigControl<T> control, Consumer<T> setter, Dynamic<?> dynamic) {
            DataResult<T> result = control.codec().parse(dynamic);
            result.result().ifPresent(setter);
            return result;
        }

        default <T> void whenParsed(FactoryConfig<T> config, T newValue) {
            config.set(newValue);
        }

        default <T> void sync(FactoryConfig<T> config) {
        }

        default boolean allowSync() {
            return false;
        }

        default boolean allowClientSync(Player player) {
            return allowSync() && (player != null && (player.hasPermissions(2) || FactoryAPIPlatform.getEntityServer(player).isSingleplayerOwner(/*? if >=1.21.9 {*//*player.nameAndId()*//*?} else {*/player.getGameProfile()/*?}*/)));
        }
    }

    static <T> void saveOptionAndConsume(FactoryConfig<T> config, T newValue, Consumer<T> consumer) {
        config.set(newValue);
        config.save();
        consumer.accept(newValue);
    }

    static FactoryConfig<Boolean> fromMixin(FactoryMixinToggle toggle, FactoryMixinToggle.Storage storage) {
        return create(toggle.key, toggle.configDisplay.get(), toggle.defaultValue, toggle, FactoryConfigControl.TOGGLE, b-> {}, storage::save);
    }

    class StorageHandler implements StorageAccess {
        public File file;
        public File defaultFile;
        public final Map<String, FactoryConfig<?>> configMap;
        protected final boolean allowSync;
        protected boolean serverOnly;

        public final FactoryEvent<Consumer<StorageHandler>> preLoad = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));
        public final FactoryEvent<Consumer<StorageHandler>> afterLoad = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));

        public final FactoryEvent<Consumer<StorageHandler>> preSave = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));
        public final FactoryEvent<Consumer<StorageHandler>> afterSave = new FactoryEvent<>(e-> m-> e.invokeAll(l->l.accept(m)));


        public StorageHandler(Map<String, FactoryConfig<?>> configMap, boolean allowSync) {
            this.configMap = configMap;
            this.allowSync = allowSync;
        }

        public StorageHandler(boolean allowSync) {
            this(new HashMap<>(), allowSync);
        }

        public StorageHandler() {
            this(false);
        }

        public StorageHandler withFile(File file) {
            this.file = file;
            return this;
        }

        public StorageHandler withDefaultFile(File file) {
            this.defaultFile = file;
            return this;
        }

        public static StorageHandler fromMixin(FactoryMixinToggle.Storage storage, boolean allowSync) {
            return new StorageHandler(storage.configMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e-> FactoryConfig.fromMixin(e.getValue(), storage))), allowSync) {
                @Override
                public void save() {
                    storage.save();
                }
                @Override
                public void load() {
                    storage.load();
                }
            };
        }

        public FactoryConfig<Boolean> getMixinToggle(FactoryMixinToggle toggle) {
            return (FactoryConfig<Boolean>) configMap.get(toggle.key);
        }

        public StorageHandler withFile(String configDirectoryFile) {
            return withFile(FactoryAPI.getConfigDirectory().resolve(configDirectoryFile).toFile());
        }

        public StorageHandler withDefaultFile(String configDirectoryFile) {
            return withDefaultFile(FactoryAPI.getConfigDirectory().resolve(configDirectoryFile).toFile());
        }

        public StorageHandler withServerFile(MinecraftServer server, String serverDirectoryFile) {
            this.serverOnly = true;
            return withFile(MinecraftServerAccessor.of(server).getStorageSource().getDimensionPath(Level.OVERWORLD).resolve(serverDirectoryFile).toFile());
        }

        public <T> void setAndSync(FactoryConfig<T> config, T value) {
            config.set(value);
            sync(config);
        }

        public <T> void sync(FactoryConfig<T> config) {
            if (!allowSync) return;
            if (FactoryAPI.currentServer == null) {
                CommonNetwork.sendToServer(CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_C2S, this, config));
            } else CommonNetwork.sendToPlayers(FactoryAPI.currentServer.getPlayerList().getPlayers(), CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_S2C, this, config));
        }

        public void sync() {
            if (!allowSync) return;
            if (FactoryAPI.currentServer == null) {
                CommonNetwork.sendToServer(CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_C2S, this));
            } else CommonNetwork.sendToPlayers(FactoryAPI.currentServer.getPlayerList().getPlayers(), CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_S2C, this));
        }

        @Override
        public boolean allowSync() {
            return allowSync;
        }

        public void reset() {
            configMap.values().forEach(FactoryConfig::reset);
        }

        public void resetAndLoad() {
            reset();
            load();
        }

        public boolean isServerManaged() {
            return allowSync || isServerOnly();
        }

        public boolean isServerOnly() {
            return serverOnly;
        }

        @Override
        public void save() {
            if (file == null) {
                LOGGER.warn("Failed to save config, its file wasn't set.");
                return;
            }
            if (isServerManaged() && FactoryAPI.currentServer == null && FactoryAPI.isClient() && FactoryAPIClient.hasLevel()) return;
            preSave.invoker.accept(this);
            if (FactoryConfig.save(file, configMap, false))
                afterSave.invoker.accept(this);
        }

        public void load() {
            if (file == null) {
                LOGGER.warn("Failed to load config, its file wasn't set.");
                return;
            }

            preLoad.invoker.accept(this);
            if (defaultFile != null)
                FactoryConfig.load(defaultFile, configMap, true);

            if (FactoryConfig.load(file, configMap, false))
                afterLoad.invoker.accept(this);
            else {
                reset();
                save();
            }
        }

        public <T> void decodeConfigs(Dynamic<T> dynamic) {
            FactoryConfig.decodeConfigs(configMap, dynamic);
        }

        public <T> T encodeConfigs(DynamicOps<T> ops) {
            return FactoryConfig.encodeConfigs(configMap, ops);
        }

        public <T> FactoryConfig<T> register(String key, FactoryConfig<T> config) {
            configMap.put(key,config);
            return config;
        }

        public <T> FactoryConfig<T> register(FactoryConfig<T> config) {
            return register(config.getKey(), config);
        }
    }

    default void save() {
        getStorageAccess().save();
    }

    default void sync() {
        getStorageAccess().sync(this);
    }

    default DataResult<T> decode(Dynamic<?> dynamic) {
        return getStorageAccess().decode(this,dynamic);
    }

    default DataResult<T> decodeDefault(Dynamic<?> dynamic) {
        return getStorageAccess().decode(control(), this::setDefault, dynamic);
    }

    default <E> DataResult<E> encode(DynamicOps<E> ops) {
        return getStorageAccess().encode(this, ops);
    }

    default <E> DataResult<E> encodeDefault(DynamicOps<E> ops) {
        return getStorageAccess().encode(control(), defaultValue(), ops);
    }

    static <T> FactoryConfig<T> create(String key, FactoryConfigDisplay<T> display, T defaultValue, Bearer<T> bearer, FactoryConfigControl<T> control, Consumer<T> consumer, StorageAccess access) {
        return new Instance<>(key, display, defaultValue, bearer, control, consumer, access);
    }
    
    class Instance<T> implements FactoryConfig<T> {
        private final String key;
        private final FactoryConfigDisplay<T> display;
        private T defaultValue;
        private final Bearer<T> bearer;
        private final FactoryConfigControl<T> control;
        private final Consumer<T> consumer;
        private final StorageAccess access;

        public Instance(String key, FactoryConfigDisplay<T> display, T defaultValue, Bearer<T> bearer, FactoryConfigControl<T> control, Consumer<T> consumer, StorageAccess access) {
            this.key = key;
            this.display = display;
            this.defaultValue = defaultValue;
            this.bearer = bearer;
            this.control = control;
            this.consumer = consumer;
            this.access = access;
        }
        
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public T defaultValue() {
            return defaultValue;
        }

        @Override
        public void setDefault(T value) {
            this.defaultValue = value;
        }

        @Override
        public FactoryConfigControl<T> control() {
            return control;
        }

        @Override
        public StorageAccess getStorageAccess() {
            return access;
        }

        @Override
        public FactoryConfigDisplay<T> getDisplay() {
            return display;
        }

        @Override
        public void set(T t) {
            bearer.set(t);
            consumer.accept(t);
        }

        @Override
        public T get() {
            return bearer.get();
        }
    }

    class Builder<T> {
        private String key;
        private T defaultValue;
        private FactoryConfigDisplay<T> display;
        private FactoryConfigControl<T> control;
        private Consumer<T> afterSet = value -> {};

        public Builder<T> key(String key) {
            this.key = key;
            return this;
        }

        public Builder<T> display(FactoryConfigDisplay<T> display) {
            this.display = display;
            return this;
        }

        public Builder<T> displayFromKey(Function<String,FactoryConfigDisplay<T>> display) {
            return display(display.apply(key));
        }

        public Builder<T> control(FactoryConfigControl<T> control) {
            this.control = control;
            return this;
        }

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> afterSet(Consumer<T> afterSet) {
            this.afterSet = afterSet;
            return this;
        }

        public FactoryConfig<T> build(StorageAccess access) {
            return create(
                    Objects.requireNonNull(key, "The key of FactoryConfig can't be null"),
                    display,
                    Objects.requireNonNull(control, "The control of FactoryConfig can't be null"),
                    Objects.requireNonNull(defaultValue, "The default value of FactoryConfig can't be null"),
                    afterSet,
                    access);
        }

        public FactoryConfig<T> buildAndRegister(StorageHandler handler) {
            return handler.register(build(handler));
        }

        public static Builder<Boolean> createToggle() {
            return new Builder<Boolean>().control(FactoryConfigControl.TOGGLE);
        }
    }

    static FactoryConfig<Boolean> createBoolean(String key, FactoryConfigDisplay<Boolean> display, boolean defaultValue, Consumer<Boolean> consumer, StorageAccess access) {
        return create(key, display, defaultValue, Bearer.of(defaultValue), FactoryConfigControl.TOGGLE, consumer, access);
    }

    static FactoryConfig<Integer> createInteger(String key, FactoryConfigDisplay<Integer> display, int min, int max, int defaultValue, Consumer<Integer> consumer, StorageAccess access) {
        return createInteger(key, display, new FactoryConfigControl.Int(min, ()-> max, Integer.MAX_VALUE), defaultValue, consumer, access);
    }

    static FactoryConfig<Integer> createInteger(String key, FactoryConfigDisplay<Integer> display, FactoryConfigControl<Integer> control, int defaultValue, Consumer<Integer> consumer, StorageAccess access) {
        return create(key, display, control, defaultValue, consumer, access);
    }

    static <T> FactoryConfig<T> create(String key, FactoryConfigDisplay<T> display, FactoryConfigControl<T> control, T defaultValue, Consumer<T> consumer, StorageAccess access) {
        return create(key, display, defaultValue, Bearer.of(defaultValue), control, consumer, access);
    }

    static boolean hasCommonConfigEnabled(FactoryConfig<Boolean> config, boolean defaultValue) {
        return (FactoryAPI.isClient() && config.getStorageAccess() instanceof StorageHandler h && !FactoryAPIClient.hasModOnServer(COMMON_STORAGES.getKey(h).getNamespace()) ? defaultValue : config.get());
    }

    static boolean hasCommonConfigEnabled(FactoryConfig<Boolean> config) {
        return hasCommonConfigEnabled(config, false);
    }

    static <T> void decodeConfigs(Map<String,? extends FactoryConfig<?>> configs, Dynamic<T> dynamic) {
        dynamic.asMapOpt().result().ifPresent(m->m.forEach(p-> p.getFirst().asString().result().ifPresent(s-> {
            FactoryConfig<?> config = configs.get(s);
            if (config == null) {
                LOGGER.warn("Config named as {} with value {} wasn't found", s, p.getSecond().toString());
            } else config.decode(p.getSecond());
        })));
    }

    static <T> T encodeConfigs(Map<String,? extends FactoryConfig<?>> configs, DynamicOps<T> ops) {
        return ops.createMap(configs.entrySet().stream().collect(Collectors.toMap(e-> ops.createString(e.getKey()), e-> e.getValue().encode(ops).result().orElseThrow())));
    }

    static boolean load(File file, Map<String,? extends FactoryConfig<?>> configs, boolean isDefault) {
        if (!file.exists()) {
            return false;
        }

        boolean makeBackup = false;

        try (BufferedReader r = Files.newReader(file, Charsets.UTF_8)) {
            if (JsonParser.parseReader(r) instanceof JsonObject obj) {
                obj.asMap().forEach((s, e)->{
                    FactoryConfig<?> config = configs.get(s);
                    if (config == null) {
                        LOGGER.warn("Config named as {} from {} config file wasn't found", s, file.toString());
                    } else {
                        if (isDefault)
                            config.decodeDefault(new Dynamic<>(JsonOps.INSTANCE, e));
                        else
                            config.decode(new Dynamic<>(JsonOps.INSTANCE, e));
                    }
                });
            } else {
                LOGGER.warn("Config file {} can't be loaded, it's certainly corrupt or in a wrong syntax", file.toString());
                makeBackup = true;
            }

        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Failed to load the config {}: {}", file.toString(), e.getMessage());
            makeBackup = true;
        }

        if (makeBackup) {
            File invalidJson = new File(file.getParent(), file.getName() + "_old");
            try {
                file.renameTo(invalidJson);
            } catch (SecurityException e) {
                LOGGER.warn("Failed to make a backup of {} config file to {}: {}", file.toString(), invalidJson.toString(), e.getMessage());
            }
        }

        return !makeBackup;
    }

    static boolean save(File file, Map<String,? extends FactoryConfig<?>> configs, boolean isDefault) {
        File parent = file.getParentFile();
        if (!parent.exists()) parent.mkdirs();
        try (JsonWriter w = new JsonWriter(Files.newWriter(file, Charsets.UTF_8))) {
            w.setSerializeNulls(false);
            w.setIndent("  ");
            JsonObject obj = new JsonObject();
            configs.forEach((s,config)-> (isDefault ? config.encodeDefault(JsonOps.INSTANCE) : config.encode(JsonOps.INSTANCE)).resultOrPartial(error-> LOGGER.warn("Failed to save config named as {} from {} config file: {}",s, file.toString(),error)).ifPresent(e-> obj.add(s,e)));
            GsonHelper.writeValue(w,obj, Comparator.naturalOrder());
            return true;
        } catch (IOException e) {
            LOGGER.warn("Failed to save the config {}: {}", file.toString(), e);
            return false;
        }
    }
}
