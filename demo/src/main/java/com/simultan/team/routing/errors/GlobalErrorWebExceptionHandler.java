package com.simultan.team.routing.errors;

import static com.simultan.team.libraries.model.rest.ServiceResponse.failed;

import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@AutoConfigureBefore({ErrorWebFluxAutoConfiguration.class, WebFluxAutoConfiguration.class})
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  public GlobalErrorWebExceptionHandler(GlobalErrorAttributes errorAttributes,
      ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
    super(errorAttributes, new Resources(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }



  // constructors

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
      ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
          RequestPredicates.all(), this::renderErrorResponse);
    }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Map<String, Object> error = getErrorAttributes(request, ErrorAttributeOptions.defaults());
    return ServerResponse.status(getHttpStatus(error))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(failed(getResponseCode(error),
            getResponseMessage(error), null, getStackTraces(error))
        );
  }

  private String getResponseCode(Map<String, Object> attributes) {
    return (String) attributes.get("code");
  }

  private int getHttpStatus(Map<String, Object> attributes) {
    return (int) attributes.get("status");
  }

  private String getResponseMessage(Map<String, Object> attributes) {
    return (String) attributes.get("message");
  }

  private String getStackTraces(Map<String, Object> attributes) {
    return (String) attributes.get("trace");
  }
}
