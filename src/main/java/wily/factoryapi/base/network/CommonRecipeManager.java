package wily.factoryapi.base.network;

//? if >=1.21.2 {
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.mixin.base.RecipeManagerAccessor;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.network.CommonNetwork;
//? if >1.20.1 {
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommonRecipeManager {

    public static <R extends Recipe<?>> /*? if >1.20.1 {*/RecipeHolder<R>/*?} else {*//*R*//*?}*/ byId(ResourceLocation id, RecipeType<R> type) {
        return (/*? if >1.20.1 {*/RecipeHolder<R>/*?} else {*//*R*//*?}*/)/*? if <1.21.2 {*//*getRecipeManager().byKey(id).orElse(null)*//*?} else {*/recipesByType.getOrDefault(type, Collections.emptyMap()).get(id)/*?}*/;
    }

    public static <R extends Recipe<?>> Collection</*? if >1.20.1 {*/RecipeHolder<R>/*?} else {*//*R*//*?}*/> byType(RecipeType<R> type) {
        return /*? if <1.21.2 {*/ /*((RecipeManagerAccessor)getRecipeManager()).getRecipeByType(type)/^? if <1.20.5 {^//^.values()^//^?}^/*//*?} else {*/(Collection) recipesByType.get(type).values()/*?}*/;
    }

    public static <R extends Recipe<I>, I extends /*? if <1.20.5 {*//*Container*//*?} else {*/RecipeInput/*?}*/> Optional</*? if >1.20.1 {*/RecipeHolder<R>/*?} else {*//*R*//*?}*/> getRecipeFor(RecipeType<R> type, I input, Level level) {
        Collection</*? if >1.20.1 {*/RecipeHolder<R>/*?} else {*//*R*//*?}*/> recipes = byType(type);
        for (/*? if >1.20.1 {*/RecipeHolder<R>/*?} else {*//*R*//*?}*/ recipe : recipes) {
            if (recipe/*? if >1.20.1 {*/.value()/*?}*/.matches(input,level)) return Optional.of(recipe);
        }
        return Optional.empty();
    }
    public static <R extends Recipe<I>, I extends /*? if <1.20.5 {*//*Container*//*?} else {*/RecipeInput/*?}*/> Optional<ItemStack> getResultFor(RecipeType<R> type, I input, Level level) {
        return getRecipeFor(type,input,level).map(r->r/*? if >1.20.1 {*/.value()/*?}*/.assemble(input,level.registryAccess()));
    }

    //? if <1.21.2 {
    /*public static RecipeManager getRecipeManager(){
        return FactoryAPI.currentServer == null ? FactoryAPIClient.getRecipeManager() : FactoryAPI.currentServer.getRecipeManager();
    }
    *///?}

    //? if >=1.21.2 {
    private static final Set<RecipeType<?>> recipeTypesToSync = new HashSet<>();
    private static Map<RecipeType<?>,Map<ResourceLocation,RecipeHolder<?>>> recipesByType = Collections.emptyMap();

    public static void updateRecipes(RecipeManager manager){
        recipesByType = manager.getRecipes().stream().collect(Collectors.groupingBy(h->h.value().getType(),Collectors.toMap(h->h.id().location(), Function.identity())));
        for (RecipeType<?> recipeType : recipeTypesToSync) {
            ClientPayload.getInstance().syncRecipeTypes.put(recipeType, recipesByType.get(recipeType));
        }
    }

    public static void clearRecipes(){
        recipesByType = Collections.emptyMap();
        ClientPayload.getInstance().syncRecipeTypes.clear();
    }

    public static boolean canSyncType(RecipeType<?> type){
        return recipeTypesToSync.contains(type);
    }

    public static void addRecipeTypeToSync(RecipeType<?> recipeType){
        recipeTypesToSync.add(recipeType);
    }

    public record ClientPayload(Map<RecipeType<?>,Map<ResourceLocation,RecipeHolder<?>>> syncRecipeTypes) implements CommonNetwork.Payload {
        public static final CommonNetwork.Identifier<ClientPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("send_client_recipes"), ClientPayload::new);
        private static final ClientPayload instance = new ClientPayload(new HashMap<>());

        public static ClientPayload getInstance(){
            return instance;
        }

        public ClientPayload(CommonNetwork.PlayBuf buf){
            this(buf.get().readMap(b->b.readById(BuiltInRegistries.RECIPE_TYPE::byId), b->b.readMap(FriendlyByteBuf::readResourceLocation, b1->RecipeHolder.STREAM_CODEC.decode(buf.get()))));
        }

        @Override
        public void apply(Context context) {
            recipesByType = syncRecipeTypes;
        }

        @Override
        public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
            return ID;
        }

        @Override
        public void encode(CommonNetwork.PlayBuf buf) {
            buf.get().writeMap(syncRecipeTypes, (b,t)-> b.writeById(BuiltInRegistries.RECIPE_TYPE::getId,t),(b,t)-> b.writeMap(t,FriendlyByteBuf::writeResourceLocation,(b1,h)->RecipeHolder.STREAM_CODEC.encode(buf.get(),h)));
        }
    }
    //?}
}
