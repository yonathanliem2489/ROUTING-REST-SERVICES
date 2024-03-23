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
    return buildRouter(context, routingRestProperties, objectValidator);
  }

}