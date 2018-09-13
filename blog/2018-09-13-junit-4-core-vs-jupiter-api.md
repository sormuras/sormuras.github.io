# JUnit 4 vs Jupiter - a high-level API comparison

| JUnit 4 | Jupiter |
| :-----: | :-----: |
| [org/junit/package-summary](https://junit.org/junit4/javadoc/latest/org/junit/package-summary.html) | [org/junit/jupiter/api/package-summary](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/package-summary.html) |

## Common Concepts

Basic stuff is basic. Commonly.

### Entry-points

| JUnit 4 | Jupiter |
| ------- | ------- |
| `Test`  | `Test`  |
| `org.junit` | `org.junit.jupiter.api` |
| _The `Test` annotation tells JUnit that the `public void` method to which it is attached can be run as a test case._ | _`@Test` is used to signal that the annotated method is a test method._ |

- Caveat! The `expected` and `timeout` annotation elements of `org.junit.Test` are now handled by dedicated **Jupiter** assertions.
 
### Checks
 
| JUnit 4 | Jupiter |
| ------- | ------- |
| `Assert` | `Assertions` |
| `org.junit` | `org.junit.jupiter.api` |
| _A set of assertion methods useful for writing tests._ | _`Assertions` is a collection of utility methods that support asserting conditions in tests._ |

- Mind the flip! From `Assert.assertEquals(String message, Object expected, Object actual)` to `Assertions.assertEquals(Object expected, Object actual, String message)`.

### Unconditional Break-outs

| JUnit 4 | Jupiter |
| ------- | ------- |
| `Ignore` | `Disabled` |
| `org.junit` | `org.junit.jupiter.api` |
| _Sometimes you want to temporarily disable a test or a group of tests._ | _`@Disabled` is used to signal that the annotated test class or test method is currently disabled and should not be executed._ |

### Conditional Test Execution

| JUnit 4 | Jupiter |
| ------- | ------- |
| `Assume` | `Assumptions` |
| `org.junit` | `org.junit.jupiter.api` |
| _A set of methods useful for stating assumptions about the conditions in which a test is meaningful._ | _`Assumptions` is a collection of utility methods that support conditional test execution based on assumptions._ |

- New! Annotation-based conditions for enabling or disabling tests in JUnit Jupiter [org.junit.jupiter.api.condition](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/condition/package-summary.html)

### Life-cycle Hooks

| JUnit 4 | Jupiter |
| ------- | ------- |
| `org.junit` | `org.junit.jupiter.api` |
| `BeforeClass` | `BeforeAll` |
| _Sometimes several tests need to share computationally expensive setup (like logging into a database)._ | _`@BeforeAll` is used to signal that the annotated method should be executed before all tests in the current test class._ |
| `Before` | `BeforeEach` |
| _When writing tests, it is common to find that several tests need similar objects created before they can run._ | _`@BeforeEach` is used to signal that the annotated method should be executed before **each** `@Test`, `@RepeatedTest`, `@ParameterizedTest`, `@TestFactory`, and `@TestTemplate` method in the current test class._ |
| `After` | `AfterEach` |
| _If you allocate external resources in a `Before` method you need to release them after the test runs._ | _`@AfterEach` is used to signal that the annotated method should be executed after **each** `@Test`, `@RepeatedTest`, `@ParameterizedTest`, `@TestFactory`, and `@TestTemplate` method in the current test class._ |
| `AfterClass` | `AfterAll` |
| _If you allocate expensive external resources in a `BeforeClass` method you need to release them after all the tests in the class have run._ | _`@AfterAll` is used to signal that the annotated method should be executed after all tests in the current test class._ |

## Changed Concepts

Changed, evolved, matured.

### Tagging and Filtering

In contrast to the limited `org.junit.experimental.categories.Category` annotation and its associated runner in JUnit 4, JUnit Jupiter now uses simple `String`s as markers. 
Test classes and methods can be tagged via the [`@Tag`](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/Tag.html) annotation.
Those tags can later be used to filter test discovery and execution.

For a detailed description consult the [Tagging and Filtering](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering) chapter in the User-Guide.

### Test Instance Lifecycle

In order to allow individual test methods to be executed in isolation and to avoid unexpected side effects due to mutable test instance state, JUnit creates a new instance of each test class before executing each test method.
This "per-method" test instance lifecycle is the default behavior in JUnit Jupiter and is analogous to all previous versions of JUnit, including JUnit 4.
If you would prefer that JUnit Jupiter execute all test methods on the same test instance, simply annotate your test class with `@TestInstance(Lifecycle.PER_CLASS)`.

`TestInstance` - _`@TestInstance` is a type-level annotation that is used to configure the lifecycle of test instances for the annotated test class or test interface._

For a detailed description consult the [Test Instance Lifecycle](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle) chapter in the User-Guide.

### Extension Points

In contrast to the competing `Runner`, `@Rule`, and `@ClassRule` extension points in JUnit 4, the JUnit Jupiter extension model consists of a single, coherent concept: [the `Extension` API](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/extension/package-summary.html).

| JUnit 4 | Jupiter |
| ------- | ------- |
| `org.junit` | `org.junit.jupiter.api.extension` |
| `Rule` +  | `Extension` |
| `ClassRule` | `Extension` |
| `Runner` | `Extension` |

For a detailed description consult the [Extension Model](https://junit.org/junit5/docs/current/user-guide/#extensions) chapter in the User-Guide.

## New Concepts

REVOLUTION! You might have not asked for it, but it is here.

### Emit human-readable messages

In JUnit Jupiter you should use [`TestReporter`](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/TestReporter.html) where you used to print information to _stdout_ or _stderr_ in JUnit 4.

### Grouping, nesting, gathering..._

Nested tests give the test writer more capabilities to express the relationship among several group of tests.

- `org.junit.jupiter.api.Nested` _`@Nested` is used to signal that the annotated class is a nested, non-static test class (i.e., an inner class) that can share setup and state with an instance of its enclosing class._

For a detailed description consult the [Dynamic Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-nested) chapter in the User-Guide.

### Testlets

A new kind of test is a dynamic test which is generated at runtime by a factory method that is annotated with `@TestFactory`.

- `org.junit.jupiter.api.TestFactory` _@TestFactory is used to signal that the annotated method is a test factory method._
- `org.junit.jupiter.api.DynamicNode` _DynamicNode serves as the abstract base class for a container or a test case generated at runtime._
- `org.junit.jupiter.api.DynamicContainer` _A DynamicContainer is a container generated at runtime._
- `org.junit.jupiter.api.DynamicTest` _A DynamicTest is a test case generated at runtime._

For a detailed description consult the [Dynamic Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests) chapter in the User-Guide.

### Test Templates

A `@TestTemplate` method is not a regular test case but rather a template for test cases.
As such, it is designed to be invoked multiple times depending on the number of invocation contexts returned by the registered providers.
Thus, it must be used in conjunction with a registered `TestTemplateInvocationContextProvider` extension.
Each invocation of a test template method behaves like the execution of a regular `@Test` method with full support for the same lifecycle callbacks and extensions.

Copied over from [Test Templates](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-templates)

### Repetitions (via Test Templates)

JUnit Jupiter provides the ability to repeat a test a specified number of times simply by annotating a method with `@RepeatedTest` and specifying the total number of repetitions desired.

- `org.junit.jupiter.api.RepeatedTest` _`@RepeatedTest` is used to signal that the annotated method is a test template method that should be repeated a specified number of times with a configurable display name._
- `org.junit.jupiter.api.RepetitionInfo` _`RepetitionInfo` is used to inject information about the current repetition of a repeated test into `@RepeatedTest`, `@BeforeEach`, and `@AfterEach` methods._

For a detailed description consult the [Repeated Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-repeated-tests) chapter in the User-Guide.

### Parameterized Tests (via Test Templates)

In JUnit 4 a limiting runner was needed from `org.junit.runners.parameterized`.

In JUnit Jupiter parameterized tests are now implemented as test template extension.
The API resides in its own dedicated module: `org.junit.jupiter.params`

For a detailed description consult the [Parameterized Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests) chapter in the User-Guide.

### Parallel Execution

JUnit Jupiter API for influencing parallel test execution.

[org.junit.jupiter.api.parallel](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/parallel/package-summary.html)

For a detailed description consult the [Parallel Execution](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution) chapter in the User-Guide.
