package wily.factoryapi.mixin.base;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.WorldDataConfiguration;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.DatapackRepositoryAccessor;
import wily.factoryapi.base.client.UIAccessor;

import java.nio.file.Path;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen implements DatapackRepositoryAccessor {
    protected CreateWorldScreenMixin(Component arg) {
        super(arg);
    }

    @Shadow @Nullable
    protected abstract Pair<Path, PackRepository> getDataPackSelectionSettings(WorldDataConfiguration arg);

    @Shadow @Final private WorldCreationUiState uiState;

    @Shadow protected abstract void tryApplyNewDataPacks(PackRepository arg, boolean bl, Consumer<WorldDataConfiguration> consumer);


    @Shadow @Nullable private TabNavigationBar tabNavigationBar;

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        if (tabNavigationBar == null) return;
        UIAccessor.of(this).getElements().put("currentTabIndex", ((TabNavigationBarAccessor)tabNavigationBar)::getCurrentTabIndex);
    }

    @Override
    public PackRepository getDatapackRepository() {
        return getDataPackSelectionSettings(uiState.getSettings().dataConfiguration()).getSecond();
    }

    @Override
    public void tryApplyNewDataPacks(PackRepository repository) {
        tryApplyNewDataPacks(repository, false, data -> {
            if (this instanceof UIAccessor accessor) Minecraft.getInstance().setScreen(accessor.getScreen());
        });
    }
}
