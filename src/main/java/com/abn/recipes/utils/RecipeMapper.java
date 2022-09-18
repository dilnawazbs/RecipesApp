package com.abn.recipes.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.abn.recipes.domain.Recipe;

@Mapper
public interface RecipeMapper {
    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Recipe recipe, Recipe resourceInput);
    Recipe asInput(Recipe recipe);
}