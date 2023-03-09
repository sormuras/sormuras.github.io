## JShell Tooling

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

jshell> run("javap", "-version")
17.0.6

jshell> javap("-version")
17.0.6

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
