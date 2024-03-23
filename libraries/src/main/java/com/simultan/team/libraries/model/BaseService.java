package com.simultan.team.libraries.model;

import com.simultan.team.libraries.model.rest.BaseRequest;
import com.simultan.team.libraries.model.rest.BaseResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public abstract class BaseService<TBaseRequest extends BaseRequest, TBaseResponse extends BaseResponse> {

  @Valid
  protected TBaseRequest request;

  protected TBaseResponse response;

  public abstract Mono<TBaseResponse> perform(TBaseRequest request);

  public Mono<TBaseResponse> call(TBaseRequest baseRequest) {
    return Mono.just(baseRequest)
        .flatMap(this::beforePerform)
        .flatMap(this::perform)
        .map(this::storeResponse)
        .flatMap(this::afterPerform)
        .map(this::storeResponse);
  }

  protected Mono<TBaseRequest> beforePerform(TBaseRequest request) {
    return Mono.just(request)
        .flatMap(this::validateRequest);
  }
  protected Mono<TBaseRequest> validateRequest(TBaseRequest request) {
    return Mono.just(request);
  }

  protected Mono<TBaseResponse> afterPerform(TBaseResponse response) {
    return Mono.just(response);
  }

  protected TBaseResponse storeResponse(TBaseResponse response) {
    this.response = response;
    return this.response;
  }
}
