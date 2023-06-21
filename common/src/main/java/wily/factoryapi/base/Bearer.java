package wily.factoryapi.base;

public class Bearer<T> {
    private T object;
    public Bearer(T obj){
        object = obj;
    }
    public T get() {
        return object;
    }
    public void set(T obj){
        object = obj;
    }
    public static<T> Bearer<T> of(T obj){return new Bearer<>(obj);}
}
