package wily.factoryapi.util;

public interface VariableResolver {
    Number getNumber(String name, Number defaultValue);
    Boolean getBoolean(String name, Boolean defaultValue);
}
