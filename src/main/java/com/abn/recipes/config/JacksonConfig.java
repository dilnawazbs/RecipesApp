package com.abn.recipes.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
      .setDefaultPropertyInclusion(Include.NON_NULL)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .findAndRegisterModules();
  }
}
