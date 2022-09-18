package com.abn.recipes.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.Recipe;
import com.abn.recipes.utils.TestUtil;

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
    public void should_find_no_recipes_if_repository_is_empty() {
        Iterable<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).isEmpty();
    }

    @Test
    public void should_store_a_recipes() {
        Recipe recipe = recipeRepository.save(new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN));
        assertThat(recipe).hasFieldOrPropertyWithValue("title", "Fried egg with tomato");
        assertThat(recipe).hasFieldOrPropertyWithValue("category", Category.NON_VEGETARIAN);
        assertThat(recipe).hasFieldOrPropertyWithValue("servings", 2);
    }

    @Test
    public void should_find_all_recipes() {
        Recipe recipe1 = new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN);
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"), "Bake potatoes and cauliflower in oven.", Category.VEGETARIAN);
        recipeRepository.save(recipe2);

        Iterable<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(2);
    }

    @Test
    public void should_find_recipes_by_id() {
        Recipe recipe1 = new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN);
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"), "Bake potatoes and cauliflower in oven.", Category.VEGETARIAN);
        recipeRepository.save(recipe2);

        Recipe recipeResult = recipeRepository.findById(recipe2.getId()).get();
        assertThat(recipeResult.getTitle()).isEqualTo(recipe2.getTitle());
    }

    @Test
    public void should_update_recipe_by_id() {
        Recipe recipe1 = new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN);
        recipeRepository.save(recipe1);
        
        Recipe recipe2 = new Recipe("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"), "Bake potatoes and cauliflower in oven.", Category.VEGETARIAN);
        recipeRepository.save(recipe2);

        Recipe updatedRecipe = new Recipe("2", "Fried cauliflower and potatoes", 4, TestUtil.getIngredients("potato", "cauliflower"), "Bake potatoes and cauliflower in oven.", Category.VEGETARIAN);

        Recipe recipe = recipeRepository.findById(recipe2.getId()).get();
        recipe.setTitle(updatedRecipe.getTitle());
        recipe.setServings(updatedRecipe.getServings());
        recipeRepository.save(recipe);

        Recipe checkRecipe = recipeRepository.findById(recipe2.getId()).get();
    
        assertThat(checkRecipe.getId()).isEqualTo(recipe.getId());
        assertThat(checkRecipe.getTitle()).isEqualTo(recipe.getTitle());
        assertThat(checkRecipe.getServings()).isEqualTo(recipe.getServings());
    }

    @Test
    public void should_delete_recipe_by_id() {
        Recipe recipe1 = new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN);
        recipeRepository.save(recipe1);
            
        Recipe recipe2 = new Recipe("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"), "Bake potatoes and cauliflower in oven.", Category.VEGETARIAN);
        recipeRepository.save(recipe2);

        recipeRepository.deleteById(recipe2.getId());

        Iterable<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(1);
    }

    @Test
    public void should_delete_all_tutorials() {
        recipeRepository.save(new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "egg"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN));
        recipeRepository.save(new Recipe("2", "Fried potatoes", 3, TestUtil.getIngredients("potato", "cauliflower"), "Bake potatoes and cauliflower in oven.", Category.VEGETARIAN));

        recipeRepository.deleteAll();

        assertThat(recipeRepository.findAll()).isEmpty();
    }
} 