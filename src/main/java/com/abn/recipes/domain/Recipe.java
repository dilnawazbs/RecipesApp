package com.abn.recipes.domain;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Recipes")
public class Recipe {
    @Id
    private String id;
    private String title;
    private Integer servings;
    private Set<String> ingredients;
    private String instructions;
    private Category category;
}
