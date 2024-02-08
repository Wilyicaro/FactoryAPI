package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SideList<T extends IModifiableTransportHandler> extends NonNullList<T> {


    public SideList(Supplier<T> defaultValue){
        super(new ArrayList<>(Arrays.stream(Direction.values()).map(d->defaultValue.get()).toList()), defaultValue.get());
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
        return direction == null ? defaultState : get(direction).getTransport();
    }
    public void setTransport(TransportState transportState, Direction direction) {
        get(direction).setTransport(transportState);
    }

    public boolean contains(Direction d) {
        return get(d) != null;
    }

    public void forEach(BiConsumer<Direction,? super T> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < this.size(); i++) {
            action.accept(Direction.values()[i],get(i));
        }
    }

}
