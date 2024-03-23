package com.simultan.team.routing.model;

import com.simultan.team.libraries.model.rest.BaseResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
public class ProfileResponse extends BaseResponse {

  private String name;
  private int age;
}
