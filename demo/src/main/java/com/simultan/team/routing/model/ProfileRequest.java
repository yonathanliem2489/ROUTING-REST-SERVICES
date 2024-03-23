package com.simultan.team.routing.model;

import com.simultan.team.libraries.model.rest.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest extends BaseRequest {

  @NotBlank
  private String name;

  @NotNull
  private Integer age;
}
