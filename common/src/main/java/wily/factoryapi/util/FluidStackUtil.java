package wily.factoryapi.util;

import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidStackUtil {

    public static FluidStack fromJson(JsonObject jsonObject){
        if (jsonObject != null){
            String string2 = GsonHelper.getAsString(jsonObject, "fluid", "minecraft:empty");
            long amount = getPlatformFluidAmount(GsonHelper.getAsLong(jsonObject, "amount", 1000));
            return FluidStack.create(BuiltInRegistries.FLUID.get(new ResourceLocation(string2)),amount);
        }
        return FluidStack.empty();
    }
    public static FluidStack fromTag(CompoundTag tag){
        if (tag.contains("FluidName") || tag.contains("fluidVariant") && Platform.isFabric()){
            Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(Platform.isFabric() ?tag.getCompound("fluidVariant").getString("fluid") : tag.getString("FluidName")));
            if (fluid == Fluids.EMPTY) return FluidStack.empty();
            return FluidStack.create(fluid,tag.getLong(Platform.isFabric() ? "amount": "Amount"));
        }
        return FluidStack.empty();
    }
    public static CompoundTag toTag(FluidStack stack){
        CompoundTag tag = new CompoundTag();
        if (stack != null && !stack.isEmpty()){
            tag.putLong(Platform.isFabric() ? "amount": "Amount", stack.getAmount());
            CompoundTag fluidTag = tag;
            if (Platform.isFabric()) tag.put("fluidVariant", fluidTag = new CompoundTag());
            fluidTag.putString(Platform.isFabric() ? "fluid": "FluidName",BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString());
        }
        return tag;
    }
    public static long getPlatformFluidAmount(long amount){
        return (long) (((float)amount / 1000) * FluidStack.bucketAmount());
    }
}
