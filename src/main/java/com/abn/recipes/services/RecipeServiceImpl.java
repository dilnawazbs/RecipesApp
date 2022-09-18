package com.abn.recipes.services;

import com.abn.recipes.domain.Recipe;
import com.abn.recipes.exception.ResourceNotFoundException;
import com.abn.recipes.repositories.RecipeRepository;
import com.abn.recipes.utils.SearchCriteriaBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class RecipeServiceImpl implements RecipeService {

  @Autowired
  private RecipeRepository recipeRepository;

  @Override
  public List<Recipe> getAllRecipes() {
    return recipeRepository.findAll();
  }

  @Override
  public List<Recipe> getFilteredRecipe(MultiValueMap<String, String> filters) {
    return StreamSupport
      .stream(recipeRepository
          .findAll(SearchCriteriaBuilder.addCondition(filters))
          .spliterator(),
        false
      )
      .collect(Collectors.toList());
  }

  @Override
  public Recipe save(final Recipe recipe) {
    return recipeRepository.save(recipe);
  }

  @Override
  public Recipe findRecipeById(String recipeId)
    throws ResourceNotFoundException {
    return recipeRepository
      .findById(recipeId)
      .orElseThrow(() -> new ResourceNotFoundException("Recipe not found for this id :: " + recipeId));
  }

  @Override
  public void deleteRecipeById(String recipeId) {
    recipeRepository.deleteById(recipeId);
  }

  @Override
  public void deleteAllRecipes() {
    recipeRepository.deleteAll();
  }
}
