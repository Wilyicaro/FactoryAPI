package wily.factoryapi.mixin.base;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
//? if >=1.20.3 {
import com.mojang.authlib.yggdrasil.ProfileResult;
//?}
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.MinecraftAccessor;
import wily.factoryapi.base.client.UIAccessor;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftAccessor {
    @Shadow @Nullable public ClientLevel level;

    @Shadow @Final public Gui gui;

    @Mutable
    @Shadow @Final private User user;


    @Mutable
    @Shadow @Final private ProfileKeyPairManager profileKeyPairManager;

    @Mutable
    @Shadow @Final private UserApiService userApiService;

    //? if >=1.20.3 {
    @Mutable
    @Shadow @Final private CompletableFuture<ProfileResult> profileFuture;
    @Mutable
    @Shadow @Final private CompletableFuture<UserApiService.UserProperties> userPropertiesFuture;
    //?}

    @Shadow @Final private YggdrasilAuthenticationService authenticationService;

    @Mutable
    @Shadow @Final private ClientTelemetryManager telemetryManager;

    @Shadow @Final private static Logger LOGGER;

    @Shadow private ReportingContext reportingContext;

    @Mutable
    @Shadow @Final private RealmsDataFetcher realmsDataFetcher;

    @Shadow @Final private SplashManager splashManager;

    @Inject(method = "resizeDisplay",at = @At("RETURN"))
    public void resizeDisplay(CallbackInfo ci) {
        if (this.level != null) {
            UIAccessor.of(gui).reloadUI();
            FactoryAPIClient.RESIZE_DISPLAY.invoker.accept(Minecraft.getInstance());
        }
    }
    @Inject(method = "setScreen",at = @At("RETURN"))
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (this.level != null) {
            UIAccessor.of(gui).reloadUI();
        }
    }
    @Inject(method = "stop",at = @At("RETURN"))
    public void stop(CallbackInfo ci) {
        FactoryAPIClient.STOPPING.invoker.accept(Minecraft.getInstance());
    }

    //? if <1.20.5 {
    /*@Accessor
    public abstract float getPausePartialTick();
    *///?}

    //? if <=1.20.1 {
    /*@Unique boolean gameLoaded = false;
    @Inject(method = "onGameLoadFinished",at = @At("RETURN"))
    public void onGameLoadFinished(CallbackInfo ci) {
      gameLoaded = true;
    }
    *///?}

    @Override
    public boolean hasGameLoaded() {
        return /*? if <=1.20.1 {*//*gameLoaded*//*?} else {*/Minecraft.getInstance().isGameLoadFinished()/*?}*/;
    }

    //? if <1.21.2 {
    /*@Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
    public void beforeScreenTick(CallbackInfo ci) {
        if (Minecraft.getInstance().screen == null) return;
        UIAccessor accessor = UIAccessor.of(Minecraft.getInstance().screen);
        Screen.wrapScreenError(accessor::beforeTick, "Ticking screen before tick", Minecraft.getInstance().screen.getClass().getCanonicalName());
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V", shift = At.Shift.AFTER))
    public void afterScreenTick(CallbackInfo ci) {
        if (Minecraft.getInstance().screen == null) return;
        UIAccessor accessor = UIAccessor.of(Minecraft.getInstance().screen);
        Screen.wrapScreenError(accessor::afterTick, "Ticking screen after tick", Minecraft.getInstance().screen.getClass().getCanonicalName());
    }
    *///?} else {
    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;tick()V"))
    public void beforeScreenTick(CallbackInfo ci) {
        if (Minecraft.getInstance().screen != null) UIAccessor.of(Minecraft.getInstance().screen).beforeTick();
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;tick()V", shift = At.Shift.AFTER))
    public void afterScreenTick(CallbackInfo ci) {
        if (Minecraft.getInstance().screen != null) UIAccessor.of(Minecraft.getInstance().screen).afterTick();
    }
    //?}

    @Override
    public boolean setUser(User user) {
        if (user == null) {
            LOGGER.warn("Something went wrong, the User cannot be set to null");
            return false;
        }
        this.user = splashManager.user = user;
        //? if >=1.20.3 {
        this.profileFuture = CompletableFuture.supplyAsync(() -> Minecraft.getInstance().getMinecraftSessionService().fetchProfile(user.getProfileId(), true), Util.nonCriticalIoPool());
        this.userApiService = user.getType() != User.Type.MSA ? UserApiService.OFFLINE : this.authenticationService.createUserApiService(user.getAccessToken());
        this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return userApiService.fetchProperties();
            } catch (AuthenticationException var2) {
                LOGGER.error("Failed to fetch user properties", var2);
                return UserApiService.OFFLINE_PROPERTIES;
            }
        }, Util.nonCriticalIoPool());
        //?} else {
        /*try {
            this.userApiService = authenticationService.createUserApiService(user.getAccessToken());
        } catch (AuthenticationException var4) {
            AuthenticationException authenticationException = var4;
            LOGGER.error("Failed to verify authentication", authenticationException);
            this.userApiService = UserApiService.OFFLINE;
        }
        *///?}
        this.profileKeyPairManager = ProfileKeyPairManager.create(userApiService, user, Minecraft.getInstance().gameDirectory.toPath());
        this.telemetryManager = new ClientTelemetryManager(Minecraft.getInstance(), userApiService, user);
        this.reportingContext = ReportingContext.create(ReportEnvironment.local(), userApiService);
        this.realmsDataFetcher = new RealmsDataFetcher(/*? if >1.21.4 {*/RealmsClient.getOrCreate()/*?} else {*//*RealmsClient.create(Minecraft.getInstance())*//*?}*/);
        //? if >=1.20.3
        RealmsAvailabilityAccessor.setFuture(null);
        return true;
    }
}
