# Multi-Release JAR Check

As of May 2021, Maven and Gradle and Ant (and other build tools) don't use JDK's `jar` tool to create or update JAR files.
Said (over-)simplified, most of them make use of a `zip`-like process that generates a `.zip` file with a `.jar` as an ending.

Since Java 9, `jar` also tries to validate the contents of a JAR file it creates or updates.
This goes for plain JAR file, modular JAR files, plain MR-JAR files, and also modular MR-JAR files.

This blog is about validating existing MR-JAR files, be they plain or modular, in regard of the following two categories.
I'll extend this list when needed.

## New or Different Classes

- https://github.com/unitsofmeasurement/indriya/issues/346

> (...) the classes in a multi-release JAR file should be exactly the same across versions.

## Wrong Release

- https://github.com/sandermak/modulescanner/issues/14

> For example, when a MR-JAR contains a version `9` directory within its `META-INF` facility,
> it shouldn't contain classes that are compiled with `--release 10` or higher.

## Command-line Demo

It'd be cool, if there was a: `jar --validate --file FILE.jar` option...

...but as there is none, let's roll our own validator!
Using as much logic as possible from the `jar` tool itself and JDK classes where applicable.

```
Usage: java MultiReleaseCheck.java <JAR|GAV>
       JAR = path to local JAR file
       GAV = Maven Central coordinates
```

- `java MultiReleaseCheck.java org.lwjgl:lwjgl:3.2.2`

```text
Expected 9, but 'META-INF/versions/9/module-info.class' reports: 10 (54)
```

- `java MultiReleaseCheck.java tech.units:indriya:2.1.2`

```text
entry: META-INF/versions/12/tech/units/indriya/format/NumberFormatStyle.class, contains a new public class not found in base entries
entry: META-INF/versions/12/tech/units/indriya/format/NumberDelimiterQuantityFormat.class, contains a class with different api from earlier version
module-info.class in a versioned directory contains additional "requires transitive"
module-info.class in a versioned directory contains additional "requires transitive"
entry: META-INF/versions/14/tech/units/indriya/AbstractSystemOfUnits.class, contains a class with different api from earlier version
invalid multi-release jar file $TEMP/MultiReleaseCheck-4926303968850859607/indriya-2.1.2.jar deleted
```

- Or via `jbang`: `jbang --java 16 sormuras.github.io/demo/main/MultiReleaseCheck.java <JAR|GAV>`

## Links

- [MultiReleaseCheck.java](../demo/main/MultiReleaseCheck.java)
- [JEP 238: Multi-Release JAR Files](https://openjdk.java.net/jeps/238)
