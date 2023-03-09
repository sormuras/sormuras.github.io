## Java Shell Tooling Script

This blog post is about running tools provided by the Java Development Kit in an interactive Java Shell session. 
The example presented below will print out verbose disassembled code with internal type signatures of a Java class created in the current Java Shell session.   

### Goal

In other words, the goal is to be able to write something like the following:

```text
jshell> interface Empty {}
jshell> javap(Empty.class)
```
For reference, here are the equivalent steps and commands for an operating system shell like `cmd.exe` or `bash`:

- Create "Empty.java" file in the current working directory
  - Windows: `echo interface Empty {} > Empty.java`
  - Linux/Mac: `echo 'interface Empty {}' > Empty.java`  
- `javac Empty.java`
- `javap -c -s -v Empty.class`

Depending on the JDK version installed and configured (here JDK 17.0.6), the output should read like:

```text
Classfile .../Empty.class
  Last modified Mar 9, 2023; size 91 bytes
  SHA-256 checksum 619286c972fba33cc94ae68b010888447efeafa04c58edad5045052f720a87fc
  Compiled from "Empty.java"
interface Empty
  minor version: 0
  major version: 61
  flags: (0x0600) ACC_INTERFACE, ACC_ABSTRACT
  this_class: #1                          // Empty
  super_class: #3                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 0, attributes: 1
Constant pool:
  #1 = Class              #2              // Empty
  #2 = Utf8               Empty
  #3 = Class              #4              // java/lang/Object
  #4 = Utf8               java/lang/Object
  #5 = Utf8               SourceFile
  #6 = Utf8               Empty.java
{
}
SourceFile: "Empty.java"
```

### Introducing TOOLING.jsh

The source of `TOOLING.jsh` is shared at: <https://github.com/sormuras/jdk-tools/blob/main/TOOLING.jsh>

Take a moment to follow the link read it before proceeding with using `TOOLING.jsh`.

tl;dr:

```jshelllanguage
// Dedicated tool running methods
void jar(String... args) { run("jar", args); }
void javac(String... args) { run("javac", args); }
void javadoc(String... args) { run("javadoc", args); }
void javap(String... args) { run("javap", args); }
// ...

// Arbitrary tool running method
void run(String name, String... args) {
    var tool = java.util.spi.ToolProvider.findFirst(name).orElseThrow();
    tool.get().run(System.out, System.err, args);
}

// List loadable tool providers
void tools() {
    java.util.ServiceLoader.load(java.util.spi.ToolProvider.class).stream()
            .map(java.util.ServiceLoader.Provider::get)
            .map(java.util.spi.ToolProvider::name)
            .sorted()
            .forEach(System.out::println);
}
```

Consult the API documentation of [ServiceLoader](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ServiceLoader.html) (since Java 6) and [ToolProvider](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/spi/ToolProvider.html) (since Java 9) for more details. 

### Using TOOLING.jsh

The `jshell` tool supports loading predefined scripts via its `/open file` command.

The section of `jshell`'s man page reads:

> `/open file`
>
> _Opens the script specified and reads the snippets into the tool._
> _The script can be a local file or one of the following predefined scripts:_
>
> - `DEFAULT`
>   _Loads the default entries, which are commonly used as imports._
> - `JAVASE`
>   _Imports all Java SE packages._
> - `PRINTING`
>   _Defines print, println, and printf as jshell methods for use within the tool._

In addition to local files and predefined scripts you may also load a remote resource.

---
Open `TOOLING.jsh` in a fresh Java Shell session.
List available tools by invoking the `tools()` method. 
```text
jshell
|  Welcome to JShell -- Version 17.0.6
|  For an introduction type: /help intro

jshell> /open https://github.com/sormuras/jdk-tools/raw/main/TOOLING.jsh

jshell> tools()
jar
javac
javadoc
javap
jdeps
jlink
jmod
jpackage
```
_Note that not all tools listed by the [Java Development Kit Tool Specifications](https://docs.oracle.com/en/java/javase/17/docs/specs/man/) implement the `ToolProvider` interface._
_Some might do so in the future, some never will._

---
Run an arbitrary tool by passing its name and command-line arguments to the `run()` method.
```text
jshell> run("javap", "-version")
17.0.6
```

---
Run a tool by using its dedicated wrapper method and passing command-line arguments.
```text
jshell> javap("-version")
17.0.6
```
---
Run `javap` tool for the `Runnable` class (from package `java.lang` in module `java.base` loaded by the Java Runtime `jrt:` file system) by using the `javap()` overload that takes an instance of `Class<?>` as an argument.
```text
jshell> javap(java.lang.Runnable.class)
Classfile jrt:/java.base/java/lang/Runnable.class
  Last modified Dec 6, 2022; size 201 bytes
  SHA-256 checksum a4525fd4150d870abe74d18d81470c2ba6b345bbbea96e2a8d09acfb0026e5f8
  Compiled from "Runnable.java"
public interface java.lang.Runnable
  minor version: 0
  major version: 61
  flags: (0x0601) ACC_PUBLIC, ACC_INTERFACE, ACC_ABSTRACT
  this_class: #1                          // java/lang/Runnable
  super_class: #3                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 1, attributes: 2
Constant pool:
   #1 = Class              #2             // java/lang/Runnable
   #2 = Utf8               java/lang/Runnable
   #3 = Class              #4             // java/lang/Object
   #4 = Utf8               java/lang/Object
   #5 = Utf8               run
   #6 = Utf8               ()V
   #7 = Utf8               SourceFile
   #8 = Utf8               Runnable.java
   #9 = Utf8               RuntimeVisibleAnnotations
  #10 = Utf8               Ljava/lang/FunctionalInterface;
{
  public abstract void run();
    descriptor: ()V
    flags: (0x0401) ACC_PUBLIC, ACC_ABSTRACT
}
SourceFile: "Runnable.java"
RuntimeVisibleAnnotations:
  0: #10()
    java.lang.FunctionalInterface
```

### Goal!

Create an empty interface named `Empty` and disassemble it promptly.

```text
jshell> interface Empty {}
|  created interface Empty

jshell> javap(Empty.class)
Classfile /tmp/TOOLING-11160583645141535433.class
  Last modified Mar 9, 2023; size 191 bytes
  SHA-256 checksum 1b2d874a42c3695a136f3ce0bb849ce2aa09f6a96c8b5f4d18f7c1f07fc8bf20
  Compiled from "$JShell$27.java"
public interface REPL.$JShell$27$Empty
  minor version: 0
  major version: 61
  flags: (0x0601) ACC_PUBLIC, ACC_INTERFACE, ACC_ABSTRACT
  this_class: #1                          // REPL/$JShell$27$Empty
  super_class: #3                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 0, attributes: 3
Constant pool:
   #1 = Class              #2             // REPL/$JShell$27$Empty
   #2 = Utf8               REPL/$JShell$27$Empty
   #3 = Class              #4             // java/lang/Object
   #4 = Utf8               java/lang/Object
   #5 = Utf8               SourceFile
   #6 = Utf8               $JShell$27.java
   #7 = Utf8               NestHost
   #8 = Class              #9             // REPL/$JShell$27
   #9 = Utf8               REPL/$JShell$27
  #10 = Utf8               InnerClasses
  #11 = Utf8               Empty
{
}
SourceFile: "$JShell$27.java"
NestHost: class REPL/$JShell$27
InnerClasses:
  public static #11= #1 of #8;            // Empty=class REPL/$JShell$27$Empty of class REPL/$JShell$27
```
