package com.simultan.team.routing.services;


import com.simultan.team.libraries.model.BaseService;
import com.simultan.team.routing.model.ProfileRequest;
import com.simultan.team.routing.model.ProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CreateProfileService extends BaseService<ProfileRequest, ProfileResponse> {

  @Override
  public Mono<ProfileResponse> perform(ProfileRequest request) {
    log.info("header {}", request.getHeaders());
    log.info("params {}", request.getParams());
    log.info("pathVariables {}", request.getPathVariables());
    return Mono.just(ProfileResponse.builder()
            .name(request.getName())
            .age(request.getAge())
        .build());
  }
}