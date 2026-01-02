package wily.factoryapi.mixin.base;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import wily.factoryapi.base.FactoryExtraMenuSupplier;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements FactoryExtraMenuSupplier.PrepareMenu {

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, /*? if <1.21.6 {*/ blockPos, f,/*?}*/ gameProfile);
    }

    @Shadow protected abstract void nextContainerCounter();

    @Shadow public abstract void closeContainer();

    @Shadow protected abstract void initMenu(AbstractContainerMenu abstractContainerMenu);

    @Shadow private int containerCounter;

    @Override
    public Optional<AbstractContainerMenu> prepareMenu(MenuProvider provider, Consumer<AbstractContainerMenu> openClientMenu) {
        if (provider == null) {
            return Optional.empty();
        } else {
            if (this.containerMenu != this.inventoryMenu) {
                this.closeContainer();
            }

            this.nextContainerCounter();
            AbstractContainerMenu abstractContainerMenu = provider.createMenu(this.containerCounter, this.getInventory(), this);
            if (abstractContainerMenu == null) {
                if (this.isSpectator()) {
                    this.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
                }

                return Optional.empty();
            } else {
                openClientMenu.accept(abstractContainerMenu);
                this.initMenu(abstractContainerMenu);
                this.containerMenu = abstractContainerMenu;
                return Optional.of(containerMenu);
            }
        }
    }
}
