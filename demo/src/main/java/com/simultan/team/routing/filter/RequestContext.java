package com.simultan.team.routing.filter;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class RequestContext implements Serializable {

  public static final String LANG = "lang";

  private String lang;

  @lombok.Builder(builderClassName = "Builder")
  public RequestContext(String lang) {
    this.lang = lang;
  }
}