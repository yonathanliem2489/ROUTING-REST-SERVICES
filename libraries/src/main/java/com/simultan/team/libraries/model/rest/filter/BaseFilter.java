package com.simultan.team.libraries.model.rest.filter;

import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public abstract class BaseFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  @Override
  public Mono<ServerResponse> filter(
      ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction) {
    return filterSection(serverRequest, handlerFunction);
  }

  public abstract Mono<ServerResponse> filterSection(
      ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction);
}
