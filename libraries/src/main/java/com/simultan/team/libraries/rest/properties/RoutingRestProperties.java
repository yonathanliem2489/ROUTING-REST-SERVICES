package com.simultan.team.libraries.rest.properties;

import io.swagger.v3.oas.models.Paths;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "routing.rest")
public class RoutingRestProperties {

    @Valid
    @NestedConfigurationProperty
    private List<RestEndpoint> restEndpoints;

    @Data
    public static class RestEndpoint {

        @NotNull
        private HttpMethod httpMethod;

        @NotBlank
        private String path;

        @NotBlank
        private String serviceClass;

        private String requestClass;

        private String accept;

        private String contentType;

        private List<String> filterClasses;

        private Map<String, String> mandatoryHeaders;

        private Paths pathDocumentations;
    }
}
