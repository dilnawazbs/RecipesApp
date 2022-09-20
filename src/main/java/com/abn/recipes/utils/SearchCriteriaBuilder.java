package com.abn.recipes.utils;

import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.QRecipeDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.util.MultiValueMap;

/**
 * This class is used to build all the queries passed as parameters.
 */
public class SearchCriteriaBuilder {

  /**
   * Creates the query criteria for the given search map.
   *
   * @param searchMap the filtered query from URI
   * @return the {@link Predicate} the search predicate for the given filters map
   */
  public static Predicate addCondition(MultiValueMap<String, String> searchMap) {
    BooleanBuilder queryBuilder = new BooleanBuilder();
    QRecipeDTO qRecipe = new QRecipeDTO("recipe");
    searchMap.forEach((key, value) -> {
        switch (key) {
          case "servings":
            queryBuilder.and(qRecipe.servings.eq(Integer.parseInt(value.get(0))));
            break;
          case "includes":
            value.forEach(tempVal -> queryBuilder.and(qRecipe.ingredients.contains(tempVal)));
            break;
          case "excludes":
            value.forEach(tempVal -> queryBuilder.andNot(qRecipe.ingredients.contains(tempVal)));
            break;
          case "instructions":
            value.forEach(tempVal -> queryBuilder.and(qRecipe.instructions.containsIgnoreCase(tempVal)));
            break;
          case "title":
            value.forEach(tempVal -> queryBuilder.and(qRecipe.title.containsIgnoreCase(tempVal)));
            break;
          case "category":
            queryBuilder.and(qRecipe.category.eq(Category.valueOf(value.get(0))));
            break;
          default:
        }
      }
    );
    return queryBuilder.getValue();
  }
}
