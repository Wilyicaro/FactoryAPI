package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Locale;

public enum BlockSide {
    FRONT,BACK,TOP,BOTTOM,RIGHT,LEFT;

    public Component getComponent(){
        return Component.translatable("tooltip.factory_api.gui_" + name().toLowerCase(Locale.ENGLISH));
    }
    public static final BlockSide[] FRONT_FACE_SIDES = values();
    public static final BlockSide[] TOP_FACE_SIDES = new BlockSide[]{TOP,BOTTOM,FRONT,BACK,RIGHT,LEFT};


    public Direction blockStateToFacing(BlockState blockState, BlockSide[] sides){
        if (blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).isPresent()) return convertToHorizontalFacing(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING),sides);
        else if (blockState.getOptionalValue(BlockStateProperties.FACING).isPresent()) return convertToFacing(blockState.getValue(BlockStateProperties.FACING),sides);
        return null;
    }
    public Direction blockStateToFacing(BlockState blockState){
        return blockStateToFacing(blockState,values());
    }
    public Direction convertToHorizontalFacing(Direction blockStateDirection, BlockSide[] sides){
        if (this.equals(sides[0])) {
            return blockStateDirection;
        } else if (this.equals(sides[1])) {
            return blockStateDirection.getOpposite();
        } else if (this.equals(sides[2])) {
            return Direction.UP;
        } else if (this.equals(sides[3])) {
            return Direction.DOWN;
        } else if (this.equals(sides[4])) {
            return blockStateDirection.getCounterClockWise();
        } else if (this.equals(sides[5])) {
            return blockStateDirection.getClockWise();
        }
        return blockStateDirection;
    }
    public Direction convertToHorizontalFacing(Direction blockStateDirection){
        return convertToHorizontalFacing(blockStateDirection,values());
    }
    public Direction convertToFacing(Direction d, BlockSide[] sides){
        if (this == sides[0])
            return d;
        if (this == sides[1])
            return d.getOpposite();
        if (Direction.Plane.HORIZONTAL.test(d)) {
            if (this == sides[2])
                return Direction.UP;
            if (this == sides[3])
                return Direction.DOWN;
            if (this == sides[4])
                return d.getCounterClockWise();
            if (this == sides[5])
                return d.getClockWise();
        }else {
            if (this == sides[2])
                return d == Direction.DOWN ? Direction.NORTH : Direction.SOUTH;
            if (this == sides[3])
                return d == Direction.DOWN ? Direction.SOUTH : Direction.NORTH;
            if (this == sides[4])
                return  d == Direction.DOWN ? Direction.EAST : Direction.WEST;
            if (this == sides[5])
                return d == Direction.DOWN ? Direction.WEST : Direction.EAST;
        }
        return d;
    }
    public Direction convertToFacing(Direction blockStateDirection){
        return convertToFacing(blockStateDirection,values());
    }

}
