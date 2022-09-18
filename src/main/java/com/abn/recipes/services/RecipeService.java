package com.abn.recipes.services;

import com.abn.recipes.domain.Recipe;
import com.abn.recipes.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.util.MultiValueMap;

public interface RecipeService {
  /**
   * @return All the recepies
   */
  List<Recipe> getAllRecipes();

  /**
   *
   * @param filters the filtered query from URI
   * @return  the filetered {@link Recipe}
   */
  List<Recipe> getFilteredRecipe(final MultiValueMap<String, String> filters);

  /**
   *
   * @param recipe the {@link Recipe} to save
   * @return the saved {@link Recipe}
   */
  Recipe save(final Recipe recipe);

  /**
   * @param recipeId the id of the {@link Recipe}
   *
   * @return the {@link Recipe}
   * @throws ResourceNotFoundException
   */
  Recipe findRecipeById(final String recipeId) throws ResourceNotFoundException;

  /**
   * Deletes the {@link Recipe} by id.
   *
   * @param recipeId the id of {@link Recipe} to delete
   */
  void deleteRecipeById(final String recipeId);

  /**
   * Deletes all the {@link Recipe}.
   */
  void deleteAllRecipes();
}
