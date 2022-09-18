package com.abn.recipes.repositories;

import com.abn.recipes.domain.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Interface for generic CRUD operations on a Mongo repository.
 */
@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String>, QuerydslPredicateExecutor<Recipe> {}
