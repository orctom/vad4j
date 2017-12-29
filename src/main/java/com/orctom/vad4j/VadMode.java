package com.orctom.vad4j;

public enum VadMode {

  quality(1),
  low_bitrate(2),
  aggressive(3),
  very_aggressive(4);

  private int code;

  private VadMode(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
