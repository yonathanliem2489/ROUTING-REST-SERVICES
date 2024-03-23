package com.simultan.team.libraries.model.rest;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseRequest implements Serializable {

  private Map<String, Object> headers;
  private Map<String, Object> params;
  private Map<String, Object> pathVariables;
}
