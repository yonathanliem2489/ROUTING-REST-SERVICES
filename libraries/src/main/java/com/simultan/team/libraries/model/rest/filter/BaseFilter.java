package com.simultan.team.libraries.model.rest.filter;

import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

public abstract class BaseFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
}
