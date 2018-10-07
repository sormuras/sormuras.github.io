# Cycles Detector

https://search.maven.org/artifact/de.sormuras/bartholdy


```java
	@ParameterizedTest
	@MethodSource("platform.tooling.support.Helper#loadModuleDirectoryNames")
	void modules(String module) {
		var jar = Helper.createJarPath(module);
		var result = new CyclesDetector(jar).run(Configuration.of());

		switch (module) {
			case "junit-jupiter-api":
				assertEquals(1, result.getExitCode(), "result=" + result);
				assertLinesMatch(List.of(
					"org.junit.jupiter.api.extension.ExtensionContext -> org.junit.jupiter.api.TestInstance",
					"org.junit.jupiter.api.extension.ExtensionContext -> org.junit.jupiter.api.TestInstance$Lifecycle"),
					result.getOutputLines("err"));
				break;
			case "junit-jupiter-engine":
				assertEquals(1, result.getExitCode(), "result=" + result);
				assertLinesMatch(List.of(
					"org.junit.jupiter.engine.descriptor.TestInstanceLifecycleUtils -> org.junit.jupiter.engine.Constants",
					"org.junit.jupiter.engine.discovery.JavaElementsResolver -> org.junit.jupiter.engine.JupiterTestEngine",
					"org.junit.jupiter.engine.execution.ConditionEvaluator -> org.junit.jupiter.engine.Constants",
					"org.junit.jupiter.engine.extension.ExtensionRegistry -> org.junit.jupiter.engine.Constants"),
					result.getOutputLines("err"));
				break;
			case "junit-jupiter-params":
				assertEquals(1, result.getExitCode(), "result=" + result);
				assertTrue(result.getOutputLines("err").stream().allMatch(
					line -> line.startsWith("org.junit.jupiter.params.shadow.com.univocity.parsers.")));
				break;
			default:
				assertEquals(0, result.getExitCode(), "result=" + result);
		}

	}
```
