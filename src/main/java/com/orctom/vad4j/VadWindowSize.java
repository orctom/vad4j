package com.orctom.vad4j;

public enum VadWindowSize {
  _10ms(1),
  _20ms(2),
  _30ms(3);

  private int code;

  private VadWindowSize(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
