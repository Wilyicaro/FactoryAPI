//? if >=1.20.3 {
package wily.factoryapi.mixin.base;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.realmsclient.RealmsAvailability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@Mixin(RealmsAvailability.class)
public interface RealmsAvailabilityAccessor {
    @Accessor
    static void setFuture(CompletableFuture<UserApiService.UserProperties> userProperties) {

    }
}
//?}
