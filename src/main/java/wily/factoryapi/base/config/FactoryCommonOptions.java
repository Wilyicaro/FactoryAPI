package wily.factoryapi.base.config;

import wily.factoryapi.util.FactoryComponents;

public class FactoryCommonOptions {
    public static final FactoryConfig.StorageHandler COMMON_STORAGE = new FactoryConfig.StorageHandler().withFile("factory_api/common_options.json");
    public static final FactoryConfig<Boolean> EXPRESSION_FAIL_LOGGING = COMMON_STORAGE.register(FactoryConfig.createBoolean("expressionFailLogging", FactoryConfigDisplay.createToggle(FactoryComponents.optionsName("expressionFailLogging")), true, b-> {}, COMMON_STORAGE));
}
