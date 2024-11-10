package wily.factoryapi.base;


public class Stocker<T> implements Bearer<T> {
    private T object;
    public Stocker(T obj){
        object = obj;
    }
    public T get() {
        return object;
    }
    public void set(T obj){
        object = obj;
    }
    public static<T> Stocker<T> of(T obj){return new Stocker<>(obj);}
    public static int cyclic(int min, int i, int max){
        return i >= max ? min : i < min ? max - 1 : i;
    }
    public static class Sizeable extends Stocker<Integer>{
        public int max = 0;
        public int min = 0;

        public Sizeable(Integer i) {
            super(i);
        }
        public Sizeable(Integer i, int max) {
            super(i);
            this.max = max;
        }
        public void set(Integer obj) {
            set(obj,false);
        }
        public void set(int i, boolean cyclic){
            super.set(cyclic ? cyclic(min,i,max + 1) : Math.max(min,Math.min(i,max)));
        }
        public int add(int value, boolean cyclic){
            int oldValue = get();
            set(get() + value,cyclic);
            return get() - oldValue;
        }
        public int add(int value){
            return add(value,false);
        }
        public int shrink(int value){
            return add(-value);
        }
    }
}
