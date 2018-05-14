# Scatter Assertions

[Is it OK to have multiple asserts in a single unit test?](https://softwareengineering.stackexchange.com/questions/7823/is-it-ok-to-have-multiple-asserts-in-a-single-unit-test)

What are the tools JUnit 5 (read Jupiter) offers to scatter assertions? 

Find the source to this blog entry at [demo/test/scatter](https://github.com/sormuras/sormuras.github.io/tree/master/demo/test/scatter).

![Scatter Assertions](2018-05-14-junit5-scatter-assertions-screenshot.png)

## A. Single method with three assertions

Sequential assertions in a single method.

```java
@Test
void test() {
  var object = new Object();
  assertNotNull(object);
  assertNotEquals(new Object(), object);
  assertThrows(IllegalMonitorStateException.class, object::wait);
}
```

## B. Three methods with single assertion each

Each assertion resides in its own test method.

```java
private final Object object = new Object();

@Test
void constructor() {
  assertNotNull(object);
}

@Test
void equality() {
  assertNotEquals(new Object(), object);
}

@Test
void waitWithoutMonitorFails() {
  assertThrows(IllegalMonitorStateException.class, object::wait);
}
```

## C. Single method with grouped assertion

In a grouped assertion all assertions are executed, and any failures will be reported together.

```java
@Test
void test() {
  var object = new Object();
  assertAll(
      () -> assertNotNull(object),
      () -> assertNotEquals(new Object(), object),
      () -> assertThrows(IllegalMonitorStateException.class, object::wait));
}
```

<https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions>

## D. Single test factory method with three dynamic tests

A `DynamicTest` is a test case generated at runtime. It is composed of a display name and an `Executable`.
`Executable` is a functional interface which means that the implementations of dynamic tests can be
provided as lambda expressions or method references.

```java
@TestFactory
Stream<DynamicTest> test() {
  var object = new Object();
  return Stream.of(
      dynamicTest("constructor", () -> assertNotNull(object)),
      dynamicTest("equality", () -> assertNotEquals(new Object(), object)),
      dynamicTest("waitWithoutMonitorFails", () -> assertThrows(Exception.class, object::wait)));
}
```

<https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests>

## E. Single parameterized method

```java
@ParameterizedTest
@MethodSource
void test(String caption, Executable executable) {
  assertDoesNotThrow(executable, caption);
}

static Stream<Arguments> test() {
  var object = new Object();
  return Stream.of(
      Arguments.of("constructor", (Executable) () -> assertNotNull(object)),
      Arguments.of("equality", (Executable) () -> assertNotEquals(new Object(), object)),
      Arguments.of("waitWithoutMonitorFails", (Executable) () -> assertThrows(Exception.class, object::wait)));
}
```

<https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests>
