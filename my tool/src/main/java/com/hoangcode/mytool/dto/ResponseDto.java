package com.hoangcode.mytool.dto;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.validation.BindingResult;


public class ResponseDto<T> {

  /**
   * Status of processing <br/>
   * <code>true</code>: OK, <code>false</code>: NG
   */
  @JsonView
  private boolean success;

  /**
   * Output data in the case of processing is successful
   */
  @JsonView
  private T response;

  public ResponseDto() {
  }

  public ResponseDto(boolean success, T response) {
    this.success = success;
    this.response = response;
  }

  public ResponseDto(boolean success) {
    this(success, null);
  }


  /**
   * Constructor for failing processing
   *
   * @param bindingResult Error
   */
  public ResponseDto(BindingResult bindingResult) {

    this.success = false;
    this.response = null;
  }


  public static <T> ResponseDto<T> success(T object) {
    return new ResponseDto(true, object);
  }

  public static <T> ResponseDto<T> success() {
    return new ResponseDto(true, (T) null);
  }

  public static <T> ResponseDto<T> error(String messageCd, String message) {
    return new ResponseDto(false, null);
  }


  @Override
  public String toString() {
    return "ResponseDto{" +
        "success=" + success +
        ", response=" + response +
        "}";
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public T getResponse() {
    return response;
  }

  public void setResponse(T response) {
    this.response = response;
  }
}
