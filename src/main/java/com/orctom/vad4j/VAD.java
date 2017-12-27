package com.orctom.vad4j;

import com.orctom.vad4j.exception.Bytes;
import com.orctom.vad4j.exception.VADException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class VAD implements Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(VAD.class);

  public static final float THRESHOLD = 0.6F;
  private static final int CODE_SUCCESS = 0;

  private Pointer state;

  static {
    Native.setProtected(true);
  }

  public VAD() {
    LOGGER.info("create vad");
    state = Detector.INSTANCE.create_kika_vad_detector();
    int result = Detector.INSTANCE.init_kika_vad_detector(state);
    if (CODE_SUCCESS != result) {
      throw new VADException("Failed to init VAD");
    }
    LOGGER.info("created vad");
  }

  public float speechProbability(byte[] pcm) {
    LOGGER.info("speechProbability");
    if (null == pcm || pcm.length < 320) {
      LOGGER.info("speechProbability not a frame");
      return 0F;
    }

    short[] frame = Bytes.toShortArray(pcm);
    LOGGER.info("speechProbability byte[] -> short[]");
    float score = Detector.INSTANCE.process_kika_vad_prob(state, frame, frame.length);
    LOGGER.info("speechProbability score: {}", score);
    LOGGER.trace("score: {}", score);
    return score;
  }

  public boolean isSpeech(byte[] pcm) {
    return speechProbability(pcm) >= THRESHOLD;
  }

  public boolean isSilent(byte[] pcm) {
    return speechProbability(pcm) < THRESHOLD;
  }

  @Override
  public void close() {
    LOGGER.info("vad close");
    Detector.INSTANCE.destroy_kika_vad_detector(state);
  }

  public interface Detector extends Library {

    Detector INSTANCE = Native.loadLibrary("kvad", Detector.class);

    Pointer create_kika_vad_detector();

    int init_kika_vad_detector(Pointer vadDetector);

    int reset_kika_vad_detector(Pointer vadDetector);

    /**
     * 1: voice
     * 0: no voice
     * <0: crash
     */
    int process_kika_vad(Pointer state, short[] frame, int length);

    float process_kika_vad_prob(Pointer state, short[] frame, int length);

    void destroy_kika_vad_detector(Pointer vadDetector);

  }
}
