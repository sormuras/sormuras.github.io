# Testing In The Modular World

This is a blog about how to organize, find and execute tests.
This is not an introduction to the Java module system.

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
Meaning most (all?) access modifiers are irrelevant: packages are treated as white-boxes.  

Ever placed a test class in a different package compared to the class under test?
Welcome to "black-box testing in the package world"!

```text
main/                          test/                               test/
   com/                           com/                                black/
      xyz/                           xyz/                                box/
         📜 SomeClass.java              🔨 SomeClassTests.java              🔲 BlackBoxTests.java
```

// TODO What is accessible from a black-box test?

## Fast-forward to modules

Extrapolate the idea of separated source set roots to the Java module system.
Packages are now members of modules and only some packages are exported to other modules for consumption.
// TODO In order to access... 

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

You already noticed that the _white-box_ source set contains a cloak-and-dagger `module-info.[java|test]` file.
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


## 💣 `module-info.[java|test]` or white-box testing modules

At least two ways exist that lift the strict module boundaries for testing.

### White-box testing with `module-info.java`

- `javac` version 9+ and `maven-compiler-plugin` version 3.8.0+ support compiling `module-info.java` residing in test source sets.

### White-box testing with `java` command line options

- `java` version 9+ provides command line options configure the Java module system "on-the-fly" at start up time.
- various test launcher tools allow additional command line options to be passed to the test runtime


## Test Mode Table

https://static.javadoc.io/de.sormuras/junit-platform-maven-plugin/0.0.9/de/sormuras/junit/platform/maven/plugin/testing/TestMode.html

Defined by the relation of one main and one test module name.

```text
                          main plain    main module   main module
                             ---            foo           bar

     test plain  ---          0              2             2

     test module foo          1              3             4

     test module bar          1              4             3

     0 = CLASSIC
     1 = MAIN_CLASSIC_TEST_MODULE           (black-box test) // CLASSIC_MODULAR_TEST
     2 = MAIN_MODULE_TEST_CLASSIC           (white-box test) // MODULAR_CLASSIC_TEST
     3 = MAIN_MODULE_TEST_MODULE_SAME_NAME  (white-box test) // MODULAR_PATCHED_TEST
     4 = MODULAR                            (black-box test) // MODULAR_BLACKBOX_TEST
```

- Algorithm outline:

```java
static Mode of(String main, String test) { 
   if (main == null) { 
     if (test == null) { // trivial case: no modules declared at all 
       return CLASSIC; 
     } 
     return MAIN_CLASSIC_TEST_MODULE; // only test module present 
   } 
   if (test == null) { // only main module is present 
     return MAIN_MODULE_TEST_CLASSIC; 
   } 
   if (main.equals(test)) { // same module name 
     return MAIN_MODULE_TEST_MODULE_SAME_NAME; 
   } 
   return MODULAR; // true-modular testing, no patching involved 
 }
```

## Maven Sample Project

https://github.com/sormuras/sandbox/blob/master/sors-modular-testing-blueprint

```text
.
├── main
│   └── java
│       ├── foo
│       │   ├── PackageFoo.java
│       │   └── PublicFoo.java
│       └── module-info.java
├── test
│   └── java
│       ├── foo
│       │   └── PackageFooTests.java
│       └── module-info.java
└── it
    ├── bar
    │   ├── pom.xml
    │   └── src
    │       └── test
    │           └── java
    │               ├── bar
    │               │   └── PublicFooTests.java
    │               └── module-info.java
    └── settings.xml
```

# Resources

- http://openjdk.java.net/projects/jigsaw/ **Key documents, presentations, & other resources**
- https://blog.codefx.org/tag/jpms/
