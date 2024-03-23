package com.simultan.team.routing.configuration;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.simultan.team.libraries.model.BaseService;
import com.simultan.team.libraries.model.rest.BaseRequest;
import com.simultan.team.libraries.model.rest.filter.BaseFilter;
import com.simultan.team.libraries.rest.properties.RoutingRestProperties;
import com.simultan.team.libraries.rest.utils.BaseRestEndpoint;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
public class RoutingRestConfiguration extends BaseRestEndpoint {

  @Autowired
  private ApplicationContext context;

  @Bean
  RouterFunction<ServerResponse> routingRestEndpoints(RoutingRestProperties routingRestProperties,
      SmartValidator objectValidator) {
    RouterFunctions.Builder routerFunctionBuilder = route();
    routingRestProperties.getRestEndpoints().forEach(restEndpoint -> {
      BaseService handlerInstance = handleInstance(restEndpoint.getServiceClass(), context, BaseService.class);
      BaseRequest baseRequest = resolveBaseRequest(restEndpoint.getRequestClass(), BaseRequest.class);
      RequestPredicate requestPredicate = resolvePredicate(restEndpoint);
      final RouterFunction<ServerResponse>[] routerFunction = new RouterFunction[]{
          route(requestPredicate, request ->
              resolveHandler(handlerInstance, baseRequest.getClass(), request, restEndpoint.getPath(), objectValidator))};
      if(Objects.nonNull(restEndpoint.getFilterClasses())) {
        restEndpoint.getFilterClasses().forEach(filter ->
            routerFunction[0] = routerFunction[0].filter(handleInstance(filter, context, BaseFilter.class))
        );
      }

      routerFunctionBuilder.add(routerFunction[0]);
    });

    return routerFunctionBuilder.build();
  }

}