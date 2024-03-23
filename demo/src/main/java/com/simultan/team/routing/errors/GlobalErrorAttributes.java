package com.simultan.team.routing.errors;

import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;

@Component
public class GlobalErrorAttributes implements ErrorAttributes {
  private static final String ERROR_ATTRIBUTE = GlobalErrorAttributes.class.getName() + ".ERROR";

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request,
      ErrorAttributeOptions options) {
    Map<String, Object> attributes = new LinkedHashMap<>();
    attributes.put("timestamp", new Date());
    attributes.put("path", request.path());
    Throwable error = getError(request);

    MergedAnnotation<ResponseStatus> statusAnnotation = MergedAnnotations
        .from(error.getClass(), SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);

    HttpStatus httpStatus = determineHttpStatus(error, statusAnnotation);
    attributes.put("status", httpStatus.value());

    attributes.put("error", httpStatus.getReasonPhrase());

    if(options.isIncluded(Include.EXCEPTION)) {
      attributes.put("exception", error.getClass());
    }

    return attributes;
  }

  private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> statusAnnotation) {
    if (error instanceof ConstraintViolationException || error instanceof BindException) {
      return HttpStatus.BAD_REQUEST;
    }
    if (error instanceof NoResourceFoundException) {
      return HttpStatus.NOT_FOUND;
    }

    return statusAnnotation.getValue("code", HttpStatus.class)
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public Throwable getError(ServerRequest request) {
    return (Throwable) request.attribute(ERROR_ATTRIBUTE)
        .orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
  }

  @Override
  public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
    exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
  }
}
