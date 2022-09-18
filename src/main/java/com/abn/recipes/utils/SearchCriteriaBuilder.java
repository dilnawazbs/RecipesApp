package com.abn.recipes.utils;

import org.springframework.util.MultiValueMap;

import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.QRecipe;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

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
        QRecipe qRecipe = new QRecipe("recipe");
        searchMap.forEach((key, value) -> {
            switch(key) {
                case "servings": queryBuilder.and(qRecipe.servings.eq(Integer.parseInt(value.get(0))));
                    break;
                case "includes": queryBuilder.and(qRecipe.ingredients.contains(value.get(0)));
                    break;
                case "excludes": queryBuilder.andNot(qRecipe.ingredients.contains(value.get(0)));
                    break;
                case "instructions": queryBuilder.and(qRecipe.instructions.containsIgnoreCase(value.get(0)));
                    break;
                case "title": queryBuilder.and(qRecipe.title.containsIgnoreCase(value.get(0)));
                    break;
                case "category": queryBuilder.and(qRecipe.category.eq(Category.valueOf(value.get(0))));  
                    break;
                default: 
            }
        });
        return queryBuilder.getValue();
    }
}