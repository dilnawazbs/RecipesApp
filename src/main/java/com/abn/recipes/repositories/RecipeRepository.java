package com.abn.recipes.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.abn.recipes.domain.Recipe;

/**
 * Interface for generic CRUD operations on a Mongo repository.
 */
@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String>, QuerydslPredicateExecutor<Recipe> {
}

