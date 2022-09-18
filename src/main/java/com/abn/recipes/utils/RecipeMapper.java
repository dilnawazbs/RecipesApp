package com.abn.recipes.utils;

import com.abn.recipes.domain.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RecipeMapper {
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget Recipe recipe, Recipe recipeInput);

  Recipe asInput(Recipe recipe);
}
