package com.abn.recipes.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.IORecipe;
import com.abn.recipes.domain.Recipe;
import com.abn.recipes.services.RecipeService;
import com.abn.recipes.utils.PatchMediaType;
import com.abn.recipes.utils.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RecipeService recipeService;

  @Test
  void shouldCreateRecipe() throws Exception {
    Recipe recipe = new Recipe("1", "Fried egg with tomato", 2, TestUtil.getIngredients("potato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );
    mockMvc.perform(post("/recipes").contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(recipe)))
      .andExpect(status().isCreated())
      .andDo(print());
  }

  @Test
  void shouldReturnRecipe() throws Exception {
    String id = "1";
    Recipe recipe = new Recipe(id, "Fried egg with tomato", 2, TestUtil.getIngredients("potato", "cauliflower"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );

    when(recipeService.findRecipeById(id)).thenReturn(recipe);
    mockMvc.perform(get("/recipes/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.title").value(recipe.getTitle()))
      .andExpect(jsonPath("$.ingredients").isArray())
      .andExpect(jsonPath("$.instructions").value(recipe.getInstructions()))
      .andExpect(jsonPath("$.category").value(recipe.getCategory().name()))
      .andDo(print());
  }

  @Test
  void shouldReturnListOfRecipes() throws Exception {
    Set<String> ingredients = new HashSet<>();
    ingredients.add("potato");
    ingredients.add("cauliflower");
    List<Recipe> recipes = new ArrayList<>(
      Arrays.asList(
        new Recipe("0", "Fried egg with tomato", 2, TestUtil.getIngredients("egg", "tomato"),
          "crack the egg on the pan with little oil.",
          Category.NON_VEGETARIAN
        ),
        new Recipe("1", "Kadai paneer", 3, TestUtil.getIngredients("paneer", "bell pepper"),
          "firstly, in a large kadai, heat 1 tbsp butter and saute 1 bay leaf, 1 green chilli, 1 tsp kasuri methi till they aromatic.",
          Category.VEGETARIAN
        ),
        new Recipe("2", "mixed veg", 2, TestUtil.getIngredients("bell pepper", "potato", "mushroom"),
          "chop all the ingredients and stir it in kadai with oil and spices",
          Category.VEGETARIAN
        ),
        new Recipe("3", "salmon with potato", 4, TestUtil.getIngredients("salmon", "potato"),
          "at 250 degree celcium grill the fish along with potato in the oven",
          Category.NON_VEGETARIAN
        )
      )
    );

    when(recipeService.getAllRecipes()).thenReturn(recipes);
    mockMvc.perform(get("/recipes"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.size()").value(recipes.size()))
      .andDo(print());
  }

  @Test
  void shouldReturnListOfRecipeWithFilter_Vegetarian() throws Exception {
    List<Recipe> recipes = new ArrayList<>(
      Arrays.asList(
        new Recipe("1", "Kadai paneer", 3, TestUtil.getIngredients("paneer", "bell pepper"),
          "firstly, in a large kadai, heat 1 tbsp butter and saute 1 bay leaf, 1 green chilli, 1 tsp kasuri methi till they aromatic.",
          Category.VEGETARIAN
        ),
        new Recipe("2", "mixed veg", 2, TestUtil.getIngredients("bell pepper", "potato", "mushroom"),
          "chop all the ingredients and stir it in kadai with oil and spices",
          Category.VEGETARIAN
        )
      )
    );

    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("category", "VEGETARIAN");

    when(recipeService.getFilteredRecipe(paramsMap)).thenReturn(recipes);
    mockMvc.perform(get("/recipes").params(paramsMap))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.size()").value(recipes.size()))
      .andDo(print());
  }

  @Test
  void shouldReturnNoContentWhenFilter_Servings5() throws Exception {
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("servings", "5");
    List<Recipe> recipes = Collections.emptyList();

    when(recipeService.getFilteredRecipe(paramsMap)).thenReturn(recipes);
    mockMvc.perform(get("/recipes").params(paramsMap))
      .andExpect(status().isNoContent())
      .andDo(print());
  }

  @Test
  void shouldUpdateRecipe() throws Exception {
    String id = "1";

    Recipe existingRecipe = new Recipe(id, "Fried egg with tomato", 2, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );
    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 4, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );

    when(recipeService.updateRecipe(any(String.class), any(IORecipe.class))).thenReturn(updatedRecipe);

    mockMvc.perform(put("/recipes/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updatedRecipe)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(updatedRecipe.getId()))
      .andExpect(jsonPath("$.servings").value(updatedRecipe.getServings()))
      .andDo(print());
  }

  @Test
  void shouldReturnNoContentFoundUpdateRecipe() throws Exception {
    String id = "1";

    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 4, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );
    when(recipeService.findRecipeById(id)).thenReturn(null);
    when(recipeService.save(any(Recipe.class))).thenReturn(updatedRecipe);

    mockMvc.perform(put("/recipes/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updatedRecipe)))
      .andExpect(status().is2xxSuccessful())
      .andDo(print());
  }

  @Test
  void shouldMergePatchUpdateRecipe() throws Exception {
    String id = "1";

    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 4, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );

    when(recipeService.saveMergePatch(any(String.class), any(JsonMergePatch.class))).thenReturn(updatedRecipe);

    mockMvc.perform(patch("/recipes/{id}", id)
          .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
          .content(TestUtil.fromFile("merge-patch.json")))
      .andExpect(status().isNoContent())
      .andDo(print());
  }

  @Test
  void shouldPatchUpdateRecipe() throws Exception {
    String id = "1";

    Recipe updatedRecipe = new Recipe(id, "Fried egg with tomato", 4, TestUtil.getIngredients("egg", "tomato"),
      "crack the egg on the pan with little oil.",
      Category.NON_VEGETARIAN
    );

    when(recipeService.saveJsonPatch(any(String.class), any(JsonPatch.class))).thenReturn(updatedRecipe);
    mockMvc.perform(patch("/recipes/{id}", id)
          .contentType(PatchMediaType.APPLICATION_JSON_PATCH)
          .content(TestUtil.fromFile("json-patch.json")))
      .andExpect(status().isNoContent())
      .andDo(print());
  }

  @Test
  void shouldPatchUpdateRecipe_UnSupportedMediaType() throws Exception {
    String id = "1";

    mockMvc.perform(patch("/recipes/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.fromFile("json-patch.json")))
      .andExpect(status().isUnsupportedMediaType())
      .andDo(print());
  }

  @Test
  void shouldDeleteRecipe() throws Exception {
    String id = "1";
    doNothing().when(recipeService).deleteRecipeById(id);
    mockMvc.perform(delete("/recipes/{id}", id))
      .andExpect(status().is2xxSuccessful())
      .andDo(print());
  }

  @Test
  void shouldDeleteAllRecipes() throws Exception {
    doNothing().when(recipeService).deleteAllRecipes();
    mockMvc.perform(delete("/recipes"))
      .andExpect(status().is2xxSuccessful())
      .andDo(print());
  }
}
