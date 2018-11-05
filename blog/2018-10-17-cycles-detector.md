# Cycles Detector

The `CyclesDetector` tool helps you finding cyclic dependencies within your
Java packages inside a JAR file.

## Programmatic Usage

```java
var detector = new CyclesDetector(jar);
var result = detector.run(Configuration.of());
assertEquals(0, result.getExitCode(), "result=" + result);
```

https://search.maven.org/artifact/de.sormuras/bartholdy
