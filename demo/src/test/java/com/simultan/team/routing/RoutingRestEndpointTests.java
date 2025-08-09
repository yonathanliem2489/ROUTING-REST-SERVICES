//package com.simultan.team.routing;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//import static org.springframework.core.ParameterizedTypeReference.forType;
//import static org.springframework.core.ResolvableType.forClassWithGenerics;
//
//import com.simultan.team.libraries.model.rest.ServiceResponse;
//import com.simultan.team.routing.RoutingRestEndpointTests.TestingConfiguration;
//import com.simultan.team.routing.model.ProfileRequest;
//import com.simultan.team.routing.model.ProfileResponse;
//import com.simultan.team.routing.services.CreateProfileService;
//import com.simultan.team.routing.services.GetProfileService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//@SpringBootTest(classes = RoutingRestTestingConfiguration.class, webEnvironment = RANDOM_PORT, properties = {
//    "routing.rest.restEndpoints[0].httpMethod=POST",
//    "routing.rest.restEndpoints[0].path=/routing-demo/test-post",
//    "routing.rest.restEndpoints[0].serviceClass=com.simultan.team.routing.services.CreateProfileService",
//    "routing.rest.restEndpoints[0].requestClass=com.simultan.team.routing.model.ProfileRequest",
//    "routing.rest.restEndpoints[0].filterClasses[0]=com.simultan.team.routing.filter.RequestContextFilter",
//    "routing.rest.restEndpoints[0].accept=application/json",
//    "routing.rest.restEndpoints[0].contentType=application/json",
//
//    "routing.rest.restEndpoints[1].httpMethod=GET",
//    "routing.rest.restEndpoints[1].path=/routing-demo/test-get/{id}",
//    "routing.rest.restEndpoints[1].serviceClass=com.simultan.team.routing.services.GetProfileService",
//    "routing.rest.restEndpoints[1].filterClasses[0]=com.simultan.team.routing.filter.RequestContextFilter",
//    "routing.rest.restEndpoints[1].accept=application/json",
//
//    "routing.rest.restEndpoints[2].httpMethod=POST",
//    "routing.rest.restEndpoints[2].path=/routing-demo/test-post/{id}",
//    "routing.rest.restEndpoints[2].serviceClass=com.simultan.team.routing.services.CreateProfileService",
//    "routing.rest.restEndpoints[2].requestClass=com.simultan.team.routing.model.ProfileRequest",
//    "routing.rest.restEndpoints[2].filterClasses[0]=com.simultan.team.routing.filter.RequestContextFilter",
//    "routing.rest.restEndpoints[2].accept=application/json",
//    "routing.rest.restEndpoints[2].contentType=application/json",
//})
//@AutoConfigureWebTestClient(timeout = "60000")
//@Import(value = TestingConfiguration.class)
//class RoutingRestEndpointTests {
//  private static final ParameterizedTypeReference<ServiceResponse<ProfileResponse>> PROFILE_RESPONSE_TYPE_REFERENCE =
//      forType(forClassWithGenerics(ServiceResponse.class, ProfileResponse.class).getType());
//
//  @TestConfiguration
//  protected static class TestingConfiguration {
//    @Bean
//    CreateProfileService createProfileService() {
//      return new CreateProfileService();
//    }
//
//    @Bean
//    GetProfileService getProfileService() {
//      return new GetProfileService();
//    }
//  }
//
//  @Autowired
//  private WebTestClient testClient;
//
//
//  @Test
//  void whenGetRoutingWithPathVariable_thenShouldSuccess() {
//    ServiceResponse<ProfileResponse> response = testClient.get()
//        .uri(builder -> builder.path("/routing-demo/test-get/123")
//            .build())
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBody(PROFILE_RESPONSE_TYPE_REFERENCE)
//        .returnResult()
//        .getResponseBody();
//    assertEquals(20, response.getPayload().getAge());
//    assertEquals("andi", response.getPayload().getName());
//  }
//
//  @Test
//  void whenPost_thenShouldSuccess() {
//
//    ProfileRequest profileRequest = new ProfileRequest();
//    profileRequest.setName("yonathan");
//    profileRequest.setAge(25);
//
//    ServiceResponse<ProfileResponse> response = testClient.post()
//        .uri(builder -> builder.path("/routing-demo/test-post")
//            .build())
//        .bodyValue(profileRequest)
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBody(PROFILE_RESPONSE_TYPE_REFERENCE)
//        .returnResult()
//        .getResponseBody();
//    assertEquals(profileRequest.getAge(), response.getPayload().getAge());
//    assertEquals(profileRequest.getName(), response.getPayload().getName());
//  }
//
//  @Test
//  void whenPostWithPathVariable_thenShouldSuccess() {
//
//    ProfileRequest profileRequest = new ProfileRequest();
//    profileRequest.setName("yonathan");
//    profileRequest.setAge(25);
//
//    ServiceResponse<ProfileResponse> response = testClient.post()
//        .uri(builder -> builder.path("/routing-demo/test-post/123456")
//            .build())
//        .bodyValue(profileRequest)
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isOk()
//        .expectBody(PROFILE_RESPONSE_TYPE_REFERENCE)
//        .returnResult()
//        .getResponseBody();
//    assertEquals(profileRequest.getAge(), response.getPayload().getAge());
//    assertEquals(profileRequest.getName(), response.getPayload().getName());
//  }
//
//
//  @Test
//  void whenPost_thenShouldConstraintException() {
//
//    ProfileRequest profileRequest = new ProfileRequest();
//    profileRequest.setAge(25);
//
//    testClient.post()
//        .uri(builder -> builder.path("/routing-demo/test-post")
//            .build())
//        .bodyValue(profileRequest)
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
//  }
//
//  @Test
//  void whenGetRouting_thenShouldNotFound() {
//
//    testClient.get()
//        .uri(builder -> builder.path("/routing-demo/test-2")
//            .build())
//        .accept(MediaType.APPLICATION_JSON)
//        .exchange()
//        .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
//  }
//
//  @AfterEach
//  void tearDown() {}
//
//}
