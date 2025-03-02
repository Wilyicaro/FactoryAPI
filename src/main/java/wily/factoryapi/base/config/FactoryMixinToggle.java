package wily.factoryapi.base.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.Stocker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FactoryMixinToggle extends Stocker<Boolean> {
    public static final Logger LOGGER = LogManager.getLogger("factory_mixin_config");
    public final String key;
    public final boolean defaultValue;
    public final ArbitrarySupplier<FactoryConfigDisplay<Boolean>> configDisplay;

    public FactoryMixinToggle(String key, Boolean defaultValue, ArbitrarySupplier<FactoryConfigDisplay<Boolean>> configDisplay) {
        super(defaultValue);
        this.key = key;
        this.defaultValue = defaultValue;
        this.configDisplay = configDisplay;
    }

    public record Storage(File file, Map<String, FactoryMixinToggle> configMap) {
        public Storage(File file){
            this(file, new HashMap<>());
        }

        public Storage(String configDirectoryFile){
            this(FactoryAPI.getConfigDirectory().resolve(configDirectoryFile).toFile());
        }

        public FactoryMixinToggle register(String key, FactoryMixinToggle config){
            configMap.put(key,config);
            return config;
        }

        public FactoryMixinToggle register(FactoryMixinToggle config){
            return register(config.key, config);
        }

        public boolean get(String key){
            return !configMap.containsKey(key) || configMap.get(key).get();
        }

        public boolean getFormatted(String target, String key){
            return get(format(target, key));
        }

        public String format(String target, String mixinClass){
            mixinClass = mixinClass.replace(target,"");
            return mixinClass.substring(0, mixinClass.lastIndexOf("."));
        }

        public void load() {
            if (!file.exists()) {
                save();
                return;
            }
            try (BufferedReader r = Files.newReader(file, Charsets.UTF_8)) {
                GsonHelper.parse(r).asMap().forEach((s, e) -> {
                    FactoryMixinToggle config = configMap.get(s);
                    if (config == null) {
                        LOGGER.warn("Config named as {} from {} config file wasn't found", s, file.toString());
                    } else config.set(e.getAsBoolean());
                });
            } catch (IOException e) {
                LOGGER.warn("Failed to load the config {}: {}", file.toString(), e);
            }
        }

        public void save() {
            File parent = file.getParentFile();
            if (!parent.exists()) parent.mkdirs();
            try (JsonWriter w = new JsonWriter(Files.newWriter(file, Charsets.UTF_8))) {
                w.setSerializeNulls(false);
                w.setIndent("  ");
                JsonObject obj = new JsonObject();
                configMap.forEach((s, config) -> obj.add(s, new JsonPrimitive(config.get())));
                GsonHelper.writeValue(w, obj, Comparator.naturalOrder());
            } catch (IOException e) {
                LOGGER.warn("Failed to save the config {}: {}", file.toString(), e);
            }
        }
    }


}
