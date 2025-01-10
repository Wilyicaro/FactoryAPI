package wily.factoryapi.util;

import org.apache.commons.lang3.ArrayUtils;
import wily.factoryapi.base.Stocker;


import java.util.AbstractList;
import java.util.List;
import java.util.function.Supplier;

public class PagedList<T> extends AbstractList<T> {
    public final Stocker.Sizeable page;
    private final Supplier<Integer> maxPageSize;
    public T[] objects = (T[]) new Object[0];

    public PagedList(Stocker.Sizeable page, int maxPageSize){
        this(page,()-> maxPageSize);
    }

    public PagedList(Stocker.Sizeable page, Supplier<Integer> maxPageSize){
        this.page = page;
        this.maxPageSize = maxPageSize;
    }

    public int allSize(){
        return objects.length;
    }

    @Override
    public int size() {
        return Math.min(maxPageSize.get(),allSize() - page.get() * maxPageSize.get());
    }

    @Override
    public void add(int index, T element) {
        objects = ArrayUtils.insert(index, objects, element);
    }

    @Override
    public boolean add(T t) {
        if (!isEmpty() && (allSize() % maxPageSize.get()) == 0 && page.max <= (allSize() / maxPageSize.get() - 1))
            page.max++;
        add(allSize(),t);
        return true;
    }
    @Override
    public T get(int index) {
        return objects[page.get() * maxPageSize.get() + index];
    }


    @Override
    public T remove(int index) {
        T r = objects[index];
        objects = ArrayUtils.remove(objects,index);
        if (objects.length % maxPageSize.get() == maxPageSize.get() - 1) page.max--;
        return r;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < objects.length; i++)
            if (objects[i] == o) return i % maxPageSize.get();
        return -1;
    }
    public static <T> int occurrenceOf(List<T> list, T object, int index){
        int o = 0;
        for (int i = 0; i < list.size(); i++) {
            if (object.equals(list.get(i))){
                if (i == index) return o;
                else o++;
            }
        }
        return -1;
    }
    public static <T> int indexOf(List<T> list, T object, int occurrence){
        int o = 0;
        for (int i = 0; i < list.size(); i++) {
            if (object == list.get(i)){
                if (o == occurrence) return i;
                else o++;
            }
        }
        return -1;
    }


}
