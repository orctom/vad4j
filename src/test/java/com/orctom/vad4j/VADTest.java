package com.orctom.vad4j;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.LongAdder;

public class VADTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(VADTest.class);

  @Test
  public void isVoice() throws Exception {
    InputStream in = getClass().getResourceAsStream("/pcm/kika_1.pcm");
    byte[] bytes = ByteStreams.toByteArray(in);
    LongAdder counter = new LongAdder();
    int chunkSize = 426;
    int startIndex = 0;
    int endIndex = chunkSize;
    try (VAD vad = new VAD()) {
      while (startIndex < bytes.length) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (endIndex > bytes.length) {
          endIndex = bytes.length;
        }
        byte[] pcm = Arrays.copyOfRange(bytes, startIndex, endIndex);
        float score = vad.speechProbability(pcm);

        counter.increment();
        startIndex += chunkSize;
        endIndex += chunkSize;
        LOGGER.info("vad : took: {}, score: {}", stopwatch, score);
      }
    }
    System.out.println("finished, processed: #" + counter.toString());
  }
}
