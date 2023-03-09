# Roll your own `TestEngine`

The User Guide of JUnit 5 has a section named [Plugging in your own Test Engine](https://junit.org/junit5/docs/current/user-guide/#launcher-api-engines-custom).
This section links to JUnit Jupiter and JUnit Vintage as two `TestEngine` implementations.
It also mentions that:

> Third parties may also contribute their own `TestEngine` by implementing the interfaces in the `org.junit.platform.engine` module and registering their engine.

This section briefly explains ways to register a custom Test Engine and ends with some notes and warnings about reserved names.
But that's it.
No further documentation is provided on how to roll your own.
This blog post closes this gap ... soon.

## Interface TestEngine

https://junit.org/junit5/docs/current/api/org.junit.platform.engine/org/junit/platform/engine/TestEngine.html

> A `TestEngine` facilitates discovery and execution of tests for a particular programming model.
> For example, JUnit provides a TestEngine that discovers and executes tests written using the JUnit Jupiter programming model.
>
> Every `TestEngine` must provide its own unique ID, discover tests from `EngineDiscoveryRequests`, and execute those tests according to `ExecutionRequests`.


## Examples

- 🍨 [Ice Cream Test Engine](https://github.com/junit-team/junit5-samples/tree/master/junit5-modular-world#ice-cream-test-engine)

  The `ice.cream` module demonstrates how to write and register your own `TestEngine` implementatio.
  This engine does not find any tests in containers, but "discovers" a configurable amount of ice cream scoops.

- 🦄 [Java Program JUnit Platform Test Engine](https://github.com/sormuras/mainrunner)

  Write a plain Java program under `src/test/java`
  Include Mainrunner at test runtime on Java 11 or later.
  Done.

- 🧵 [Sleeping Virtual Threads Test Engine](https://github.com/sormuras/junit5-looming)

  Similary to the Ice Cream Test Engine, this one also generates fake tests.
  All those tests do is: `Thread.sleep(...);`
  But using Virtual Threads from the Project [Loom](https://jdk.java.net/loom) early-access builds.

- Test Engines listed on the [JUnit 5 Wiki](https://github.com/junit-team/junit5/wiki/Third-party-Extensions#junit-platform-test-engines)

  Cucumber - Drools Scenario - jqwik - KotlinTest - Specsy - Spek - Bnd Tester/Bundle Engine - TestNGine

## 
