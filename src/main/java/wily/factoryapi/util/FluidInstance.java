package wily.factoryapi.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if >=1.20.5 {
/*import net.minecraft.core.Holder;
import net.minecraft.core.component.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
*///?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;

//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
//?} elif forge {
/*import net.minecraftforge.fluids.FluidStack;
*///?} elif neoforge {
/*import net.neoforged.neoforge.fluids.FluidStack;
*///?}

public class FluidInstance /*? if >=1.20.5 && !forge {*//*implements DataComponentHolder*//*?}*/ {
    private final Fluid fluid;
    private int amount;
    //? if <1.20.5 || forge {
    private CompoundTag tag;
    //?} else
    /*private PatchedDataComponentMap components = new PatchedDataComponentMap(PatchedDataComponentMap.EMPTY);*/


    public static final Codec<FluidInstance> CODEC = RecordCodecBuilder.create(i-> i.group(ResourceLocation.CODEC.xmap(r->FactoryAPIPlatform.getRegistryValue(r,BuiltInRegistries.FLUID), BuiltInRegistries.FLUID::getKey).fieldOf("fluid").forGetter(FluidInstance::getFluid), Codec.INT.fieldOf("amount").forGetter(FluidInstance::getAmount), /*? if <1.20.5 || forge {*/ CompoundTag/*?} else {*/ /*DataComponentPatch*//*?}*/.CODEC.fieldOf(/*? if <1.20.5 {*/ "nbt"/*?} else {*/ /*"components"*//*?}*/).forGetter(FluidInstance::/*? if <1.20.5 || forge {*/ getNonNullTag/*?} else {*//*getComponentsPatch*//*?}*/)).apply(i,FluidInstance::new));
    //? if >=1.20.5
    /*public static final StreamCodec<RegistryFriendlyByteBuf,FluidInstance> STREAM_CODEC = StreamCodec.of(FluidInstance::encode, FluidInstance::decode);*/
    public static final FluidInstance EMPTY = new FluidInstance(Fluids.EMPTY,0);


    public FluidInstance(Fluid fluid, int amount){
        this.fluid = fluid;
        this.amount = amount;
    }
    public FluidInstance(Fluid fluid, int amount, /*? if <1.20.5 || forge {*/ CompoundTag tag/*?} else {*/ /*DataComponentPatch components*//*?}*/){
        this(fluid,amount);
        //? if <1.20.5 || forge {
        this.tag = tag.copy();
        //?} else
        /*this.components = PatchedDataComponentMap.fromPatch(DataComponentMap.builder().build(),components);*/
    }

    public static FluidInstance empty() {
        return EMPTY;
    }
    public static FluidInstance create(FluidInstance fluidInstance, int amount){
        return create(fluidInstance.getFluid(),amount);
    }
    public static FluidInstance create(Fluid fluid, int amount){
        return new FluidInstance(fluid,amount);
    }
    public static FluidInstance create(Fluid fluid){
        return new FluidInstance(fluid,1000);
    }
    public static FluidInstance create(Fluid fluid, long amount){
        return new FluidInstance(fluid,getMilliBucketsFluidAmount(amount));
    }
    //? if <1.20.5 || forge {
    public CompoundTag getNonNullTag() {
        return getTag() == null ? new CompoundTag() : getTag();
    }
    //?}
    //? if <1.20.5 || forge {
    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(CompoundTag tag) {
        if (getFluid() != Fluids.EMPTY) this.tag = tag;
    }

    public CompoundTag getOrCreateTag() {
        if (tag == null)
            setTag(new CompoundTag());
        return tag;
    }
    //?} else {
    /*@Override
    public DataComponentMap getComponents() {
        return components;
    }
    public DataComponentPatch getComponentsPatch() {
        return !this.isEmpty() ? components.asPatch() : DataComponentPatch.EMPTY;
    }

    public <T> T set(DataComponentType<? super T> type, @Nullable T component) {
        return this.components.set(type, component);
    }

    public <T> T remove(DataComponentType<? extends T> type) {
        return this.components.remove(type);
    }

    public void applyComponents(DataComponentPatch patch) {
        this.components.applyPatch(patch);
    }

    public void applyComponents(DataComponentMap components) {
        this.components.setAll(components);
    }

    *///?}

    public Fluid getFluid(){
        return fluid;
    }

    public boolean isEmpty(){
        return getAmount() <= 0;
    }

    public int getAmount(){
        return getFluid() == Fluids.EMPTY ? 0 : amount;
    }

    public void setAmount(int amount){
        if (getFluid() != Fluids.EMPTY) this.amount = amount;
    }

    //? if fabric {
    public FluidVariant toVariant(){
        return FluidVariant.of(fluid,/*? if <1.20.5 {*/ tag/*?} else {*/ /*getComponentsPatch()*//*?}*/);
    }
    //?} else if neoforge || forge {
    /*private FluidStack stack;
    public FluidStack toStack(){
        if (this.isEmpty()) return FluidStack.EMPTY;
        if (stack == null) stack = new FluidStack(fluid,amount);
        stack.setAmount(amount);
        //? if <1.20.5 || forge {
        stack.setTag(getTag());
        //?} else
        /^stack.applyComponents(getComponents());^/
        return stack;
    }
    *///?}
    public void setAmount(long amount){
        setAmount(getMilliBucketsFluidAmount(amount));
    }

    public static FluidInstance fromJson(JsonObject jsonObject){
        if (jsonObject != null) return CODEC.parse(JsonOps.INSTANCE,jsonObject).result().orElse(empty());
        return empty();
    }
    public static FluidInstance fromTag(CompoundTag tag){
        return CODEC.parse(NbtOps.INSTANCE,tag).result().orElseGet(()->{
            if (tag.contains("FluidName") || tag.contains("fluidVariant") && FactoryAPI.getLoader().isFabric()){
                Fluid fluid = FactoryAPIPlatform.getRegistryValue(FactoryAPI.createLocation(FactoryAPI.getLoader().isFabric() ? tag.getCompound("fluidVariant").getString("fluid") : tag.getString("FluidName")),BuiltInRegistries.FLUID);
                if (fluid == Fluids.EMPTY) return FluidInstance.empty();
                return FluidInstance.create(fluid,getMilliBucketsFluidAmount(tag.getLong(FactoryAPI.getLoader().isFabric() ? "amount": "Amount")));
            }
            return empty();
        });
    }

    public static CompoundTag toTag(FluidInstance stack){
        return CODEC.encodeStart(NbtOps.INSTANCE,stack).result().map(t-> t instanceof CompoundTag c ? c : null).orElse(new CompoundTag());
    }
    public static long getPlatformBucketFluidAmount(){
        return FactoryAPI.getLoader().isFabric() ? 81000L : 1000;
    }
    public static long getPlatformFluidAmount(long amount){
        return (long) (((float)amount / 1000) * getPlatformBucketFluidAmount());
    }
    public static int getMilliBucketsFluidAmount(long amount){
        return (int) (((float)amount / getPlatformBucketFluidAmount()) * 1000);
    }

    public boolean isFluidEqual(FluidInstance resource) {
        return getFluid() == resource.getFluid();
    }

    public void grow(int amount) {
        setAmount(getAmount()+amount);
    }

    public void shrink(int amount) {
        setAmount(getAmount()-amount);
    }

    public FluidInstance withAmount(int amount) {
        setAmount(amount);
        return this;
    }

    public FluidInstance copy() {
        return new FluidInstance(getFluid(),getAmount());
    }

    public FluidInstance copyWithAmount(int maxDrain) {
        return copy().withAmount(maxDrain);
    }

    public Component getName() {
        return getFluid().defaultFluidState().createLegacyBlock().getBlock().getName();
    }

    public static void encode( /*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*//*RegistryFriendlyByteBuf *//*?}*/ buf, FluidInstance instance){
        buf.writeInt(instance.getAmount());
        if (instance.isEmpty()) return;
        buf.writeResourceLocation(BuiltInRegistries.FLUID.getKey(instance.getFluid()));
        //? if <1.20.5 || forge {
        buf.writeNbt(instance.getNonNullTag());
        //?} else
        /*DataComponentPatch.STREAM_CODEC.encode(buf,instance.getComponentsPatch());*/
    }
    public static FluidInstance decode(/*? if <1.20.5 {*/FriendlyByteBuf/*?} else {*//*RegistryFriendlyByteBuf *//*?}*/ buf){
        int amount = buf.readInt();
        if (amount <= 0) return EMPTY;
        return new FluidInstance(FactoryAPIPlatform.getRegistryValue(buf.readResourceLocation(),BuiltInRegistries.FLUID), amount, /*? if <1.20.5 || forge {*/buf.readNbt()/*?} else {*//*DataComponentPatch.STREAM_CODEC.decode(buf)*//*?}*/);
    }
}
