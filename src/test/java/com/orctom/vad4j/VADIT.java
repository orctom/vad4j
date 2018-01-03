package com.orctom.vad4j;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class VADIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(VADIT.class);

  @Test
  public void isVoice() throws Exception {
    LongAdder counter = new LongAdder();
    ExecutorService es = Executors.newFixedThreadPool(30);
    final int chunkSize = 1775;
    final int length = 23432523;
    for (int i = 0; i < 10_000; i++) {
      es.submit(() -> {
        int startIndex = 0;
        int endIndex = chunkSize;
        Random random = new Random();
        try (VAD vad = new VAD()) {
          while (startIndex < length) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            if (endIndex > length) {
              endIndex = length;
            }
            int size = endIndex - startIndex;
            byte[] pcm = new byte[size];
            random.nextBytes(pcm);

            float score = vad.speechProbability(pcm);

            counter.increment();
            startIndex += chunkSize;
            endIndex += chunkSize;
            LOGGER.info("vad isSilent: {}, took: {}", score, stopwatch);
          }
        }
      });
    }
    TimeUnit.SECONDS.sleep(600);
    es.shutdown();
    es.awaitTermination(5, TimeUnit.MINUTES);
    es.shutdownNow();
    System.out.println("finished, processed: #" + counter.toString());
  }
}
