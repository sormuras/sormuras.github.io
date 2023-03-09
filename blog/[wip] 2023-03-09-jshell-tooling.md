## JShell Tooling

```text
jshell
|  Welcome to JShell -- Version 11.0.14.1
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
```

```text
jshell> javap("-version")

11.0.14.1
```

```text
jshell> javap("-c", "java.lang.Runnable")

Compiled from "Runnable.java"
public interface java.lang.Runnable {
  public abstract void run();
}
```

```text
jshell> javap(Runnable.class)

Classfile jrt:/modules/java.base/java/lang/Runnable.class
  Last modified Feb 27, 2022; size 201 bytes
  MD5 checksum 18222535968b132563fe0ad616f6fbc7
  Compiled from "Runnable.java"
public interface java.lang.Runnable
  minor version: 0
  major version: 55
  flags: (0x0601) ACC_PUBLIC, ACC_INTERFACE, ACC_ABSTRACT
  this_class: #1                          // java/lang/Runnable
  super_class: #2                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 1, attributes: 2
Constant pool:
   #1 = Class              #9             // java/lang/Runnable
   #2 = Class              #10            // java/lang/Object
   #3 = Utf8               run
   #4 = Utf8               ()V
   #5 = Utf8               SourceFile
   #6 = Utf8               Runnable.java
   #7 = Utf8               RuntimeVisibleAnnotations
   #8 = Utf8               Ljava/lang/FunctionalInterface;
   #9 = Utf8               java/lang/Runnable
  #10 = Utf8               java/lang/Object
{
  public abstract void run();
    descriptor: ()V
    flags: (0x0401) ACC_PUBLIC, ACC_ABSTRACT
}
SourceFile: "Runnable.java"
RuntimeVisibleAnnotations:
  0: #8()
    java.lang.FunctionalInterface
```

```text
jshell> class Empty {}

|  created class Empty

jshell> javap(Empty.class)

Classfile /tmp/TOOLING-17832827764353920976.class
  Last modified Mar 9, 2023; size 284 bytes
  MD5 checksum 48ec6cfb46485ad9ebc48ad49c156861
  Compiled from "$JShell$30.java"
public class REPL.$JShell$30$Empty
  minor version: 0
  major version: 55
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #2                          // REPL/$JShell$30$Empty
  super_class: #3                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 1, attributes: 3
Constant pool:
   #1 = Methodref          #3.#12         // java/lang/Object."<init>":()V
   #2 = Class              #13            // REPL/$JShell$30$Empty
   #3 = Class              #16            // java/lang/Object
   #4 = Utf8               <init>
   #5 = Utf8               ()V
   #6 = Utf8               Code
   #7 = Utf8               LineNumberTable
   #8 = Utf8               SourceFile
   #9 = Utf8               $JShell$30.java
  #10 = Utf8               NestHost
  #11 = Class              #17            // REPL/$JShell$30
  #12 = NameAndType        #4:#5          // "<init>":()V
  #13 = Utf8               REPL/$JShell$30$Empty
  #14 = Utf8               Empty
  #15 = Utf8               InnerClasses
  #16 = Utf8               java/lang/Object
  #17 = Utf8               REPL/$JShell$30
{
  public REPL.$JShell$30$Empty();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 5: 0
}
SourceFile: "$JShell$30.java"
NestHost: class REPL/$JShell$30
InnerClasses:
  public static #14= #2 of #11;           // Empty=class REPL/$JShell$30$Empty of class REPL/$JShell$30
```
