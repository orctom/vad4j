package com.orctom.vad4j.exception;

public class VADException extends RuntimeException {
  public VADException(String message) {
    super(message);
  }

  public VADException(String message, Throwable cause) {
    super(message, cause);
  }

  public VADException(Throwable cause) {
    super(cause);
  }
}
