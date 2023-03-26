package wily.factoryapi.base;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public interface SimpleFluidLoggedBlock extends BucketPickup, LiquidBlockContainer {
    List<Fluid> BLOCK_LOGGABLE_FLUIDS = new ArrayList<>(List.of(Fluids.EMPTY,Fluids.WATER,Fluids.LAVA));

    default IntegerProperty FLUIDLOGGED() {
        return IntegerProperty.create("fluidlogged", 0, BLOCK_LOGGABLE_FLUIDS.size() -1);
    }


    default boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
            return BLOCK_LOGGABLE_FLUIDS.get(blockState.getValue(FLUIDLOGGED())).isSame(Fluids.EMPTY) && BLOCK_LOGGABLE_FLUIDS.contains(fluid);
    }

    default boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (BLOCK_LOGGABLE_FLUIDS.get(blockState.getValue(FLUIDLOGGED())).isSame(Fluids.EMPTY) && BLOCK_LOGGABLE_FLUIDS.contains(fluidState.getType())) {
            if (!levelAccessor.isClientSide()) {
                levelAccessor.setBlock(blockPos, blockState.setValue(FLUIDLOGGED(), BLOCK_LOGGABLE_FLUIDS.indexOf(fluidState.getType())), 3);
                levelAccessor.scheduleTick(blockPos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
            }

            return true;
        } else {
            return false;
        }
    }

    default ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if ( blockState.getValue(FLUIDLOGGED()) > 0) {
            Fluid f = BLOCK_LOGGABLE_FLUIDS.get(blockState.getValue(FLUIDLOGGED()));
            levelAccessor.setBlock(blockPos, blockState.setValue(FLUIDLOGGED(), 0), 3);
            if (!blockState.canSurvive(levelAccessor, blockPos)) {
                levelAccessor.destroyBlock(blockPos, true);
            }
            return new ItemStack(f.getBucket());
        } else {
            return ItemStack.EMPTY;
        }
    }

    default Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}
