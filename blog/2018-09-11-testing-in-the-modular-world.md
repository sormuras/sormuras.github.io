# Testing In The Modular World

This is a blog about how to organize, find and execute tests.
This is not an introduction to the Java module system.

TL;DR - Fork/clone and run sample project [sors-modular-testing-blueprint](https://github.com/sormuras/sandbox/blob/master/sors-modular-testing-blueprint)

## Good ol' times

Flash-back to the early days of unit testing in Java and to the question: [_"Where should I put my test files?"_](https://junit.org/junit4/faq.html#organize_1)

- You can place your tests in the same package and directory as the classes under test.

For example:

```text
src/
   com/
      xyz/
         📜 SomeClass.java
         🔨 SomeClassTests.java
```

While adequate for small projects, many developers felt that this approach clutters the source directory, and makes it hard to package up client binaries without also including unwanted test code, or writing unnecessarily complex packaging tasks.

- An arguably better way is to place the tests in a separate parallel directory structure with package alignment.

```text
main/                          test/
   com/                           com/
      xyz/                           xyz/
         📜 SomeClass.java              🔨 SomeClassTests.java
```

This approach allows test code to access to all the `public` and package visible members of the classes under test.

Who made it and still makes it work today? The **`classpath`**!
Every classpath element points to a root of assets contributing to the resources available at runtime.
A special type of resource is a Java class which in turn declares an package it belongs to.
There is no enforced restriction of how many times a package may be declared on the classpath.
All assets are merged logically at runtime, effectively resulting in the same situation where classes under test and test classes reside physically in the same directory.
Meaning most (all?) access modifiers are irrelevant: packages are treated as white boxes.

Ever placed a test class in a different package compared to the class under test?
Welcome (b(l)ack) to "black box testing in the package world"!

```text
main/                          test/                               test/
   com/                           com/                                black/
      xyz/                           xyz/                                box/
         📜 SomeClass.java              🔨 SomeClassTests.java              🔲 BlackBoxTests.java
```

Which types and members from main are accessible from such a black box test?
The answer is left open for a brush-up of the reader's modifier visibility memory.

## Fast-forward to modules

Packages are now members of modules and only some packages are exported to other modules for consumption.
Extrapolating the idea of separated source set roots to the Java module system could lead to a layout like:

```text
main/                          test/                               test/
   com.xyz/                       com.xyz/                            black.box/
      com/                           com/                                black/
         abc/                           abc/                                box/
            📜 OtherClass.java             🔨 OtherClassTests.java              🔲 BlackBoxTests.java
         xyz/                           xyz/                             ☕ module-info.java
            📜 SomeClass.java              🔨 SomeClassTests.java
      ☕ module-info.java             🔥 module-info.[java|test] 🔥
```

You already noticed that the _white box_ source set contains a cloak-and-dagger `module-info.[java|test]` file.
Before diving into this topic, let's examine the other two plain and simple module descriptors.

### ☕ `module com.xyz`

- The main module descriptor for `com.xyz` contains some imaginary entries.
- It only exports package `com.xyz` but also contains the `com.abc` package.

```java
module com.xyz {
    requires java.logging;
    requires java.sql;

    exports com.xyz;
}
```
_Note! The package `com.abc` should **not** be part of a module named `com.xyz`. Why not? See [Stephen's JPMS module naming](https://blog.joda.org/2017/04/java-se-9-jpms-module-naming.html) blog for details._

### ☕ `open module black.box`

- The test module descriptor for `black.box` reads module `com.xyz` and a bunch of testing framework modules.
- It may only refer to accessible (`public` and residing in an `exported` package) types in those other modules.
- This includes modules `com.xyz` in particular: tests may refer to published types in package `com.xyz` - test can't refer to types in non-published package `com.abc`.
- Module `black.box` is declaring itself `open` allowing test discovery via deep reflection.

```java
open module black.box {
    requires com.xyz;

    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires org.mockito;
}
```

# Modular White Box Testing

## Visibility table

Based on the [Visibility](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html) table.

- **B** - same module, same package, **other** compilation unit: `package foo; class B {}`
- **C** - same module, **other** package, subclass: `package bar; class C extends foo.A {}`
- **D** - same module, **other** package, unrelated class: `package bar; class D {}`
- **E** - **other** module, package `foo` is exported: `package bar; class E {}`
- **F** - **other** module, package `foo` _not_ exported `package bar; class F {}`

```text
                       B   C   D   E   F
package foo;
public class A {       o   o   o   o   -
  private int a;       -   -   -   -   -
  int b;               o   -   -   -   -
  protected int c;     o   o   -   -   -
  public int d;        o   o   o   o   -
}
```

## 🔥`module-info.[java|test]`🔥

At least three ways exist that lift the strict module boundaries for testing.

### Resort to the classpath

Delete all `module-info.java` files, or exclude them from compilation, and your tests ignore all boundaries implied by the Java module system.
Use internal implementation details of the Java runtime, 3rd-party libraries including test framework and of course, use the internal types from your _main_ source set.
The last part was the intended goal -- achieved, yes, but paid a very high price.

Let's explore ways that maintain the boundaries of the Java module system intact.

### White box modular testing with `module-info.java`

The foundation tool `javac` version 9+ and `maven-compiler-plugin` version 3.8.0+ support compiling `module-info.java` residing in test source sets.

Here you use the default module description syntax to a) shadow the main configuration and b) express addition requirements needed for testing.

- `module-info.java`

```java
// same name as main module and open for deep reflection
open module com.xyz {
    requires java.logging;          // copied from main module descriptor
    requires java.sql;              // - " -
    exports com.xyz;                // - " -

    requires org.junit.jupiter.api; // additional test requirement
    requires org.assertj.core;      // - " -
    requires org.mockito;           // - " -
}
```

_Note: Copying parts from the main module descriptor manually is brittle. The "Java 9 compatible build tool" [pro](https://github.com/forax/pro) solves this by auto-merging a main and test module descriptor on-the-fly._

### White box modular testing with extra `java` command line options

The foundation tool `java` version 9+ provides command line options configure the Java module system "on-the-fly" at start up time.
Various test launcher tools allow additional command line options to be passed to the test runtime.

Here are the additional command line options needed to achieve the same modular configuration as above:

- `module-info.test`

```text
--add-opens                                   | "open module com.xyz"
  com.xyz/com.abc=org.junit.platform.commons  |
--add-opens                                   |
  com.xyz/com.xyz=org.junit.platform.commons  |

--add-reads                                   | "requires org.junit.jupiter.api"
  com.xyz=org.junit.jupiter.api               |
--add-reads                                   | "requires org.assertj.core"
  com.xyz=org.assertj.core                    |
--add-reads                                   | "requires org.mockito"
  com.xyz=org.mockito                         |
```

This option is already "supported" by some IDEs, at least they don't stumble compiling tests when a `module-info.test` file is present.

## Test Mode

The test mode is defined by the relation of one *main* and one *test* module name.

- `C` = `CLASSIC` -> no modules available
- `M` = `MODULAR` -> main 'module `foo`' and test 'module `bar`' OR main lacks module and test 'module `any`'
- `A` = `MODULAR_PATCHED_TEST_COMPILE` -> main 'module `foo`' and test 'module `foo`'
- `B` = `MODULAR_PATCHED_TEST_RUNTIME` -> main 'module `foo`' and test lacks module

### Test Mode Table

```text
                          main plain    main module   main module
                             ---            foo           bar
     test plain  ---          C              B             B
     test module foo          M              A             M
     test module bar          M              M             A
```

### Test Mode Algorithm Outline

```java
  static TestMode of(String main, String test) {
    var mainAbsent = main == null || main.trim().isEmpty(); // 12: main.isBlank();
    var testAbsent = test == null || test.trim().isEmpty(); // 12: test.isBlank();
    if (mainAbsent) {
      if (testAbsent) {      // trivial case: no modules declared at all
        return CLASSIC;
      }
      return MODULAR;        // only test module is present, no patching involved
    }
    if (testAbsent) {        // only main module is present
      return MODULAR_PATCHED_TEST_RUNTIME;
    }
    if (main.equals(test)) { // same module name
      return MODULAR_PATCHED_TEST_COMPILE;
    }
    return MODULAR;          // bi-modular testing, no patching involved
  }
```

## Sample Project

### Foundation tools `javac` and `java` (and `jshell`)

[junit5-modular-world](https://github.com/junit-team/junit5-samples/tree/master/junit5-modular-world)

### Maven Blueprint

[sors-modular-testing-blueprint](https://github.com/sormuras/sandbox/blob/master/sors-modular-testing-blueprint)

```text
.
├── main
│   └── java
│       ├── foo
│       │   ├── PackageFoo.java
│       │   └── PublicFoo.java
│       └── module-info.java <------------------ module foo { exports foo; }
├── test
│   └── java                                .--- open module foo {
│       ├── foo                            /       exports foo;
│       │   └── PackageFooTests.java      /        requires org.junit.jupiter.api;
│       └── module-info.[java|test] <----<       }
└── test-integration                      \
    └── bar                                °---- --add-reads
        └── src                                    foo=org.junit.jupiter.api
            └── test                             --add-opens
                └── java                           foo/foo=org.junit.platform.commons
                    ├── bar
                    │   └── PublicFooTests.java
                    └── module-info.java <------ open module bar {
                                                   requires foo;
                                                   requires org.junit.jupiter.api;
                                                 }
```

### Maven + JUnit Platform running all test modes

[sawdust - sources](https://github.com/micromata/sawdust)

[sawdust - build](https://travis-ci.org/micromata/sawdust)

# Resources

- [Jigsaw](http://openjdk.java.net/projects/jigsaw/) **Key documents, presentations, & other resources**
- [Sawdust](https://github.com/micromata/sawdust) **Show-casing test modes defined here**
- [JUnit Platform Maven Plugin](https://github.com/sormuras/junit-platform-maven-plugin) **Maven support for test modes defined here**
- [CodeFX/JPMS](https://blog.codefx.org/tag/jpms/) **Blog about the Java module system and more**

## Revision

This is a living document, it will be updated now-and-then.

2018-09-11 Initial version
