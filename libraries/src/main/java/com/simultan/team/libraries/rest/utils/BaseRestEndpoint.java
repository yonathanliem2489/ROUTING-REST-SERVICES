package com.simultan.team.libraries.rest.utils;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

import com.simultan.team.libraries.model.BaseService;
import com.simultan.team.libraries.model.rest.BaseRequest;
import com.simultan.team.libraries.model.rest.BlankRequest;
import com.simultan.team.libraries.model.rest.ServiceResponse;
import com.simultan.team.libraries.rest.properties.RoutingRestProperties;
import com.simultan.team.libraries.rest.properties.RoutingRestProperties.RestEndpoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@EnableConfigurationProperties(RoutingRestProperties.class)
public abstract class BaseRestEndpoint {


  protected RequestPredicate resolvePredicate(RestEndpoint restEndpoint) {

    RequestPredicate requestPredicate = RequestPredicates.method(restEndpoint.getHttpMethod())
        .and(RequestPredicates.path(restEndpoint.getPath()));
    if(Objects.nonNull(restEndpoint.getAccept())) {
      requestPredicate = requestPredicate.and(accept(MediaType.valueOf(restEndpoint.getAccept())));
    }
    if(Objects.nonNull(restEndpoint.getContentType())) {
      requestPredicate = requestPredicate.and(accept(MediaType.valueOf(restEndpoint.getContentType())));
    }

    if(Objects.nonNull(restEndpoint.getMandatoryHeaders())) {
      requestPredicate = requestPredicate.and(
          RequestPredicates.headers(header -> {
            AtomicBoolean result = new AtomicBoolean(true);
            restEndpoint.getMandatoryHeaders().forEach((key, value) -> {
              if (Objects.nonNull(header.firstHeader(key))) {
                if (!value.isEmpty() && !Objects.requireNonNull(header.firstHeader(key))
                    .equalsIgnoreCase(value)) {
                  result.set(false);
                }
              } else {
                result.set(false);
              }
            });

            return result.get();
          })
      );
    }

    return requestPredicate;
  }

  protected  <T> T handleInstance(String packageClass, ApplicationContext context, Class<T> tClass) {
    Class<?> handlerClass;
    try {
      handlerClass = Class.forName(packageClass);
    } catch (ClassNotFoundException e) {
      log.error("error define handler, message {}", e.getMessage());
      throw new RuntimeException(e);
    }
    return  tClass.cast(context.getBean(handlerClass));
  }

  protected  <T> T resolveBaseRequest(String packageClass, Class<T> tClass) {
    if(Objects.nonNull(packageClass)) {
      T handlerRequest;
      try {
        Class<?> requestClass = Class.forName(packageClass);
        Constructor<?> constructorRequest = requestClass.getConstructor();
        handlerRequest = tClass.cast(constructorRequest.newInstance());
      } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
               InvocationTargetException e) {
        log.error("error define request, message {}", e.getMessage());
        throw new RuntimeException(e);
      }

      return handlerRequest;
    }

    return tClass.cast(new BlankRequest());
  }

  protected Mono<ServerResponse> resolveHandler(BaseService baseService, Class<? extends BaseRequest> baseRequestClass,
      ServerRequest serverRequest, String path, SmartValidator objectValidator) {
    Map<String, Object> headers = mapHeadersToHashMap(serverRequest);
    Map<String, Object> params = mapParamsToHashMap(serverRequest);
    Map<String, Object> pathVariables = mapPathVariables(serverRequest, path);


    if(Objects.equals(serverRequest.method(), HttpMethod.GET)) {
      BlankRequest blankRequest = new BlankRequest();
      blankRequest.setHeaders(headers);
      blankRequest.setParams(params);
      blankRequest.setPathVariables(pathVariables);
      return baseService.call(blankRequest)
          .flatMap(response -> status(HttpStatus.OK).bodyValue(ServiceResponse.success(response)));
    }

    return serverRequest.bodyToMono(baseRequestClass)
        .flatMap(request -> validateBodyRequest(request, objectValidator))
        .flatMap(request -> {
          request.setParams(params);
          request.setHeaders(headers);
          request.setPathVariables(pathVariables);
          return baseService.call(request)
              .flatMap(response -> status(HttpStatus.OK)
                  .bodyValue(ServiceResponse.success(response)));
        });
  }

  private Mono<BaseRequest> validateBodyRequest(BaseRequest request, SmartValidator objectValidator) {
    BindingResult errors = new BeanPropertyBindingResult(request, "baseRequest");
    objectValidator.validate(request, errors);
    if(errors.hasErrors()) {
      log.error("error binding request, message {}", errors);
      return Mono.error(new BindException(errors));
    }

    return Mono.just(request);
  }

  private Map<String, Object> mapPathVariables(ServerRequest serverRequest, String path) {
    Map<String, Object> segmentMap = new HashMap<>();

    Pattern pattern = Pattern.compile("\\{(.*?)\\}");
    Matcher matcher = pattern.matcher(path);
    while (matcher.find()) {
      String segment = matcher.group(1);
      String[] parts = segment.split("/");

      String key = parts[0];
      Object value = serverRequest.pathVariable(key);
      segmentMap.put(key, value);
    }

    return segmentMap;
  }

  private Map<String, Object> mapParamsToHashMap(ServerRequest serverRequest) {
    return serverRequest.queryParams().entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().size() > 1 ? entry.getValue() : entry.getValue().get(0)));
  }

  public static Map<String, Object> mapHeadersToHashMap(ServerRequest serverRequest) {
    HttpHeaders httpHeaders = serverRequest.headers().asHttpHeaders();
    Map<String, Object> headerMap = new HashMap<>();
    httpHeaders.forEach((key, values) -> {
      if (!values.isEmpty()) {
        headerMap.put(key, values.get(0));
      }
    });

    return headerMap;
  }
}
