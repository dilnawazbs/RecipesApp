package com.abn.recipes.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.RecipeDTO;
import com.abn.recipes.utils.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeRepositoryTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private RecipeRepository recipeRepository;

  @BeforeEach
  public void addDataBase() {
    recipeRepository.deleteAll();
  }

  @AfterEach
  public void deleteDataBase() {
    mongoTemplate.getDb().drop();
  }

  @Test
  public void shouldFindNoRecipesIfRepositoryIsEmpty() {
    Iterable<RecipeDTO> recipes = recipeRepository.findAll();
    assertThat(recipes).isEmpty();
  }

  @Test
  public void shouldStoreARecipes() {
    RecipeDTO recipe = recipeRepository.save(
      new RecipeDTO("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"),
        "crack the egg on the pan with little oil.",
        Category.NON_VEGETARIAN
      ));
    assertThat(recipe).hasFieldOrPropertyWithValue("title", "Fried egg with tomato");
    assertThat(recipe).hasFieldOrPropertyWithValue("category", Category.NON_VEGETARIAN);
    assertThat(recipe).hasFieldOrPropertyWithValue("servings", 2);
  }

  @Test
  public void shouldFindAllRecipes() {
    RecipeDTO recipe1 = new RecipeDTO("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    recipeRepository.save(recipe1);

    RecipeDTO recipe2 = new RecipeDTO("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"),
      "Bake potatoes and cauliflower in oven.",
      Category.VEGETARIAN);
    recipeRepository.save(recipe2);

    Iterable<RecipeDTO> recipes = recipeRepository.findAll();
    assertThat(recipes).hasSize(2);
  }

  @Test
  public void shouldFindRecipesById() {
    RecipeDTO recipe1 = new RecipeDTO("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    recipeRepository.save(recipe1);

    RecipeDTO recipe2 = new RecipeDTO("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"),
      "Bake potatoes and cauliflower in oven.",
      Category.VEGETARIAN);
    recipeRepository.save(recipe2);

    RecipeDTO recipeResult = recipeRepository.findById(recipe2.getId()).get();
    assertThat(recipeResult.getTitle()).isEqualTo(recipe2.getTitle());
  }

  @Test
  public void shouldUpdateRecipeById() {
    RecipeDTO recipe1 = new RecipeDTO("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    recipeRepository.save(recipe1);

    RecipeDTO recipe2 = new RecipeDTO("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"),
      "Bake potatoes and cauliflower in oven.",
      Category.VEGETARIAN);
    recipeRepository.save(recipe2);

    RecipeDTO updatedRecipe = new RecipeDTO("2", "Fried cauliflower and potatoes", 4, TestUtil.getIngredients("potato", "cauliflower"),
      "Bake potatoes and cauliflower in oven.",
      Category.VEGETARIAN);

    RecipeDTO recipe = recipeRepository.findById(recipe2.getId()).get();
    recipe.setTitle(updatedRecipe.getTitle());
    recipe.setServings(updatedRecipe.getServings());
    recipeRepository.save(recipe);

    RecipeDTO checkRecipe = recipeRepository.findById(recipe2.getId()).get();

    assertThat(checkRecipe.getId()).isEqualTo(recipe.getId());
    assertThat(checkRecipe.getTitle()).isEqualTo(recipe.getTitle());
    assertThat(checkRecipe.getServings()).isEqualTo(recipe.getServings());
  }

  @Test
  public void shouldDeleteRecipeById() {
    RecipeDTO recipe1 = new RecipeDTO("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN);
    recipeRepository.save(recipe1);

    RecipeDTO recipe2 = new RecipeDTO("2","Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"),
      "Bake potatoes and cauliflower in oven.",
      Category.VEGETARIAN);
    recipeRepository.save(recipe2);

    recipeRepository.deleteById(recipe2.getId());

    Iterable<RecipeDTO> recipes = recipeRepository.findAll();
    assertThat(recipes).hasSize(1);
  }

  @Test
  public void shouldDeleteAllRecipes() {
    recipeRepository.save(
      new RecipeDTO("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"),
        "crack the egg on the pan with little oil.",
        Category.NON_VEGETARIAN));
    recipeRepository.save(
      new RecipeDTO("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"),
        "Bake potatoes and cauliflower in oven.",
        Category.VEGETARIAN));

    recipeRepository.deleteAll();

    assertThat(recipeRepository.findAll()).isEmpty();
  }
}
