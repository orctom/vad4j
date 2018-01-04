# vad4j
A Java wrapper for VAD (voice activity detector) of https://github.com/dpirch/libfvad.

## Usage

```
<dependency>
  <groupId>com.orctom</groupId>
  <artifactId>vad4j</artifactId>
  <version>1.0</version>
</dependency>
```

```java
// use built-in threshold
try (VAD vad = new VAD()) {
  boolean isSpeech = vad.isSpeech(pcm);
  LOGGER.info("is speech: {}", isSpeech);
}

// or use threshold of your choise
try (VAD vad = new VAD()) {
  float score = vad.speechProbability(pcm);
  boolean isSpeech = score >= VAD.THRESHOLD;
  LOGGER.info("is speech: {}", isSpeech);
}
```

*Notice*
* It's not thread-safe. Multiply instances of `VAD` should be created to calculate vad concurrently.
* Don't forget to `close()`
