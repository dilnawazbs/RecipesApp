package com.abn.recipes.utils;

import com.abn.recipes.domain.Recipe;
import com.abn.recipes.domain.RecipeDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RecipeMapper {
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget Recipe recipe, Recipe recipeInput);

  List<Recipe> map(List<RecipeDTO> recipes);

  Recipe asInput(Recipe recipe);

  RecipeDTO asRecipeDTO(Recipe resourceInput);

  Recipe asRecipe(RecipeDTO recipeDTO);
}
