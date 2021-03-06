package com.orctom.vad4j;

import com.orctom.vad4j.exception.VADException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

public class VAD implements Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(VAD.class);

  public static final float THRESHOLD = 0.6F;

  private static final int CODE_SUCCESS = 0;
  private static final int PACKET_MIN_SIZE = 320;
  private static final int PACKET_SIZE_MS = 120;
  private static final int PACKET_SIZE = 3840;
  private static final int BOS_DELAY_MS = 400;
  private static final int EOS_DELAY_MS = 1000;

  private AtomicBoolean stopped = new AtomicBoolean(false);

  private Pointer state;

  public VAD() {
    this(PACKET_SIZE_MS, BOS_DELAY_MS, EOS_DELAY_MS);
  }

  public VAD(int maxPacketSizeMs, int bosDelayMs, int eosDelayMs) {
    state = Detector.INSTANCE.create_kika_vad_detector();
    int result = Detector.INSTANCE.init_kika_vad_detector(state, maxPacketSizeMs, bosDelayMs, eosDelayMs);
    if (CODE_SUCCESS != result) {
      throw new VADException("Failed to init VAD");
    }
  }

  public float speechProbability(byte[] pcm) {
    if (null == pcm || pcm.length < PACKET_MIN_SIZE || pcm.length > PACKET_SIZE) {
      return 0F;
    }

    short[] frame = Bytes.toShortArray(pcm);
    try {
      float score = Detector.INSTANCE.process_kika_vad_prob(state, frame, frame.length);
      LOGGER.trace("score: {}", score);
      return score;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return 0.0F;
    }
  }

  public boolean isSpeech(byte[] pcm) {
    return speechProbability(pcm) >= THRESHOLD;
  }

  public boolean isSilent(byte[] pcm) {
    return speechProbability(pcm) < THRESHOLD;
  }

  @Override
  public void close() {
    if (stopped.getAndSet(true)) {
      return;
    }

    LOGGER.info("closing VAD");
    Detector.INSTANCE.destroy_kika_vad_detector(state);
  }

  public interface Detector extends Library {

    Detector INSTANCE = Native.loadLibrary("kvad", Detector.class);

    Pointer create_kika_vad_detector();

    /**
     * @param vadDetector the pointer
     * @param packetSizeMs packet size in ms, 120 ms recommended
     * @param startDelayMs how long before the wave been treated as start of voice
     * @param stopDelayMs low long before the wave been treated as end of voice
     */
    int init_kika_vad_detector(Pointer vadDetector, int packetSizeMs, int startDelayMs, int stopDelayMs);

    int reset_kika_vad_detector(Pointer vadDetector);

    /**
     * 1: voice
     * 0: no voice
     * &lt;0: crash
     */
    int process_kika_vad(Pointer state, short[] frame, int length);

    float process_kika_vad_prob(Pointer state, short[] frame, int length);

    void destroy_kika_vad_detector(Pointer vadDetector);
  }
}
