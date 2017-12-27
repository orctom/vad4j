package com.orctom.vad4j.exception;

public class Bytes {

  public static short[] toShortArray(byte[] bytes) {
    int len = bytes.length / 2;
    short[] shorts = new short[len];
    for (int i = 0; i < len; ++i) {
      shorts[i] = (short) (((bytes[i * 2 + 1] & 0xff) << 8) | (bytes[i * 2] & 0xff));
    }
    return shorts;
  }
}
