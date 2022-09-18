package com.abn.recipes.utils;


import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.abn.recipes.config.JacksonConfig;
import com.abn.recipes.domain.Category;
import com.abn.recipes.domain.Recipe;

import javax.json.*;
import javax.validation.Validator;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import({JacksonConfig.class, PatchHelper.class})
public class PatchHelperTest {

    @MockBean
    private Validator validator;

    @Autowired
    private PatchHelper patchHelper;

    @Test
    public void patch_shouldPatchDocument() {
        when(validator.validate(any())).thenReturn(Sets.newHashSet());
        Recipe target = Recipe.builder()
                .id("1")
                .title("Grilled potato")
                .servings(5)
                .ingredients(Sets.newHashSet(Lists.newArrayList("potatoes", "beans")))
                .instructions("Bake the potatoes in the oven at 250 degree celcius")
                .category(Category.VEGETARIAN)
                .build();
        JsonPatch patch = Json.createPatchBuilder()
                .replace("/title", "Grilled chicken potato")
                .replace("/servings", 6)
                .replace("/category", "NON_VEGETARIAN")
                .build();
        Recipe expected = Recipe.builder()
                .id("1")
                .title("Grilled chicken potato")
                .servings(6)
                .ingredients(Sets.newHashSet(Lists.newArrayList("potatoes", "beans")))
                .category(Category.NON_VEGETARIAN)
                .build();

        Recipe result = patchHelper.patch(patch, target, Recipe.class);
        assertTrue(result.getServings()==expected.getServings());
        assertEquals(result.getTitle(), expected.getTitle());
        assertEquals(result.getCategory(), expected.getCategory());
        verify(validator).validate(any());
    }

    @Test
    public void mergePatch_shouldMergePatchDocument() {
        when(validator.validate(any())).thenReturn(Sets.newHashSet());
        Recipe target = Recipe.builder()
                .id("1")
                .title("Grilled potato")
                .servings(5)
                .ingredients(Sets.newHashSet(Lists.newArrayList("potatoes", "beans")))
                .instructions("Bake the potatoes in the oven at 250 degree celcius")
                .category(Category.VEGETARIAN)
                .build();

        JsonMergePatch mergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("title", "Grilled chicken potato")
                .add("servings", 6)
                .add("ingredients", Json.createArrayBuilder().add("chicken"))
                .add("category", "NON_VEGETARIAN")
                .build());

        Recipe expected = Recipe.builder()
                .id("1")
                .title("Grilled chicken potato")
                .servings(6)
                .ingredients(Sets.newHashSet(Lists.newArrayList("chicken")))
                .category(Category.NON_VEGETARIAN)
                .build();

        Recipe result = patchHelper.mergePatch(mergePatch, target, Recipe.class);
        assertTrue(result.getServings()==expected.getServings());
        assertEquals(result.getTitle(), expected.getTitle());
        assertEquals(result.getCategory(), expected.getCategory());
        assertTrue(result.getIngredients().contains("chicken"));
        verify(validator).validate(any());
    }
}
