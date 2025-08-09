//package com.simultan.team.routing;
//
//
//import com.simultan.team.routing.configuration.RoutingRestConfiguration;
//import com.simultan.team.routing.errors.GlobalErrorAttributes;
//import com.simultan.team.routing.errors.GlobalErrorWebExceptionHandler;
//import com.simultan.team.routing.filter.RequestContextFilter;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
//import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
//import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
//import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.WebProperties;
//import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration;
//import org.springframework.context.annotation.Import;
//
//@SpringBootConfiguration
//@ImportAutoConfiguration({ReactiveWebServerFactoryAutoConfiguration.class,
//    JacksonAutoConfiguration.class, CodecsAutoConfiguration.class, ValidationAutoConfiguration.class,
//    RedisReactiveAutoConfiguration.class, RedisAutoConfiguration.class,
//    ErrorWebFluxAutoConfiguration.class, WebFluxAutoConfiguration.class,
//    HttpHandlerAutoConfiguration.class, WebClientAutoConfiguration.class, WebTestClientAutoConfiguration.class,
//    ServletWebServerFactoryAutoConfiguration.class
//})
//@Import({
//    GlobalErrorAttributes.class,
//    RequestContextFilter.class,
//    RoutingRestConfiguration.class,
//    GlobalErrorWebExceptionHandler.class,
//})
//@EnableConfigurationProperties(WebProperties.class)
//public class RoutingRestTestingConfiguration {
//}
