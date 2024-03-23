package com.simultan.team.routing.configuration;

import com.simultan.team.libraries.model.OpenApiBaseConfiguration;
import com.simultan.team.libraries.rest.properties.RoutingRestProperties;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Slf4j
@Configuration
public class OpenApiConfiguration extends OpenApiBaseConfiguration {

  private static final String HEADER = "header";

  @Autowired
  private RoutingRestProperties routingRestProperties;

  @Bean
  public OpenApiCustomizer openApiCustomizer() {
      return getOpenApiCustom(routingRestProperties,
          buildHeaderParameters(), buildApiResponses());
  }

  private List<Parameter> buildHeaderParameters() {
    return List.of(
        new Parameter().name("example").in(HEADER)
          .schema(new StringSchema().example("example"))
          .required(true)
          .description("example mandatory header")
    );
  }

  private ApiResponses buildApiResponses() {
    ApiResponses apiResponses = new ApiResponses();
    apiResponses.addApiResponse(
        String.valueOf(HttpStatus.OK.value()),
        new ApiResponse().description("successful operation"));
    apiResponses.addApiResponse(
        String.valueOf(HttpStatus.BAD_REQUEST.value()),
        new ApiResponse().description("Bad request"));
    apiResponses.addApiResponse(
        String.valueOf(HttpStatus.UNAUTHORIZED.value()),
        new ApiResponse().description("Not authorized"));
    return apiResponses;
  }
}
