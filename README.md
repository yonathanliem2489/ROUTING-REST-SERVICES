# ROUTING-REST-SERVICES
Routing Rest Endpoints


## Routing Rest Endpoints
### Background
Current conditions, when we start a new project. to create a new endpoint, we had to code in rest-web. In fact, with its low complexity, there is no need to do new coding for each endpoint creation, so we are trying to create a ``Routing Rest Endpoint'' feature to speed up the development process.

### What is served?
We try to support starting from unit tests, error handling and also documentation of this improvement, and this feature already supports it

### Installation
#### Create Route Configuration
```java
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
```

#### Create Open Api Configuration
```java

@Slf4j
@Configuration
public class OpenApiConfiguration extends OpenApiBaseConfiguration {

  private static final String HEADER = "header";

  @Autowired
  private RoutingRestProperties routingRestProperties;

  @Bean
  public OpenApiCustomizer openApiCustomizer() {
      return getOpenApiCustom(routingRestProperties,
          buildHeaderParameters(), buildApiResponses());
  }

  private List<Parameter> buildHeaderParameters() {
    return List.of(
        new Parameter().name("example").in(HEADER)
          .schema(new StringSchema().example("example"))
          .required(true)
          .description("example mandatory header")
    );
  }

  private ApiResponses buildApiResponses() {
    ApiResponses apiResponses = new ApiResponses();
    apiResponses.addApiResponse(
        String.valueOf(HttpStatus.OK.value()),
        new ApiResponse().description("successful operation"));
    apiResponses.addApiResponse(
        String.valueOf(HttpStatus.BAD_REQUEST.value()),
        new ApiResponse().description("Bad request"));
    apiResponses.addApiResponse(
        String.valueOf(HttpStatus.UNAUTHORIZED.value()),
        new ApiResponse().description("Not authorized"));
    return apiResponses;
  }
}
```

#### Added in properties
```
# springdoc config
springdoc.swagger-ui.path=/v3/api-docs

routing.rest.restEndpoints[0].httpMethod=POST
routing.rest.restEndpoints[0].path=/routing-demo/test-post
routing.rest.restEndpoints[0].serviceClass=com.simultan.team.routing.services.CreateProfileService
routing.rest.restEndpoints[0].requestClass=com.simultan.team.routing.model.ProfileRequest
routing.rest.restEndpoints[0].filterClasses[0]=com.simultan.team.routing.filter.RequestContextFilter
routing.rest.restEndpoints[0].accept=application/json
routing.rest.restEndpoints[0].contentType=application/json
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.operation-id=post-test
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[0].name=testParams
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[0].in=query
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[0].schema.example=test
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[0].schema.required=true
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[0].schema.description=test params
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[1].name=testHeaders
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[1].in=header
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[1].schema.example=test
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[1].schema.required=true
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.parameters[1].schema.description=test headers
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.requestBody.content[application/json].schema.name=example request
routing.rest.restEndpoints[0].pathDocumentations[/routing-demo/test-post].post.requestBody.content[application/json].schema.example={"name":"andi","age":30}

routing.rest.restEndpoints[1].httpMethod=GET
routing.rest.restEndpoints[1].path=/routing-demo/test-get
routing.rest.restEndpoints[1].serviceClass=com.simultan.team.routing.services.CreateProfileService
routing.rest.restEndpoints[1].filterClasses[0]=com.simultan.team.routing.filter.RequestContextFilter
routing.rest.restEndpoints[1].accept=application/json
routing.rest.restEndpoints[1].pathDocumentations[/routing-demo/test-get].get.operation-id=get-test


routing.rest.restEndpoints[2].httpMethod=POST
routing.rest.restEndpoints[2].path=/routing-demo/test-post/{id}
routing.rest.restEndpoints[2].serviceClass=com.simultan.team.routing.services.CreateProfileService
routing.rest.restEndpoints[2].requestClass=com.simultan.team.routing.model.ProfileRequest
routing.rest.restEndpoints[2].filterClasses[0]=com.simultan.team.routing.filter.RequestContextFilter
routing.rest.restEndpoints[2].accept=application/json
routing.rest.restEndpoints[2].contentType=application/json
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.operation-id=post-path-variable-test
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[0].name=testParams
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[0].in=query
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[0].schema.example=test
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[0].schema.required=true
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[0].schema.description=test params
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[1].name=testHeaders
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[1].in=header
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[1].schema.example=test
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[1].schema.required=true
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[1].schema.description=test headers
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[2].name=id
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[2].in=path
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[2].schema.example=123456
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[2].schema.required=true
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.parameters[2].schema.description=test path variable
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.requestBody.content[application/json].schema.name=example request
routing.rest.restEndpoints[2].pathDocumentations[/routing-demo/test-post/{id}].post.requestBody.content[application/json].schema.example={"name":"andi","age":30}

```

### Display in swagger
You can visit swagger to test the endpoint ``{{host}}/v3/webjars/swagger-ui/index.html``

### Testing
With a route endpoint, you don't need to create new tests to cover test cases at the controller level. unless the conditions you create are still not covered by the pre-existing unit tests. The positive case and negative case were already covered when this feature was created.