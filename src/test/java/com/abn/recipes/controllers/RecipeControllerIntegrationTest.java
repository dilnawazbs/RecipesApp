package com.abn.recipes.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;

import com.abn.recipes.RecipesApplication;
import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.Recipe;
import com.abn.recipes.utils.PatchMediaType;
import com.abn.recipes.utils.TestUtil;

import lombok.SneakyThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = RecipesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecipeControllerIntegrationTest {
    @Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

    private String getRootUrl() {
		return "http://localhost:" + port;
	}

	@Test
	public void contextLoads() {
    }

	@BeforeAll
    public void setup(){
        List<Recipe> recipes = new ArrayList<>(
            Arrays.asList(new Recipe("0", "Fried egg with tomato", 2, TestUtil.getIngredients("egg", "tomato"), "crack the egg on the pan with little oil. And bake in oven for 2 minutes.", Category.NON_VEGETARIAN),
                new Recipe("1", "Kadai paneer", 3, TestUtil.getIngredients("paneer", "bell pepper"), "firstly, in a large kadai, heat 1 tbsp butter and saute 1 bay leaf, 1 green chilli, 1 tsp kasuri methi till they aromatic.", Category.VEGETARIAN),
                new Recipe("2", "mixed veg", 2, TestUtil.getIngredients("bell pepper", "potato", "mushroom"), "chop all the ingredients and stir it in kadai with oil and spices", Category.VEGETARIAN),
                new Recipe("3", "salmon with potato", 4, TestUtil.getIngredients("salmon", "potato"), "at 250 degree celcium grill the fish along with potato in the oven", Category.NON_VEGETARIAN)
            ));
        recipes.forEach(recipe -> restTemplate.postForEntity(getRootUrl() + "/recipes", recipe, Recipe.class));
		restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
	public void testGetAllRecipes() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/recipes",
				HttpMethod.GET, entity, String.class);
		assertNotNull(response.getBody());
	}

    @Test
	public void testGetRecipeById() {
		Recipe recipe = restTemplate.getForObject(getRootUrl() + "/recipes/1", Recipe.class);
		assertNotNull(recipe);
	}

	@Test
	public void testGetFilteredRecipe_AllVegetarian() {
		List<LinkedHashMap<String, Object>> recipes = restTemplate.getForObject(getRootUrl() + "/recipes?category=VEGETARIAN", List.class);
		assertNotNull(recipes);
		recipes.forEach(recipe -> {
			assertEquals(recipe.get("category"), "VEGETARIAN");
		});
	}

	@Test
	public void testGetFilteredRecipe_Servings4_IncludesPotato() {
		List<LinkedHashMap<String, Object>> recipes = restTemplate.getForObject(getRootUrl() + "/recipes?servings=4&&includes=potato", List.class);
		assertNotNull(recipes);
		assertEquals(recipes.size(), 1);
		assertTrue(((List<String>)recipes.get(0).get("ingredients")).contains("potato"));
		assertEquals(recipes.get(0).get("servings"), 4);
	}

	@Test
	public void testGetFilteredRecipe_IngredientsOven_ExcludeSalmon() {
		List<LinkedHashMap<String, Object>> recipes = restTemplate.getForObject(getRootUrl() + "/recipes?instructions=oven&&excludes=salmon", List.class);
		assertNotNull(recipes);
		recipes.forEach(recipe -> {
			assertTrue(((String)(recipes.get(0).get("instructions"))).contains("oven"));
			assertFalse(((List<String>)recipes.get(0).get("ingredients")).contains("salmon"));
		});
	}

    @Test
	public void testUpdateRecipe() {
		int id = 1;
		Recipe recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		recipe.setServings(4);
		restTemplate.put(getRootUrl() + "/recipes/" + id, recipe);
		Recipe updatedRecipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		assertNotNull(updatedRecipe);
        assertEquals(updatedRecipe.getServings(), Integer.valueOf(4));
	}

	@Test
	public void testCreateRecipe() {
        Recipe recipe = new Recipe("5","Fried egg with tomato", 2, TestUtil.getIngredients("potato"), "crack the egg on the pan with little oil.", Category.NON_VEGETARIAN );
		ResponseEntity<Recipe> postResponse = restTemplate.postForEntity(getRootUrl() + "/recipes", recipe, Recipe.class);
		assertNotNull(postResponse);
		assertNotNull(postResponse.getBody());
        assertEquals(postResponse.getBody().getCategory(), Category.NON_VEGETARIAN);
	}

    @Test
	public void testDeleteRecipe() {
		int id = 0;
		Recipe recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		assertNotNull(recipe);
		restTemplate.delete(getRootUrl() + "/recipes/" + id);
		try {
			recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		} catch (final HttpClientErrorException e) {
			assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
		}
	}

	@Test
	public void testMergePatchRecipe_forServings() {
		int id = 5;
		Recipe recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		recipe.setServings(4);

		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(PatchMediaType.APPLICATION_MERGE_PATCH);
        ResponseEntity<Void> updatedRecipe =  restTemplate.exchange("/recipes/"+id, HttpMethod.PATCH, new HttpEntity<>(TestUtil.fromFile("merge-patch.json"), headers), Void.class, id);

		assertNotNull(updatedRecipe);
		recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		assertEquals(recipe.getServings(), Integer.valueOf(4));
	}

	@Test
	public void testPatchRecipe_forCategory() {
		int id = 5;
		Recipe recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);

		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(PatchMediaType.APPLICATION_JSON_PATCH);
        ResponseEntity<Void> updatedRecipe =  restTemplate.exchange("/recipes/"+id, HttpMethod.PATCH, new HttpEntity<>(TestUtil.fromFile("json-patch.json"), headers), Void.class, id);

		assertNotNull(updatedRecipe);
		recipe = restTemplate.getForObject(getRootUrl() + "/recipes/" + id, Recipe.class);
		assertEquals(recipe.getCategory(), Category.VEGETARIAN);
	}

	@SneakyThrows
	private String fromFile(String path) {
        return StreamUtils.copyToString(new ClassPathResource(path).getInputStream(), Charset.defaultCharset());
    }
}