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
import wily.factoryapi.base.ArbitrarySupplier;
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
import java.util.function.*;
import java.util.stream.Collectors;

public interface FactoryConfig<T> extends Bearer<T> {
    ListMap<ResourceLocation, StorageHandler> COMMON_STORAGES = new ListMap<>();

    static StorageHandler registerCommonStorage(ResourceLocation location, StorageHandler handler){
        COMMON_STORAGES.put(location, handler);
        return handler;
    }

    Logger LOGGER = LogManager.getLogger("factory_config");

    String getKey();

    T defaultValue();

    default void reset(){
        set(defaultValue());
    }

    FactoryConfigControl<T> control();

    StorageAccess getStorageAccess();

    FactoryConfigDisplay<T> getDisplay();

    interface StorageAccess {
        void save();

        default <T,E> DataResult<E> encode(FactoryConfig<T> config, DynamicOps<E> ops) {
            return config.control().codec().encodeStart(ops,config.get());
        }

        default <T> DataResult<T> decode(FactoryConfig<T> config, Dynamic<?> dynamic) {
            DataResult<T> result = config.control().codec().parse(dynamic);
            result.result().ifPresent(v->whenParsed(config,v));
            return result;
        }

        default <T> void whenParsed(FactoryConfig<T> config, T newValue) {
            config.set(newValue);
        }

        default <T> void sync(FactoryConfig<T> config) {
        }

        default boolean allowSync(){
            return false;
        }

        default boolean allowClientSync(Player player){
            return allowSync() && (player != null && (player.hasPermissions(2) || player.getServer().isSingleplayerOwner(player.getGameProfile())));
        }
    }

    static <T> void saveOptionAndConsume(FactoryConfig<T> config, T newValue, Consumer<T> consumer) {
        config.set(newValue);
        config.save();
        consumer.accept(newValue);
    }

    static FactoryConfig<Boolean> fromMixin(FactoryMixinToggle toggle, FactoryMixinToggle.Storage storage){
        return create(toggle.key, toggle.configDisplay.get(), toggle.defaultValue, toggle, FactoryConfigControl.TOGGLE, b-> {}, storage::save);
    }

    class StorageHandler implements StorageAccess {
        public File file;
        public final Map<String, FactoryConfig<?>> configMap;
        protected final boolean allowSync;
        protected boolean serverOnly;

        public StorageHandler(Map<String, FactoryConfig<?>> configMap, boolean allowSync){
            this.configMap = configMap;
            this.allowSync = allowSync;
        }

        public StorageHandler(boolean allowSync){
            this(new HashMap<>(), allowSync);
        }

        public StorageHandler(){
            this(false);
        }

        public StorageHandler withFile(File file){
            this.file = file;
            return this;
        }

        public static StorageHandler fromMixin(FactoryMixinToggle.Storage storage, boolean allowSync){
            return new StorageHandler(storage.configMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e-> FactoryConfig.fromMixin(e.getValue(), storage))), allowSync){
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

        public FactoryConfig<Boolean> getMixinToggle(FactoryMixinToggle toggle){
            return (FactoryConfig<Boolean>) configMap.get(toggle.key);
        }

        public StorageHandler withFile(String configDirectoryFile){
            return withFile(FactoryAPI.getConfigDirectory().resolve(configDirectoryFile).toFile());
        }

        public StorageHandler withServerFile(MinecraftServer server, String serverDirectoryFile){
            this.serverOnly = true;
            return withFile(MinecraftServerAccessor.of(server).getStorageSource().getDimensionPath(Level.OVERWORLD).resolve(serverDirectoryFile).toFile());
        }

        public <T> void setAndSync(FactoryConfig<T> config, T value){
            config.set(value);
            sync(config);
        }

        public <T> void sync(FactoryConfig<T> config){
            if (!allowSync) return;
            if (FactoryAPI.currentServer == null) {
                CommonNetwork.sendToServer(CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_C2S, this, config));
            } else CommonNetwork.sendToPlayers(FactoryAPI.currentServer.getPlayerList().getPlayers(), CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_S2C, this, config));
        }

        public void sync(){
            if (!allowSync) return;
            if (FactoryAPI.currentServer == null) {
                CommonNetwork.sendToServer(CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_C2S, this));
            } else CommonNetwork.sendToPlayers(FactoryAPI.currentServer.getPlayerList().getPlayers(), CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_S2C, this));
        }

        @Override
        public boolean allowSync() {
            return allowSync;
        }

        public void reset(){
            configMap.values().forEach(FactoryConfig::reset);
        }

        public void resetAndLoad(){
            reset();
            load();
        }

        public boolean isServerManaged(){
            return allowSync || isServerOnly();
        }

        public boolean isServerOnly(){
            return serverOnly;
        }

        @Override
        public void save() {
            if (file == null) {
                LOGGER.warn("Failed to save config, its file wasn't set.");
                return;
            }
            if (isServerManaged() && FactoryAPI.currentServer == null && FactoryAPI.isClient() && FactoryAPIClient.hasLevel()) return;
            FactoryConfig.save(file, configMap);
        }

        public void load(){
            if (file == null) {
                LOGGER.warn("Failed to load config, its file wasn't set.");
                return;
            }
            FactoryConfig.load(file, configMap);
        }

        public <T> void decodeConfigs(Dynamic<T> dynamic){
            FactoryConfig.decodeConfigs(configMap, dynamic);
        }

        public <T> T encodeConfigs(DynamicOps<T> ops){
            return FactoryConfig.encodeConfigs(configMap, ops);
        }

        public <T> FactoryConfig<T> register(String key, FactoryConfig<T> config){
            configMap.put(key,config);
            return config;
        }

        public <T> FactoryConfig<T> register(FactoryConfig<T> config){
            return register(config.getKey(), config);
        }
    }

    default void save(){
        getStorageAccess().save();
    }

    default void sync(){
        getStorageAccess().sync(this);
    }

    default DataResult<T> decode(Dynamic<?> dynamic) {
        return getStorageAccess().decode(this,dynamic);
    }

    default <E> DataResult<E> encode(DynamicOps<E> ops) {
        return getStorageAccess().encode(this,ops);
    }

    static <T> FactoryConfig<T> create(String key, FactoryConfigDisplay<T> display, T defaultValue, Bearer<T> bearer, FactoryConfigControl<T> control, Consumer<T> consumer, StorageAccess access){
        return new FactoryConfig<>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public T defaultValue() {
                return defaultValue;
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
        };
    }

    class Builder<T> {
        private String key;
        private T defaultValue;
        private FactoryConfigDisplay<T> display;
        private FactoryConfigControl<T> control;
        private Consumer<T> afterSet = value -> {};

        public Builder<T> key(String key){
            this.key = key;
            return this;
        }

        public Builder<T> display(FactoryConfigDisplay<T> display){
            this.display = display;
            return this;
        }

        public Builder<T> displayFromKey(Function<String,FactoryConfigDisplay<T>> display){
            return display(display.apply(key));
        }

        public Builder<T> control(FactoryConfigControl<T> control){
            this.control = control;
            return this;
        }

        public Builder<T> defaultValue(T defaultValue){
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> afterSet(Consumer<T> afterSet){
            this.afterSet = afterSet;
            return this;
        }

        public FactoryConfig<T> build(StorageAccess access){
            return create(key, display, control, defaultValue, afterSet, access);
        }

        public FactoryConfig<T> buildAndRegister(StorageHandler handler){
            return handler.register(build(handler));
        }

        public static Builder<Boolean> createToggle(){
            return new Builder<Boolean>().control(FactoryConfigControl.TOGGLE);
        }
    }

    static FactoryConfig<Boolean> createBoolean(String key, FactoryConfigDisplay<Boolean> display, boolean defaultValue, Consumer<Boolean> consumer, StorageAccess access){
        return create(key, display, defaultValue, Bearer.of(defaultValue), FactoryConfigControl.TOGGLE, consumer, access);
    }

    static FactoryConfig<Integer> createInteger(String key, FactoryConfigDisplay<Integer> display, int min, int max, int defaultValue, Consumer<Integer> consumer, StorageAccess access){
        return createInteger(key, display, new FactoryConfigControl.Int(min, ()-> max, Integer.MAX_VALUE), defaultValue, consumer, access);
    }

    static FactoryConfig<Integer> createInteger(String key, FactoryConfigDisplay<Integer> display, FactoryConfigControl<Integer> control, int defaultValue, Consumer<Integer> consumer, StorageAccess access){
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

    static <T> void decodeConfigs(Map<String,? extends FactoryConfig<?>> configs, Dynamic<T> dynamic){
        dynamic.asMapOpt().result().ifPresent(m->m.forEach(p-> p.getFirst().asString().result().ifPresent(s-> {
            FactoryConfig<?> config = configs.get(s);
            if (config == null){
                LOGGER.warn("Config named as {} with value {} wasn't found", s, p.getSecond().toString());
            } else config.decode(p.getSecond());
        })));
    }

    static <T> T encodeConfigs(Map<String,? extends FactoryConfig<?>> configs, DynamicOps<T> ops){
        return ops.createMap(configs.entrySet().stream().collect(Collectors.toMap(e-> ops.createString(e.getKey()), e-> e.getValue().encode(ops).result().orElseThrow())));
    }

    static void load(File file, Map<String,? extends FactoryConfig<?>> configs){
        if (!file.exists()) {
            save(file, configs);
            return;
        }
        try (BufferedReader r = Files.newReader(file, Charsets.UTF_8)){
            JsonParser.parseReader(r).getAsJsonObject().asMap().forEach((s, e)->{
                FactoryConfig<?> config = configs.get(s);
                if (config == null) {
                    LOGGER.warn("Config named as {} from {} config file wasn't found",s, file.toString());
                } else config.decode(new Dynamic<>(JsonOps.INSTANCE,e));
            });
        } catch (IOException e) {
            LOGGER.warn("Failed to load the config {}: {}",file.toString(),e);
        }
    }

    static void save(File file, Map<String,? extends FactoryConfig<?>> configs){
        File parent = file.getParentFile();
        if (!parent.exists()) parent.mkdirs();
        try (JsonWriter w = new JsonWriter(Files.newWriter(file, Charsets.UTF_8))){
            w.setSerializeNulls(false);
            w.setIndent("  ");
            JsonObject obj = new JsonObject();
            configs.forEach((s,config)-> config.encode(JsonOps.INSTANCE).resultOrPartial(error-> LOGGER.warn("Failed to save config named as {} from {} config file: {}",s, file.toString(),error)).ifPresent(e-> obj.add(s,e)));
            GsonHelper.writeValue(w,obj, Comparator.naturalOrder());
        } catch (IOException e) {
            LOGGER.warn("Failed to save the config {}: {}",file.toString(),e);
        }
    }
}
