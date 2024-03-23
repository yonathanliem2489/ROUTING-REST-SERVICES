package com.simultan.team.routing.filter;

import com.simultan.team.libraries.model.rest.filter.BaseFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class RequestContextFilter extends BaseFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  @Override
  public Mono<ServerResponse> filter(ServerRequest serverRequest, HandlerFunction<ServerResponse> handlerFunction) {
    return Mono.fromCallable(() -> serverRequest.headers().asHttpHeaders())
        .flatMap(httpHeaders -> handlerFunction.handle(serverRequest)
            .contextWrite(context -> context.put(RequestContext.class, RequestContext.builder()
                    .lang(httpHeaders.getFirst(RequestContext.LANG))
                .build())
            )
        );
  }
}
