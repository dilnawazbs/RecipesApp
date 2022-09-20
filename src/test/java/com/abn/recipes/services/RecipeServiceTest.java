package com.abn.recipes.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.Recipe;
import com.abn.recipes.exception.ResourceNotFoundException;
import com.abn.recipes.repositories.RecipeRepository;
import com.abn.recipes.utils.PatchHelper;
import com.abn.recipes.utils.RecipeMapper;
import com.abn.recipes.utils.TestUtil;
import com.querydsl.core.types.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {

  @Mock
  private RecipeRepository recipeRepository;

  @InjectMocks
  private RecipeServiceImpl recipeService;

  @Mock
  private RecipeMapper mapper;

  @Mock
  private PatchHelper patchHelper;

  @Test
  public void shouldReturnRecipe() throws Exception {
    String id = "1";
    Recipe recipe = new Recipe(id, "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "cauliflower"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
    Recipe foundedRecipe = recipeService.findRecipeById(id);
    Assertions.assertEquals(2, foundedRecipe.getServings());
    Assertions.assertEquals(2, foundedRecipe.getServings());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void shouldThrowExceptionWhenEmptyRecipe() throws Exception {
    String id = "1";
    when(recipeRepository.findById(id)).thenReturn(Optional.empty());
    try {
      recipeService.findRecipeById(id);
    } catch (ResourceNotFoundException exception) {
      Assertions.assertEquals("Recipe not found for this id :: 1", exception.getMessage());
      throw exception;
    }
  }

  @Test
  public void shouldCreateRecipe() {
    Recipe recipe = new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    when(recipeRepository.save(recipe)).thenReturn(recipe);
    Recipe foundedRecipe = recipeService.save(recipe);
    Assertions.assertEquals("1", foundedRecipe.getId());
    Assertions.assertEquals(2, foundedRecipe.getServings());
    Assertions.assertEquals(Category.NON_VEGETARIAN, foundedRecipe.getCategory());
  }

  @Test
  public void shouldReturnListOfRecipes() {
    Set<String> ingredients = new HashSet<>();
    ingredients.add("potato");
    ingredients.add("cauliflower");
    List<Recipe> recipes = new ArrayList<>(
      Arrays.asList(
        new Recipe("0", "Fried egg with tomato", 2, TestUtil.getIngredients("egg", "tomato"),
          "crack the egg on the pan with little oil.",
          Category.NON_VEGETARIAN),
        new Recipe( "1", "Kadai paneer", 3, TestUtil.getIngredients("paneer", "bell pepper"),
          "firstly, in a large kadai, heat 1 tbsp butter and saute 1 bay leaf, 1 green chilli, 1 tsp kasuri methi till they aromatic.",
          Category.VEGETARIAN),
        new Recipe("2", "mixed veg", 2, TestUtil.getIngredients("bell pepper", "potato", "mushroom"),
          "chop all the ingredients and stir it in kadai with oil and spices",
          Category.VEGETARIAN),
        new Recipe("2", "salmon with potato", 4, TestUtil.getIngredients("salmon", "potato"),
          "at 250 degree celcium grill the fish along with potato in the oven",
          Category.NON_VEGETARIAN)
      )
    );
    when(recipeRepository.findAll()).thenReturn(recipes);
    List<Recipe> foundedRecipes = recipeService.getAllRecipes();
    Assertions.assertEquals(4, foundedRecipes.size());
    Assertions.assertEquals(2, foundedRecipes.get(0).getServings());
    Assertions.assertEquals(Category.VEGETARIAN, foundedRecipes.get(1).getCategory());
  }

  @Test
  public void shouldReturnListOfRecipeWithFilter_Vegetarian() {
    List<Recipe> recipes = new ArrayList<>(
      Arrays.asList(
        new Recipe("1", "Kadai paneer", 3, TestUtil.getIngredients("paneer", "bell pepper"),
          "firstly, in a large kadai, heat 1 tbsp butter and saute 1 bay leaf, 1 green chilli, 1 tsp kasuri methi till they aromatic.",
          Category.VEGETARIAN),
        new Recipe("2", "mixed veg", 2, TestUtil.getIngredients("bell pepper", "potato", "mushroom"),
          "chop all the ingredients and stir it in kadai with oil and spices",
          Category.VEGETARIAN)));
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("category", "VEGETARIAN");
    when(recipeRepository.findAll(any(Predicate.class))).thenReturn(recipes);
    List<Recipe> foundedRecipes = recipeService.getFilteredRecipe(paramsMap);
    Assertions.assertEquals(2, foundedRecipes.size());
    Assertions.assertEquals(Category.VEGETARIAN, foundedRecipes.get(0).getCategory());
    Assertions.assertEquals(Category.VEGETARIAN, foundedRecipes.get(1).getCategory());
  }

  @Test
  public void shouldReturnListOfRecipeWithFilter_ExcludeSalmon_HasOven() {
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("instructions", "oven");
    paramsMap.add("excludes", "salmon");
    List<Recipe> recipes = new ArrayList<>(
      Arrays.asList(
        new Recipe("2", "grilled with potato", 4, TestUtil.getIngredients("potato"),
          "at 250 degree celcium grill the potato in the oven",
          Category.VEGETARIAN)));

    when(recipeRepository.findAll(any(Predicate.class))).thenReturn(recipes);
    List<Recipe> foundedRecipes = recipeService.getFilteredRecipe(paramsMap);
    Assertions.assertEquals(1, foundedRecipes.size());
    Assertions.assertEquals(true, foundedRecipes.get(0).getInstructions().contains("oven"));
    Assertions.assertEquals(true, !foundedRecipes.get(0).getIngredients().contains("salmon"));
  }

  @Test
  public void shouldReturnNoContentWhenFilter_ExcludeSalmon_HasOven() {
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("instructions", "oven");
    paramsMap.add("excludes", "salmon");
    List<Recipe> recipes = new ArrayList<>();
    when(recipeRepository.findAll(any(Predicate.class))).thenReturn(recipes);
    List<Recipe> foundedRecipes = recipeService.getFilteredRecipe(paramsMap);
    Assertions.assertEquals(0, foundedRecipes.size());
  }

  @Test
  public void shouldReturnListOfRecipeWithFilter_Serving4_IncludesPotato() {
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("servings", "4");
    paramsMap.add("includes", "potato");
    List<Recipe> recipes = new ArrayList<>(
      Arrays.asList(
        new Recipe("4", "grilled with potato", 4, TestUtil.getIngredients("potato"),
          "at 250 degree celcium grill the potato in the oven",
          Category.VEGETARIAN)));
    when(recipeRepository.findAll(any(Predicate.class))).thenReturn(recipes);
    List<Recipe> foundedRecipes = recipeService.getFilteredRecipe(paramsMap);
    Assertions.assertEquals(1, foundedRecipes.size());
    Assertions.assertEquals(4, foundedRecipes.get(0).getServings());
    Assertions.assertEquals(true, foundedRecipes.get(0).getIngredients().contains("potato"));
  }

  @Test
  public void shouldUpdateRecipe() {
    String id = "1";
    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 4, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    when(recipeRepository.save(updatedRecipe)).thenReturn(updatedRecipe);
    Recipe foundedRecipe = recipeService.save(updatedRecipe);
    Assertions.assertEquals(id, foundedRecipe.getId());
    Assertions.assertEquals(4, foundedRecipe.getServings());
    Assertions.assertEquals(true, foundedRecipe.getIngredients().contains("egg"));
    verify(recipeRepository, times(1)).save(updatedRecipe);
  }

  @Test
  public void shouldUpdateRecipeForGivenRecipeId() {
    String id = "1";
    Recipe currentRecipe = new Recipe(id, "Fried egg with tomato", 8, TestUtil.getIngredients("egg", "beans"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 4, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    when(recipeRepository.findById(id)).thenReturn(Optional.of(currentRecipe));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
    JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createValue(TestUtil.fromFile("merge-patch.json")));
    try {
      Recipe foundedRecipe = recipeService.saveMergePatch(id, jsonMergePatch);
      Assertions.assertEquals(id, foundedRecipe.getId());
      Assertions.assertEquals(4, foundedRecipe.getServings());
      Assertions.assertEquals(true, foundedRecipe.getIngredients().contains("egg"));
      verify(recipeRepository, times(1)).save(any(Recipe.class));
      verify(mapper).asInput(any(Recipe.class));
    } catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldUpdateRecipeUsingPatchForGivenRecipeId() {
    String id = "1";
    Recipe currentRecipe = new Recipe(id, "Fried egg with tomato", 8, TestUtil.getIngredients("egg", "beans"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 10, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    when(recipeRepository.findById(id)).thenReturn(Optional.of(currentRecipe));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);

    JsonPatchBuilder builder = Json.createPatchBuilder();
    JsonPatch jsonPatch = builder.replace("/servings", 10).build();
    try {
      Recipe foundedRecipe = recipeService.saveJsonPatch(id, jsonPatch);
      Assertions.assertEquals(id, foundedRecipe.getId());
      Assertions.assertEquals(10, foundedRecipe.getServings());
      Assertions.assertEquals(true, foundedRecipe.getIngredients().contains("egg"));
      verify(recipeRepository, times(1)).save(any(Recipe.class));
      verify(mapper).asInput(any(Recipe.class));
    } catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldDeleteRecipe() {
    String id = "1";
    recipeService.deleteRecipeById(id);
    verify(recipeRepository, times(1)).deleteById(eq(id));
  }

  @Test
  public void shouldDeleteAllRecipes() {
    recipeService.deleteAllRecipes();
    verify(recipeRepository, times(1)).deleteAll();
  }
}
