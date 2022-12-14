package com.abn.recipes.utils;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

public class TestUtil {

  public static Set<String> getIngredients(String... ingredients) {
    Set<String> ingredientList = new HashSet<>();
    Stream.of(ingredients).forEach(ingredient -> ingredientList.add(ingredient));
    return ingredientList;
  }

  @SneakyThrows
  public static String fromFile(String path) {
    return StreamUtils.copyToString(new ClassPathResource(path).getInputStream(), Charset.defaultCharset());
  }
}
