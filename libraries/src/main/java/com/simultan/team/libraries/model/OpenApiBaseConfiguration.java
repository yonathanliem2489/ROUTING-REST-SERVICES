package com.simultan.team.libraries.model;

import com.simultan.team.libraries.rest.properties.RoutingRestProperties;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.List;
import java.util.Objects;
import org.springdoc.core.customizers.OpenApiCustomizer;

public class OpenApiBaseConfiguration {

  public OpenApiCustomizer getOpenApiCustom(RoutingRestProperties routingRestProperties,
      List<Parameter> mandatoryParameters, ApiResponses apiResponses) {
    return openAPI -> {
      Paths paths = new Paths();
      routingRestProperties.getRestEndpoints()
          .stream().filter(restEndpoint -> Objects.nonNull(restEndpoint.getPathDocumentations()))
          .forEach(restEndpoint -> restEndpoint.getPathDocumentations().forEach((key, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
              if(Objects.nonNull(operation.getParameters())) {
                List<Parameter> parameters = new java.util.ArrayList<>(operation.getParameters());
                parameters.addAll(mandatoryParameters);
                operation.setParameters(parameters);
              }

              operation.setResponses(apiResponses);
            });

            paths.addPathItem(key, pathItem);
          }));
      openAPI.paths(paths);
    };
  }
}
