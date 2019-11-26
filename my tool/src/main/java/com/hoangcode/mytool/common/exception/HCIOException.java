package com.hoangcode.mytool.common.exception;

public class HCIOException extends BaseException {


  private static final long serialVersionUID = 1L;
  
  public HCIOException() {
    super();
  }

  public HCIOException(String message) {
    super(message);
  }

  public HCIOException(String message, Throwable cause) {
    super(message, cause);
  }

  public HCIOException(Throwable cause) {
    super(cause);
  }

}
