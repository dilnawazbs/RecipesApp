package com.abn.recipes.services;

import com.abn.recipes.domain.Recipe;
import com.abn.recipes.domain.RecipeDTO;
import com.abn.recipes.exception.ResourceNotFoundException;
import com.abn.recipes.repositories.RecipeRepository;
import com.abn.recipes.utils.PatchHelper;
import com.abn.recipes.utils.RecipeMapper;
import com.abn.recipes.utils.SearchCriteriaBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class RecipeServiceImpl implements RecipeService {

  @Autowired
  private RecipeRepository recipeRepository;

  @Autowired
  private RecipeMapper mapper;

  @Autowired
  private PatchHelper patchHelper;

  @Override
  public List<Recipe> getAllRecipes() {
    return mapper.map(recipeRepository.findAll());
  }

  @Override
  public List<Recipe> getFilteredRecipe(MultiValueMap<String, String> filters) {
    return mapper.map(StreamSupport
      .stream(recipeRepository
          .findAll(SearchCriteriaBuilder.addCondition(filters))
          .spliterator(),
        false
      )
      .collect(Collectors.toList()));
  }

  @Override
  public Recipe save(final Recipe recipe) {
    return mapper.asRecipe(recipeRepository.save(mapper.asRecipeDTO(recipe)));
  }

  @Override
  public Recipe updateRecipe(final String recipeId, final Recipe recipeDetails) throws ResourceNotFoundException {
    Recipe recipe = findRecipeById(recipeId);
    mapper.update(recipe, recipeDetails);
    return save(recipe);
  }

  @Override
  public Recipe saveMergePatch(final String recipeId, final JsonMergePatch recipePatch) throws ResourceNotFoundException {
    Recipe recipe = findRecipeById(recipeId);
    Recipe recipeResource = mapper.asInput(recipe);
    Recipe patchedRecipeResource = patchHelper.mergePatch(recipePatch, recipeResource, Recipe.class);
    mapper.update(recipe, patchedRecipeResource);
    return save(recipe);
  }

  @Override
  public Recipe saveJsonPatch(final String recipeId, final JsonPatch recipePatch) throws ResourceNotFoundException {
    Recipe recipe = findRecipeById(recipeId);
    Recipe recipeResource = mapper.asInput(recipe);
    Recipe patchedRecipeResource = patchHelper.patch(recipePatch, recipeResource, Recipe.class);
    mapper.update(recipe, patchedRecipeResource);
    return save(recipe);
  }

  @Override
  public Recipe findRecipeById(String recipeId)
    throws ResourceNotFoundException {
      RecipeDTO recipeDTO = recipeRepository
      .findById(recipeId)
      .orElseThrow(() -> new ResourceNotFoundException("Recipe not found for this id :: " + recipeId));
    return mapper.asRecipe(recipeDTO);
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
