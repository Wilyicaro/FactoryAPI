package wily.factoryapi.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import wily.factoryapi.FactoryAPI;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class SimpleMixinPlugin implements IMixinConfigPlugin {


    private final BooleanSupplier shouldApplyMixin;

    public SimpleMixinPlugin(BooleanSupplier shouldApplyMixin){
        this.shouldApplyMixin = shouldApplyMixin;
    }

    public static class NeoForge extends SimpleMixinPlugin{
        public NeoForge() {
            super(()-> FactoryAPI.getLoader() == FactoryAPI.Loader.NEOFORGE);
        }
    }
    public static class ForgeLike extends SimpleMixinPlugin{
        public ForgeLike() {
            super(()-> FactoryAPI.getLoader().isForgeLike());
        }
    }
    public static class Fabric extends SimpleMixinPlugin{
        public Fabric() {
            super(()-> FactoryAPI.getLoader() == FactoryAPI.Loader.FABRIC);
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return shouldApplyMixin.getAsBoolean();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
