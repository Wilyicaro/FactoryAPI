package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public enum BlockSide {
    TOP,BOTTOM,RIGHT,LEFT,FRONT,BACK;

    public Direction blockStateToFacing(BlockState blockState){
        if (blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).isPresent()) return convertToHorizontalFacing(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING));
        else if (blockState.getOptionalValue(BlockStateProperties.FACING).isPresent()) return convertToFacing(blockState.getValue(BlockStateProperties.FACING));
        return null;
    }
    public Direction convertToHorizontalFacing(Direction BlockStateDirection){
        return switch (this) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case RIGHT -> BlockStateDirection.getCounterClockWise();
            case LEFT -> BlockStateDirection.getClockWise();
            case FRONT -> BlockStateDirection;
            case BACK -> BlockStateDirection.getOpposite();
        };
    }
    public Direction convertToFacing(Direction pointingDirection){
        if (this == FRONT)
                return pointingDirection;
        if (this == BACK)
                return pointingDirection.getOpposite();
        if (Direction.Plane.HORIZONTAL.test(pointingDirection)) {
            if (this == TOP)
                return Direction.UP;
            if (this == BOTTOM)
                return Direction.DOWN;
            if (this == RIGHT)
                return pointingDirection.getCounterClockWise();
            if (this == LEFT)
                return pointingDirection.getClockWise();
        }else {
            if (this == TOP)
                return pointingDirection.getClockWise(Direction.Axis.X);
            if (this == BOTTOM)
                return pointingDirection.getCounterClockWise(Direction.Axis.X);
            if (this == RIGHT)
                return pointingDirection.getCounterClockWise(Direction.Axis.Z);
            if (this == LEFT)
                return pointingDirection.getClockWise(Direction.Axis.Z);
        }
    return pointingDirection;
    }
    public Direction convertTopFacing(Direction pointingDirection){
        if (this == TOP)
            return pointingDirection;
        if (this == BOTTOM)
            return pointingDirection.getOpposite();
        if (Direction.Plane.HORIZONTAL.test(pointingDirection)) {
            if (this == FRONT)
                return Direction.UP;
            if (this == BACK)
                return Direction.DOWN;
            if (this == RIGHT)
                return pointingDirection.getCounterClockWise();
            if (this == LEFT)
                return pointingDirection.getClockWise();
        }else {
            if (this == FRONT)
                return pointingDirection.getClockWise(Direction.Axis.X);
            if (this == BACK)
                return pointingDirection.getCounterClockWise(Direction.Axis.X);
            if (this == RIGHT)
                return pointingDirection.getCounterClockWise(Direction.Axis.Z);
            if (this == LEFT)
                return pointingDirection.getClockWise(Direction.Axis.Z);
        }
        return pointingDirection;
    }

}
