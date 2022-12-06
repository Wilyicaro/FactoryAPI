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
        switch (this){
            case TOP:
                return Direction.UP;
            case BOTTOM:
                return Direction.DOWN;
            case RIGHT:
                return BlockStateDirection.getCounterClockWise();
            case LEFT:
                return BlockStateDirection.getClockWise();
            case FRONT:
                return BlockStateDirection;
            case BACK:
                return BlockStateDirection.getOpposite();
        }
        return BlockStateDirection;
    }
    public Direction convertToFacing(Direction d){
        if (this == FRONT)
                return d;
        if (this == BACK)
                return d.getOpposite();
        if (Direction.Plane.HORIZONTAL.test(d)) {
            if (this == TOP)
                return Direction.UP;
            if (this == BOTTOM)
                return Direction.DOWN;
            if (this == RIGHT)
                return d.getCounterClockWise();
            if (this == LEFT)
                return d.getClockWise();
        }else {
            if (this == TOP || this == BOTTOM)
                return d.getClockWise(Direction.Axis.X);
            if (this == RIGHT)
                return Direction.WEST;
            if (this == LEFT)
                return Direction.EAST;
        }
    return d;
    }

}
