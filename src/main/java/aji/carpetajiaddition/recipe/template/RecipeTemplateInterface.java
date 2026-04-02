package aji.carpetajiaddition.recipe.template;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Map;

public interface RecipeTemplateInterface {
    void addToRecipeMap(Map<Identifier, Recipe<?>> recipeMap, HolderLookup.Provider provider);
}
