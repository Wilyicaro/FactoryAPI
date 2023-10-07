package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SideList<T> extends NonNullList<T> {
    private final TransportStateHandler<T> transportStateHandler;


    public SideList(TransportStateHandler<T> transportStateHandler, Supplier<T> defaultValue){
        super(new ArrayList<>(Arrays.stream(Direction.values()).map(d->defaultValue.get()).collect(Collectors.toList())), defaultValue.get());
        this.transportStateHandler = transportStateHandler;
    }
    public SideList(Supplier<T> defaultValue){
        this((sideList, direction) -> (TransportState) sideList.get(direction), defaultValue);
    }
    public static<T  extends ISideType<?,?>> SideList<T> createSideTypeList(Supplier<T> defaultValue){
        return new SideList<>(new TransportStateHandler<T>() {
            public TransportState get(SideList<T> sideList, Direction direction) {
                return sideList.get(direction).getTransport();
            }

            public void set(TransportState transportState, SideList<T> sideList, Direction direction) {
                sideList.get(direction).withTransport(transportState);
            }
        }, defaultValue);
    }
    public T get(Direction direction) {
        return direction == null ? null : super.get(direction.ordinal());
    }
    public void put(Direction direction, T side) {
        set(direction.ordinal(),side);
    }
    public TransportState getTransport(Direction direction) {
        return getTransportOrDefault(direction,TransportState.NONE);
    }
    public TransportState getTransportOrDefault(@Nullable Direction direction, TransportState defaultState) {
        return direction == null ? defaultState : transportStateHandler.get(this,direction);
    }
    public void setTransport(TransportState transportState, Direction direction) {
        transportStateHandler.set(transportState,this,direction);
    }

    public void forEach(BiConsumer<Direction,? super T> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < this.size(); i++) {
            action.accept(Direction.values()[i],get(i));
        }
    }

    public interface TransportStateHandler<T>{
        TransportState get(SideList<T> sideList, Direction direction);
        default void set(TransportState transportState, SideList<T> sideList, Direction direction){
            ((SideList<TransportState>) sideList).put(direction,transportState);
        }
    }

}
