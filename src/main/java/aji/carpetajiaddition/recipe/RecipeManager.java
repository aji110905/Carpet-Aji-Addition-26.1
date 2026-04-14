package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.recipe.builder.ShapedRecipeBuilder;
import aji.carpetajiaddition.recipe.template.ShapedRecipeTemplate;
import aji.carpetajiaddition.settings.RuleCategory;
import aji.carpetajiaddition.util.MinecraftServerUtil;
import carpet.api.settings.Rule;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.item.Items.DRAGON_BREATH;
import static net.minecraft.world.item.Items.DRAGON_EGG;
import static net.minecraft.world.item.Items.EGG;
import static net.minecraft.world.item.Items.END_CRYSTAL;
import static net.minecraft.world.item.Items.GLASS_BOTTLE;

public class RecipeManager {
    private static final List<ShapedRecipeTemplate> shapedRecipeList = new ArrayList<>();

    public static void addShapedRecipe(String id, String[][] pattern, Map<Character, String> ingredients, String result, int count) {
        shapedRecipeList.add(new ShapedRecipeTemplate(Identifier.fromNamespaceAndPath(CarpetAjiAdditionSettings.MOD_ID, id), pattern, ingredients, result, count));
    }

    public static void registerRecipes(Map<Identifier, Recipe<?>> recipeMap, HolderLookup.Provider provider) {
        shapedRecipeList.forEach(recipe -> recipe.addToRecipeMap(recipeMap, provider));
    }

    public static void clearRecipeListMemory() {
        shapedRecipeList.clear();
    }

    public static void buildRecipes(){
        ShapedRecipeBuilder.create(CarpetAjiAdditionSettings.dragonEggRecipe, "dragon_egg")
                .pattern("&#&")
                .pattern("^*^")
                .pattern("$$$")
                .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                .output(DRAGON_EGG, 1).build();

        ShapedRecipeBuilder.create(CarpetAjiAdditionSettings.dragonBreathRecipe, "dragon_breath")
                .pattern("#")
                .pattern("*")
                .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                .output(DRAGON_BREATH, 1).build();
    }

    public static void onRuleValueChanged(){
        if (MinecraftServerUtil.serverIsRunning()) {
            RecipeManager.clearRecipeListMemory();
            buildRecipes();
            CarpetAjiAdditionSettings.minecraftServer.execute(() -> {
                needReloadServerResources();
                for (RecipeHolder<?> recipe : CarpetAjiAdditionSettings.minecraftServer.getRecipeManager().getRecipes()) {
                    if (recipe.id().identifier().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID)) {
                        for (ServerPlayer player : CarpetAjiAdditionSettings.minecraftServer.getPlayerList().getPlayers()) {
                            if (!player.getRecipeBook().contains(recipe.id())) {
                                player.awardRecipes(List.of(recipe));
                            }
                        }
                    }
                }
            });
        }
    }

    public static void onPlayerLoggedIn(ServerPlayer player){
        if (MinecraftServerUtil.serverIsRunning() && hasActiveRecipeRule()) {
            for (RecipeHolder<?> recipe : CarpetAjiAdditionSettings.minecraftServer.getRecipeManager().getRecipes()) {
                if (recipe.id().identifier().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                    player.awardRecipes(List.of(recipe));
                }
            }
        }
    }

    private static boolean hasActiveRecipeRule() {
        Field[] fields = CarpetAjiAdditionSettings.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Rule.class)) {
                try {
                    field.setAccessible(true);
                    if (Arrays.asList(field.getAnnotation(Rule.class).categories()).contains(RuleCategory.RECIPE) && field.getBoolean(null)) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    CarpetAjiAdditionSettings.LOGGER.error("Failed to get rule value", e);
                }
            }
        }
        return false;
    }

    public static void needReloadServerResources() {
        if (MinecraftServerUtil.serverIsRunning() && hasActiveRecipeRule()) {
            CarpetAjiAdditionSettings.minecraftServer.reloadResources(CarpetAjiAdditionSettings.minecraftServer.getPackRepository().getSelectedIds());
        }
    }
}
