package wily.factoryapi.base;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


public interface SimpleFluidLoggedBlock extends BucketPickup, LiquidBlockContainer {
    List<Supplier<Fluid>> BLOCK_LOGGABLE_FLUIDS_SUPPLIER = new ArrayList<>(List.of(()->Fluids.EMPTY,()->Fluids.WATER,()->Fluids.LAVA));
    Function<Integer,IntegerProperty> DEFAULT_FLUIDLOGGED_PROPERTY = Util.memoize(i-> IntegerProperty.create("fluidlogged", 0, i));

    default IntegerProperty FLUIDLOGGED() {
        return DEFAULT_FLUIDLOGGED_PROPERTY.apply(BLOCK_LOGGABLE_FLUIDS_SUPPLIER.size() -1);
    }
    default List<Fluid> getBlockLoggableFluids(){
        return BLOCK_LOGGABLE_FLUIDS_SUPPLIER.stream().map(Supplier::get).toList();
    }

    default boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        List<Fluid> fluids = getBlockLoggableFluids();
        return BLOCK_LOGGABLE_FLUIDS_SUPPLIER.get(blockState.getValue(FLUIDLOGGED())).get().isSame(Fluids.EMPTY) && fluids.contains(fluid);
    }

    default boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        List<Fluid> fluids = getBlockLoggableFluids();
        if (BLOCK_LOGGABLE_FLUIDS_SUPPLIER.get(blockState.getValue(FLUIDLOGGED())).get().isSame(Fluids.EMPTY) && fluids.contains(fluidState.getType())) {
            if (!levelAccessor.isClientSide()) {
                levelAccessor.setBlock(blockPos, blockState.setValue(FLUIDLOGGED(), fluids.indexOf(fluidState.getType())), 3);
                levelAccessor.scheduleTick(blockPos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
            }

            return true;
        } else {
            return false;
        }
    }
    default FluidState getSimpleFluidState(BlockState state) {
        FluidState f = BLOCK_LOGGABLE_FLUIDS_SUPPLIER.get( state.getValue(FLUIDLOGGED())).get().defaultFluidState();
        return f.getType() instanceof FlowingFluid g ? g.getSource(false) : f;
    }

    default BlockState getStateForPlacement(BlockState state,BlockPlaceContext ctx) {
        int f = getBlockLoggableFluids().indexOf(ctx.getLevel().getFluidState(ctx.getClickedPos()).getType());
        return state.setValue(FLUIDLOGGED(),f == -1 ? 0 : f);
    }
    default ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if ( blockState.getValue(FLUIDLOGGED()) > 0) {
            Fluid f = BLOCK_LOGGABLE_FLUIDS_SUPPLIER.get(blockState.getValue(FLUIDLOGGED())).get();
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
