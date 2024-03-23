package com.simultan.team.libraries.model.rest;

import static java.time.ZonedDateTime.now;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Value;

@Value
@SuppressWarnings("serial")
public class ServiceResponse<P> implements Serializable {
  public static final String DEFAULT_SUCCESS_CODE = "SUCCESS";
  public static final String DEFAULT_SUCCESS_MESSAGE = "Success";

  public static final String DEFAULT_FAILURE_CODE = "FAILED";
  public static final String DEFAULT_FAILURE_MESSAGE = "Failed";

  String code;
  String message;
  List<String> errors;

  @JsonInclude(Include.NON_NULL)
  String traces;

  @JsonProperty("data")
  P payload;

  @JsonProperty("serverTime")
  @JsonFormat(shape = Shape.NUMBER_INT)
  ZonedDateTime timestamp;

  @JsonCreator
  @lombok.Builder(builderClassName = "Builder")
  ServiceResponse(@JsonProperty String code, @JsonProperty String message,
      @JsonProperty List<String> errors, @JsonProperty String traces, @JsonProperty("data") P payload,
      @JsonProperty("serverTime") @JsonFormat(shape = Shape.NUMBER_INT) ZonedDateTime timestamp) {
    this.code = code;
    this.message = message;

    if(!isEmpty(errors)) {
      errors = Collections.unmodifiableList(errors);
    }
    this.errors = errors;

    this.traces = traces;
    this.payload = payload;

    if(isNull(timestamp)) {
      timestamp = now();
    }
    this.timestamp = timestamp;
  }

  /**
   * Build success response.
   *
   * @param payload
   * @param <P>
   * @return
   */
  public static <P> ServiceResponse<P> success(P payload) {
    return ServiceResponse.<P>builder()
        .code(DEFAULT_SUCCESS_CODE).message(DEFAULT_SUCCESS_MESSAGE).payload(payload)
        .timestamp(now())
        .build();
  }

  /**
   * Build failed response.
   *
   * @param code
   * @param message
   * @param <P>
   * @return
   */
  public static <P> ServiceResponse<P> failed(String code, String message) {
    return failed(code, message, null);
  }

  /**
   * Build failed response with optional errors descriptor.
   *
   * @param code
   * @param message
   * @param errors
   * @param <P>
   * @return
   */
  public static <P> ServiceResponse<P> failed(String code, String message, List<String> errors) {
    return failed(code, message, errors, null);
  }

  /**
   * Build failed response with optional errors descriptor and error stack traces.
   *
   * @param code
   * @param message
   * @param errors
   * @param traces
   * @param <P>
   * @return
   */
  public static <P> ServiceResponse<P> failed(String code, String message, List<String> errors, String traces) {
    return ServiceResponse.<P>builder()
        .code(hasText(code) ? code.toUpperCase() : DEFAULT_FAILURE_CODE)
        .message(hasText(message) ? message : DEFAULT_FAILURE_MESSAGE)
        .errors(errors).traces(traces).timestamp(now())
        .build();
  }
}
