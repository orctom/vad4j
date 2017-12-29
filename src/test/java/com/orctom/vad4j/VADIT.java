package com.orctom.vad4j;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class VADIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(VADIT.class);

  @Test
  public void isVoice() throws Exception {
    InputStream in = getClass().getResourceAsStream("/sample.pcm");
    byte[] bytes = ByteStreams.toByteArray(in);
    LongAdder counter = new LongAdder();
    ExecutorService es = Executors.newFixedThreadPool(10);
    final int chunkSize = 1775;
    for (int i = 0; i < 10_000; i++) {
      es.submit(() -> {
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
            LOGGER.info("vad isSilent: {}, took: {}", score, stopwatch);
          }
        }
      });
    }
    es.awaitTermination(5, TimeUnit.MINUTES);
    es.shutdown();
    es.shutdownNow();
    System.out.println("finished, processed: #" + counter.toString());
  }
}
