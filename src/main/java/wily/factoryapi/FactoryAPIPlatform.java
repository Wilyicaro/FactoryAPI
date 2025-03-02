package wily.factoryapi;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.brigadier.arguments.ArgumentType;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.NotNull;
import net.minecraft.resources.ResourceLocation;
//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*///?} elif fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.impl.SimpleItemEnergyStorageImpl;
import wily.factoryapi.base.fabric.CraftyEnergyStorage;
import wily.factoryapi.base.fabric.FabricEnergyStoragePlatform;
import wily.factoryapi.base.fabric.FabricFluidStoragePlatform;
import wily.factoryapi.base.fabric.FabricItemStoragePlatform;
//? if >=1.20.4 {
import net.fabricmc.fabric.impl.resource.loader.FabricResourcePackProfile;
//?}
//?} elif neoforge {
/*import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.bus.api.IEventBus;
*///?}
//? if forge || neoforge {
/*import wily.factoryapi.base.forge.FactoryCapabilities;
import wily.factoryapi.base.forge.ForgeEnergyHandlerPlatform;
import wily.factoryapi.base.forge.ForgeFluidHandlerPlatform;
import wily.factoryapi.base.forge.ForgeItemStoragePlatform;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.*;
import wily.factoryapi.base.network.CommonNetwork;
import wily.factoryapi.util.FluidInstance;
import wily.factoryapi.util.ListMap;
import wily.factoryapi.util.ModInfo;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface FactoryAPIPlatform {
    Map<IPlatformHandler, SideList<? super IModifiableTransportHandler>> filteredHandlersCache = new ConcurrentHashMap<>();
    Map<BlockEntity, IFactoryStorage> platformStorageWrappersCache = new ConcurrentHashMap<>();

    //? if forge {
    /*ListMap<Capability<?>, FactoryStorage<?>> ITEM_CAPABILITY_MAP = new ListMap.Builder<Capability<?>, FactoryStorage<?>>().put(ForgeCapabilities.FLUID_HANDLER_ITEM, FactoryStorage.FLUID).put(ForgeCapabilities.ITEM_HANDLER,FactoryStorage.ITEM).put(ForgeCapabilities.ENERGY,FactoryStorage.ENERGY).put(FactoryCapabilities.CRAFTY_ENERGY,FactoryStorage.CRAFTY_ENERGY).build();
    ListMap<Capability<?>, FactoryStorage<?>> BLOCK_CAPABILITY_MAP = new ListMap.Builder<Capability<?>, FactoryStorage<?>>().put(ForgeCapabilities.FLUID_HANDLER, FactoryStorage.FLUID).put(ForgeCapabilities.ITEM_HANDLER,FactoryStorage.ITEM).put(ForgeCapabilities.ENERGY,FactoryStorage.ENERGY).put(FactoryCapabilities.CRAFTY_ENERGY,FactoryStorage.CRAFTY_ENERGY).build();
    *///?} else if neoforge {
    /*ListMap<ItemCapability<?,?>, FactoryStorage<?>> ITEM_CAPABILITY_MAP = new ListMap.Builder<ItemCapability<?,?>, FactoryStorage<?>>().put(Capabilities.FluidHandler.ITEM, FactoryStorage.FLUID).put(Capabilities.ItemHandler.ITEM,FactoryStorage.ITEM).put(Capabilities.EnergyStorage.ITEM,FactoryStorage.ENERGY).put(FactoryCapabilities.CRAFTY_ENERGY_ITEM,FactoryStorage.CRAFTY_ENERGY).build();
    ListMap<BlockCapability<?,Direction>, FactoryStorage<?>> BLOCK_CAPABILITY_MAP = new ListMap.Builder<BlockCapability<?,Direction>, FactoryStorage<?>>().put(Capabilities.FluidHandler.BLOCK, FactoryStorage.FLUID).put(Capabilities.ItemHandler.BLOCK,FactoryStorage.ITEM).put(Capabilities.EnergyStorage.BLOCK,FactoryStorage.ENERGY).put(FactoryCapabilities.CRAFTY_ENERGY,FactoryStorage.CRAFTY_ENERGY).build();
    *///?}

    ListMap<String, ModInfo> MOD_INFOS = new ListMap<>();

    static Component getPlatformEnergyComponent() {
        //? if fabric {
        return Component.literal("Energy (E)").withStyle(ChatFormatting.GOLD);
        //?} elif forge || neoforge {
        /*return Component.literal("Forge Energy (FE)").withStyle(ChatFormatting.GREEN);
        *///?} else
        /*throw new AssertionError();*/
    }

    static <T> T getRegistryValue(ResourceLocation location, Registry<T> registry){
        return registry./*? if <1.21.2 {*/get/*?} else {*//*getValue*//*?}*/(location);
    }

    static <T> Optional<Holder.Reference<T>> getRegistryValue(RegistryAccess access, ResourceKey<T> resourceKey){
        return access.lookupOrThrow(ResourceKey.<T>createRegistryKey(resourceKey.registry())).get(resourceKey);
    }

    static IPlatformFluidHandler getItemFluidHandler(ItemStack container) {
        //? if fabric {
        ContainerItemContext context = ItemContainerPlatform.modifiableStackContext(container);
        Storage<FluidVariant> handStorage = FluidStorage.ITEM.find(container,context);
        if (handStorage instanceof  IPlatformFluidHandler p) return p;
        if (container.getItem() instanceof IFluidHandlerItem<?> f) return createItemFluidHandler(f,container);
        return handStorage != null ? (FabricFluidStoragePlatform)()-> handStorage : null;
        //?} elif forge || neoforge {
        /*IFluidHandlerItem handler = ItemContainerPlatform.getItemFluidHandler(container);
        return handler == null ? null : handler instanceof IPlatformFluidHandler f ? f : (ForgeFluidHandlerPlatform)()-> handler;
        *///?} else
        /*throw new AssertionError();*/
    }


    static IPlatformEnergyStorage getItemEnergyStorage(ItemStack stack) {
        //? if fabric {
        ContainerItemContext context = ItemContainerPlatform.modifiableStackContext(stack);
        EnergyStorage handStorage = EnergyStorage.ITEM.find(stack,context);
        if (handStorage instanceof  IPlatformEnergyStorage p) return p;
        if (stack.getItem() instanceof IEnergyStorageItem<?>) return getItemEnergyStorage(stack,context);
        return handStorage != null ? (FabricEnergyStoragePlatform)()-> handStorage : null;
        //?} elif forge || neoforge {
        /*IEnergyStorage storage = ItemContainerPlatform.getItemEnergyStorage(stack);
        return storage == null ? null : storage instanceof IPlatformEnergyStorage f ? f : (ForgeEnergyHandlerPlatform)()-> storage;
        *///?} else
        /*throw new AssertionError();*/
    }

    static ICraftyEnergyStorage getItemCraftyEnergyStorage(ItemStack stack) {
        //? if fabric {
        ICraftyEnergyStorage craftyStorage = CraftyEnergyStorage.ITEM.find(stack,ItemContainerPlatform.modifiableStackContext(stack));
        if (craftyStorage == null) return getItemCraftyEnergyStorageApi(stack);
        return craftyStorage;
        //?} elif forge {
        /*//? if <1.20.5 {
        return stack.getCapability(FactoryCapabilities.CRAFTY_ENERGY).orElse(null);
        //?} else
        /^return stack.getItem() instanceof IFactoryItem i ? i.getStorage(FactoryStorage.CRAFTY_ENERGY,stack).orElse(null) : null;^/
        *///?} elif neoforge {
        /*return stack.getCapability(FactoryCapabilities.CRAFTY_ENERGY_ITEM);
        *///?} else
        /*throw new AssertionError();*/
    }

    //? if fabric {
    static IPlatformFluidHandler createItemFluidHandler(IFluidHandlerItem<?> f, ItemStack container) {
        return new FactoryItemFluidHandler(f.getCapacity(),container,f::isFluidValid,f.getTransport());
    }
    static IPlatformEnergyStorage getItemEnergyStorage(ItemStack container, ContainerItemContext context) {
        return  container.getItem() instanceof IEnergyStorageItem<?> f  ? (FabricEnergyStoragePlatform) ()-> SimpleItemEnergyStorageImpl.createSimpleStorage(context,f.getCapacity(),f.getTransport().canInsert() ? f.getMaxReceive() : 0,f.getTransport().canExtract() ? f.getMaxConsume() : 0) : null;
    }
    static ICraftyEnergyStorage getItemCraftyEnergyStorageApi(ItemStack container) {
        return container.getItem() instanceof ICraftyStorageItem f ? new SimpleItemCraftyStorage(container,0,f.getCapacity(), f.getMaxConsume(), f.getMaxReceive(),f.getTransport(),f.getSupportedEnergyTier(), ItemContainerPlatform.isBlockItem(container)) : null;
    }
    //?} else if forge {
    /*static <T> RegisterListing.Holder<T> deferredToRegisterHolder(RegistryObject<T> holder){
        return new RegisterListing.Holder<>() {
            @Override
            public ResourceLocation getId() {
                return holder.getId();
            }
            @Override
            public T get() {
                return holder.get();
            }
        };
    }
    static <T> T getBlockCapability(BlockEntity entity, Capability<T> capability, Direction direction) {
        return capability == null ? null : entity.getCapability(capability,direction).orElse(null);
    }
    static IEventBus getModEventBus(){
        return FMLJavaModLoadingContext.get().getModEventBus();
    }
    static IEventBus getForgeEventBus(){
        return MinecraftForge.EVENT_BUS;
    }
    *///?} else if neoforge {
    /*static <T,V extends T> RegisterListing.Holder<V> deferredToRegisterHolder(DeferredHolder<T, V> holder){
        return new RegisterListing.Holder<>() {
            @Override
            public ResourceLocation getId() {
                return holder.getId();
            }
            @Override
            public V get() {
                return holder.get();
            }
        };
    }
    static <T> T getBlockCapability(BlockEntity entity, BlockCapability<T,Direction> capability, Direction direction) {
        return capability == null ? null : capability.getCapability(entity.getLevel(),entity.getBlockPos(),entity.getBlockState(),entity, direction);
    }
    static IEventBus getModEventBus(){
        return ModLoadingContext.get().getActiveContainer().getEventBus();
    }
    static IEventBus getForgeEventBus(){
        return NeoForge.EVENT_BUS;
    }
    *///?}

    //? if forge || neoforge {
    /*static IFluidHandler.FluidAction fluidActionOf(boolean simulate){
        return(simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }
    static FluidInstance fluidStackToInstance(FluidStack stack){
        return new FluidInstance(stack.getFluid(),stack.getAmount());
    }
    *///?}


    static <T extends IPlatformHandler, U extends IModifiableTransportHandler & IPlatformHandler> U filteredOf(T handler, Direction direction, TransportState transportState, Function<T, U> sidedGetter) {
        filteredHandlersCache.entrySet().removeIf(e-> e.getKey().isRemoved());
        SideList<? super IModifiableTransportHandler> list = filteredHandlersCache.computeIfAbsent(handler, d-> new SideList<>(() -> null));
        if (!list.contains(direction)) list.put(direction,sidedGetter.apply(handler));
        list.get(direction).setTransport(transportState);
        return list.get(direction) != null ? (U)list.get(direction) : null;
    }
    static IPlatformItemHandler filteredOf(IPlatformItemHandler itemHandler, Direction direction, int[] slots, TransportState transportState) {
        FactoryItemHandler.SidedWrapper storage = FactoryAPIPlatform.filteredOf(itemHandler,direction,transportState,FactoryItemHandler.SidedWrapper::new);
        if (storage != null) storage.slots = slots;
        return storage;
    }
    static IPlatformFluidHandler filteredOf(IPlatformFluidHandler fluidHandler, Direction direction, TransportState transportState) {
        return filteredOf((FactoryFluidHandler)fluidHandler,direction,transportState,FactoryFluidHandler.SidedWrapper::new);
    }
    static IPlatformEnergyStorage filteredOf(IPlatformEnergyStorage energyStorage, Direction direction, TransportState transportState) {
        return filteredOf((FactoryEnergyStorage) energyStorage,direction,transportState,FactoryEnergyStorage.SidedWrapper::new);
    }


    static IFactoryStorage getPlatformFactoryStorage(BlockEntity be) {
        if (be instanceof IFactoryStorage st) return st;
        FactoryAPIPlatform.platformStorageWrappersCache.entrySet().removeIf(e->e.getKey().isRemoved());
        return FactoryAPIPlatform.platformStorageWrappersCache.computeIfAbsent(be, (be1)-> new IFactoryStorage() {
            @Override
            public <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(FactoryStorage<T> storage, Direction direction) {
                //? if fabric {
                if (storage == FactoryStorage.ITEM) {
                Storage<ItemVariant> variantStorage = ItemStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                if (variantStorage instanceof IPlatformItemHandler) return ()->((T) variantStorage);
                if (variantStorage!= null)
                    return ()->((T)(FabricItemStoragePlatform)()-> variantStorage);
                } else if (storage == FactoryStorage.FLUID) {
                Storage<FluidVariant> variantStorage = FluidStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                if (variantStorage instanceof IPlatformFluidHandler) return ()->(T) variantStorage;
                if (variantStorage!= null)
                    return ()->((T)(FabricFluidStoragePlatform) ()-> variantStorage);
                }else if (storage == FactoryStorage.ENERGY) {
                EnergyStorage energyStorage = EnergyStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                      if (energyStorage instanceof IPlatformEnergyStorage) return ()->(T) energyStorage;
                        if (energyStorage!= null)
                            return ()->((T)(FabricEnergyStoragePlatform)()-> energyStorage);
                    }
                    else if (storage == FactoryStorage.CRAFTY_ENERGY) {
                        ICraftyEnergyStorage energyStorage = CraftyEnergyStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (energyStorage!= null) return ()->((T)energyStorage);
                    }
                //?} elif neoforge || forge {
                /*Object handler = getBlockCapability(be,BLOCK_CAPABILITY_MAP.getKey(storage),direction);
                if (handler != null) {
                    if (storage == FactoryStorage.ENERGY)
                        return (() -> (T) (handler instanceof IPlatformEnergyStorage energyHandler ? energyHandler : (ForgeEnergyHandlerPlatform) () -> (IEnergyStorage) handler));
                    else if (storage == FactoryStorage.CRAFTY_ENERGY && handler instanceof ICraftyEnergyStorage energyHandler)
                        return (() -> (T) energyHandler);
                    else if (storage == FactoryStorage.ITEM)
                        return (() -> (T) (handler instanceof IPlatformItemHandler itemHandler ? itemHandler : (ForgeItemStoragePlatform) () -> (IItemHandler) handler));
                    else if (storage == FactoryStorage.FLUID)
                        return (() -> (T) (handler instanceof IPlatformFluidHandler fluidHandler ? fluidHandler : (ForgeFluidHandlerPlatform) () -> (IFluidHandler) handler));
                }
                *///?}
                return ArbitrarySupplier.empty();
            }
        });
    }


    static boolean isClient() {
        //? if fabric {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        //?} else if forge || neoforge {
        /*return FMLEnvironment.dist.isClient();
        *///?} else
        /*throw new AssertionError();*/
    }

    static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> void registerByClassArgumentType(Class<A> infoClass, I argumentTypeInfo) {
        //? if fabric {
        ArgumentTypesAccessor.fabric_getClassMap().put(infoClass,argumentTypeInfo);
        //?} else if forge || neoforge {
        /*ArgumentTypeInfos.registerByClass(infoClass,argumentTypeInfo);
         *///?} else
        /*throw new AssertionError();*/
    }

    static <T> RegisterListing<T> createRegister(String namespace, Registry<T> registry) {
        //? if fabric {
        return new RegisterListing<>() {
            private final List<Holder<T>> REGISTER_LIST = new ArrayList<>();

            @Override
            public Collection<Holder<T>> getEntries() {
                return REGISTER_LIST;
            }
            @Override
            public Registry<T> getRegistry() {
                return registry;
            }
            @Override
            public String getNamespace() {
                return namespace;
            }
            @Override
            public void register() {
                forEach(o-> Registry.register(registry,o.getId(),o.get()));
            }
            @Override
            public <V extends T> Holder<V> add(String id, Supplier<V> supplier) {
                ResourceLocation location = FactoryAPI.createLocation(getNamespace(),id);
                Holder<V> h = new Holder<>() {
                    V obj;
                    @Override
                    public ResourceLocation getId() {
                        return location;
                    }

                    @Override
                    public V get() {
                        return obj == null ? (obj = supplier.get()) : obj ;
                    }
                };
                REGISTER_LIST.add((Holder<T>) h);
                return h;
            }
            @NotNull
            @Override
            public Iterator<Holder<T>> iterator() {
                return REGISTER_LIST.iterator();
            }

            @Override
            public Stream<Holder<T>> stream() {
                return REGISTER_LIST.stream();
            }
        };
        //?} else if forge || neoforge {
        /*return new RegisterListing<>() {
            private final DeferredRegister<T> REGISTER = DeferredRegister.create(registry.key(),namespace);

            @Override
            public Collection<Holder<T>> getEntries() {
                return stream().collect(Collectors.toSet());
            }
            @Override
            public Registry<T> getRegistry() {
                return registry;
            }
            @Override
            public String getNamespace() {
                return namespace;
            }
            @Override
            public void register() {
                REGISTER.register(getModEventBus());
            }
            @Override
            public <V extends T> Holder<V> add(String id, Supplier<V> supplier) {
                return deferredToRegisterHolder(REGISTER.register(id,supplier));
            }
            @NotNull
            @Override
            public Iterator<Holder<T>> iterator() {
                return stream().iterator();
            }

            @Override
            public Stream<Holder<T>> stream() {
                return REGISTER.getEntries().stream().map(h->(Holder<T>) deferredToRegisterHolder(h));
            }
        };
        *///?} else
        /*throw new AssertionError();*/
    }

    static String getCurrentClassName(String className) {
        //? if fabric {
        return FabricLoader.getInstance().getMappingResolver().mapClassName("official",className);
        //?} else
        /*return className;*/
    }

    static Collection<ModInfo> getMods() {
        //? if fabric {
        FabricLoader.getInstance().getAllMods().forEach(m-> getModInfo(m.getMetadata().getId()));
        //?} else if forge || neoforge {
        /*LoadingModList.get().getMods().forEach(m-> getModInfo(m.getModId()));
        *///?}
        return MOD_INFOS.values();
    }

    static ModInfo getModInfo(String modId) {
        return FactoryAPI.isModLoaded(modId) ? MOD_INFOS.computeIfAbsent(modId, s-> {
            //? if fabric {
            return new ModInfo() {
                Optional<ModContainer> opt = FabricLoader.getInstance().getModContainer(modId);
                @Override
                public Collection<String> getAuthors() {
                    return opt.map(c-> c.getMetadata().getAuthors().stream().map(Person::getName).toList()).orElse(Collections.emptyList());
                }

                @Override
                public Optional<String> getHomepage() {
                    return opt.flatMap(c-> c.getMetadata().getContact().get("homepage"));
                }

                @Override
                public Optional<String> getIssues() {
                    return opt.flatMap(c-> c.getMetadata().getContact().get("issues"));
                }

                @Override
                public Optional<String> getSources() {
                    return opt.flatMap(c-> c.getMetadata().getContact().get("sources"));
                }

                @Override
                public Collection<String> getCredits() {
                    return opt.map(c-> c.getMetadata().getContributors().stream().map(Person::getName).toList()).orElse(Collections.emptyList());
                }

                @Override
                public Collection<String> getLicense() {
                    return opt.map(c-> c.getMetadata().getLicense()).orElse(Collections.emptyList());
                }

                @Override
                public String getDescription() {
                    return opt.map(c->c.getMetadata().getDescription()).orElse("");
                }

                @Override
                public Optional<String> getLogoFile(int i) {
                    return opt.flatMap(c->c.getMetadata().getIconPath(i));
                }

                @Override
                public Optional<Path> findResource(String s) {
                    return opt.flatMap(c->c.findPath(s));
                }

                @Override
                public String getId() {
                    return modId;
                }

                @Override
                public String getVersion() {
                    return opt.map(c->c.getMetadata().getVersion().getFriendlyString()).orElse("");
                }

                @Override
                public String getName() {
                    return opt.map(c->c.getMetadata().getName()).orElse("");
                }

                @Override
                public boolean isHidden() {
                    return opt.isPresent() && opt.get().getMetadata().containsCustomValue("fabric-api:module-lifecycle");
                }
            };
            //?} else if forge || neoforge {
            /*return new ModInfo() {
            IModFileInfo info = ModList.get().getModFileById(modId);
            Optional<? extends ModContainer> opt = ModList.get().getModContainerById(modId);
            @Override
            public Collection<String> getAuthors() {
                return opt.flatMap(c->c.getModInfo().getConfig().getConfigElement("authors").map(s-> Collections.singleton(String.valueOf(s)))).orElse(Collections.emptySet());
            }

            @Override
            public Optional<String> getHomepage() {
                return opt.flatMap(c->c.getModInfo().getConfig().getConfigElement("displayURL").map(String::valueOf));
            }

            @Override
            public Optional<String> getIssues() {
                return Optional.ofNullable(info instanceof ModFileInfo i ? i.getIssueURL() : null).map(URL::toString);
            }

            @Override
            public Optional<String> getSources() {
                return Optional.empty();
            }

            @Override
            public Collection<String> getCredits() {
                return opt.flatMap(c->c.getModInfo().getConfig().getConfigElement("credits").map(o-> Set.of(String.valueOf(o)))).orElse(Collections.emptySet());
            }

            @Override
            public Collection<String> getLicense() {
                return Collections.singleton(info.getLicense());
            }

            @Override
            public String getDescription() {
                return opt.map(c->c.getModInfo().getDescription()).orElse("");
            }

            @Override
            public Optional<String> getLogoFile(int i) {
                return this.info.getMods().stream().filter(m->m.getModId().equals(modId)).findFirst().flatMap(IModInfo::getLogoFile);
            }
            @Override
            public Optional<Path> findResource(String s) {
                return Optional.of(this.info.getFile().findResource(s)).filter(Files::exists);
            }

            @Override
            public String getId() {
                return modId;
            }

            @Override
            public String getVersion() {
                return opt.map(c->c.getModInfo().getVersion().toString()).orElse("");
            }

            @Override
            public String getName() {
                return opt.map(c->c.getModInfo().getDisplayName()).orElse("");
            }

            };
            *///?} else
            /*throw new AssertionError();*/
        }) : null;
    }
    static boolean isPackHidden(Pack pack) {
        return /*? if fabric && >=1.20.4 {*/ ((FabricResourcePackProfile)pack).fabric_isHidden() /*?} else {*/ /*false*//*?}*/;
    }

}
