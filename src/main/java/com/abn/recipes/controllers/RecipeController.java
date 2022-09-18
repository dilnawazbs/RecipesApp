package com.abn.recipes.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abn.recipes.domain.Recipe;
import com.abn.recipes.exception.ResourceNotFoundException;
import com.abn.recipes.services.RecipeService;
import com.abn.recipes.utils.PatchHelper;
import com.abn.recipes.utils.PatchMediaType;
import com.abn.recipes.utils.RecipeMapper;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeMapper mapper;
    @Autowired
    private PatchHelper patchHelper;

    /**
     * Gets the recipes for given id.
     * 
     * @param recipeId the recipeId
     * @return the {@link Recipe} 
     * @throws ResourceNotFoundException
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipesById(@PathVariable(value = "id") String recipeId) throws ResourceNotFoundException {
        return ResponseEntity.ok(recipeService.findRecipeById(recipeId));
    }

    /**
     * Gets all the recipes including filters.
     * 
     * @param filters the params map to filter
     * @return the list of {@link Recipe}
     */
    @GetMapping()
    public ResponseEntity<List<Recipe>> getRecipes(@RequestParam MultiValueMap<String, String> filters) {
        List<Recipe> recipes = new ArrayList<>();
        recipes.addAll(filters.isEmpty() ? recipeService.getAllRecipes() : recipeService.getFilteredRecipe(filters));
        return recipes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(recipes);
    } 

    /**
     * Creates the new recipe.
     * 
     * @param recipe {@link Recipe}
     * @return created {@link Recipe}
     */
    @PostMapping()
	public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        return new ResponseEntity<>(recipeService.save(recipe), HttpStatus.CREATED);
	}

    /**
     * Updates the {@link Recipe}.
     * 
     * @param recipeId the {@link Recipe} id
     * @param recipeDetails the {@link Recipe} details
     * @return the success response with updated {@link Recipe}
     * @throws ResourceNotFoundException
     */
	@PutMapping("/{id}")
	public ResponseEntity<Recipe> updateRecipe(@PathVariable(value = "id") String recipeId,
			@Valid @RequestBody Recipe recipeDetails) throws ResourceNotFoundException {
		Recipe recipe = recipeService.findRecipeById(recipeId);
        mapper.update(recipe, recipeDetails);
        return ResponseEntity.ok(recipeService.save(recipe));
	}

    /**
     * Updates the {@link Recipe} using patch.
     * 
     * @param recipeId the {@link Recipe} id
     * @param recipeDetails the recipePatch to update
     * @return the success response.
     * @throws ResourceNotFoundException
     */
	@PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_JSON_PATCH_VALUE)
	public ResponseEntity<Void> updateRecipe(@PathVariable(value = "id") String recipeId, @RequestBody JsonPatch recipePatch) throws ResourceNotFoundException {
		Recipe recipe = recipeService.findRecipeById(recipeId);
        Recipe recipeResource = mapper.asInput(recipe);
        Recipe patchedRecipeResource = patchHelper.patch(recipePatch, recipeResource, Recipe.class);
        mapper.update(recipe, patchedRecipeResource);
        recipeService.save(recipe);
        return ResponseEntity.noContent().build();
	}

     /**
     * Updates the {@link Recipe} using merge patch.
     * 
     * @param recipeId the {@link Recipe} id
     * @param recipeDetails the recipePatch to merge
     * @return the success response.
     * @throws ResourceNotFoundException
     */
    @PatchMapping(path = "/{id}", consumes = PatchMediaType.APPLICATION_MERGE_PATCH_VALUE)
    public ResponseEntity<Void> updateContact(@PathVariable(value = "id") String recipeId,
                                              @RequestBody JsonMergePatch recipePatch) throws ResourceNotFoundException {
        Recipe recipe = recipeService.findRecipeById(recipeId);
        Recipe recipeResource = mapper.asInput(recipe);
        Recipe patchedRecipeResource = patchHelper.mergePatch(recipePatch, recipeResource, Recipe.class);

        mapper.update(recipe, patchedRecipeResource);
        recipeService.save(recipe);

        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the {@link Recipe} for given recipe id.
     * 
     * @param recipeId  the recipeId to delete
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRecipe(@PathVariable(value = "id") String recipeId) {
        recipeService.deleteRecipeById(recipeId);
		return ResponseEntity.noContent().build();
	}

    /**
     * Deletes the all the {@link Recipe}.
     */
    @DeleteMapping()
    public ResponseEntity<Void> deleteAllRecipes() {
        recipeService.deleteAllRecipes();
		return ResponseEntity.noContent().build();
  }
}