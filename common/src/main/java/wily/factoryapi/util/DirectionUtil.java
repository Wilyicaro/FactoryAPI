package wily.factoryapi.util;


import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Quaternionf;

public class DirectionUtil {
    public static Direction nearestRotation(float rotateX, float rotateY, boolean invertYAxis){
        Entity entity = new ItemEntity(EntityType.ITEM,null);
        entity.setXRot(rotateX);
        entity.setYRot(rotateY);
        Direction d =Direction.orderedByNearest(entity)[0];
        return Direction.Plane.VERTICAL.test(d) && invertYAxis ? d.getOpposite() : d;
    }

    public static float rotationCyclic(float rotation){
        if (rotation> 180) return rotation - 360;
        else if ( rotation < 180) return 360 + rotation;
        return rotation;
    }
    public static float unCyclicRotation(float rotation){
        return rotation < 0 ? 360 + rotation : rotation;
    }

    public static double rotateByCenter(Direction.Axis axis,double angle, double center, double centerXDistance,  double centerZDistance){
        switch (axis){
            default -> {return center;}
            case X -> {return center + centerXDistance * Math.cos(angle) - centerZDistance * Math.sin(angle);}
            case Z -> {return center + centerXDistance * Math.sin(angle) + centerZDistance * Math.cos(angle);}
        }
    }
    public static double rotateZByCenter(double angle, double center, double centerXDistance,  double centerZDistance){
        return rotateByCenter(Direction.Axis.Z, angle,center,centerXDistance, centerZDistance);
    }
    public static double rotateXByCenter(double angle, double center, double centerXDistance,  double centerZDistance){
        return rotateByCenter(Direction.Axis.X, angle,center,centerXDistance, centerZDistance);
    }

    public static Quaternionf getRotation(Direction direction) {
        return switch (direction) {
            case DOWN ->  Axis.XP.rotationDegrees(180.0F);
            case UP -> new Quaternionf();
            case NORTH -> Axis.XP.rotationDegrees(-90.0F);
            case SOUTH -> Axis.XP.rotationDegrees(90.0F);
            case WEST -> Axis.ZP.rotationDegrees(90.0F);
            case EAST -> Axis.ZP.rotationDegrees(-90.0F);
        };
    }
    public static Quaternionf getNorthRotation(Direction direction) {
        return switch (direction) {
            case DOWN ->  Axis.XP.rotationDegrees(-90.0F);
            case UP -> Axis.XP.rotationDegrees(90.0F);
            case NORTH -> new Quaternionf();
            case SOUTH -> Axis.YP.rotationDegrees(180.0F);
            case WEST -> Axis.YP.rotationDegrees(90.0F);
            case EAST -> Axis.YP.rotationDegrees(-90.0F);
        };
    }
    public static Quaternionf getRotationByInitial(Direction initial, Direction direction) {
        Direction.Axis axis1 = initial.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        Direction.Axis axis2 = initial.getAxis() == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
        if (direction == initial.getOpposite()) {return Axis.XP.rotationDegrees(180.0F);}
        else if(direction == initial) return new Quaternionf();
        else if (direction == initial.getClockWise(Direction.Axis.X)) return Axis.XP.rotationDegrees(-90.0F);
        else if (direction == initial.getCounterClockWise(Direction.Axis.X)) return Axis.XP.rotationDegrees(90.0F);
        if (direction == initial.getCounterClockWise(Direction.Axis.Z)) return Axis.ZP.rotationDegrees(90.0F);
        if (direction == initial.getClockWise(Direction.Axis.Z)) return Axis.ZP.rotationDegrees(-90.0F);
        return new Quaternionf();
    }
    public static Quaternionf getHorizontalRotation(Direction direction) {
        return switch (direction) {
            case NORTH -> new Quaternionf();
            case SOUTH -> Axis.YP.rotationDegrees(180.0F);
            case WEST -> Axis.YP.rotationDegrees(90.0F);
            case EAST -> Axis.YP.rotationDegrees(-90.0F);
            default -> throw new IncompatibleClassChangeError();
        };
    }
}
