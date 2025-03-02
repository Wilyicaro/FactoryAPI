package wily.factoryapi.base;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import wily.factoryapi.base.network.CommonNetwork;

import java.util.Optional;
import java.util.function.Consumer;

public interface FactoryExtraMenuSupplier<T extends AbstractContainerMenu> extends MenuType.MenuSupplier<T> {
    default T create(int i, Inventory inventory){
        return create(i, inventory, null);
    }

    T create(int i, Inventory inventory, CommonNetwork.PlayBuf buf);

    static <T extends AbstractContainerMenu> MenuType<T> createMenuType(FactoryExtraMenuSupplier<T> factoryExtraMenuSupplier){
        return new MenuType<>(factoryExtraMenuSupplier, FeatureFlags.VANILLA_SET);
    }

    interface PrepareMenu {
        Optional<AbstractContainerMenu> prepareMenu(MenuProvider provider, Consumer<AbstractContainerMenu> openClientMenu);
    }
}
