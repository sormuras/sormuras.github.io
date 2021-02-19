## Project 🧩 sormuras/modules

Since August 2018, I compile an overview of Java modules uploaded to Maven Central at  https://github.com/sormuras/modules.

This overview is generated by the [Scanner](https://github.com/sormuras/modules/blob/main/src/Scanner.java) program and it is based on the results of the [modulescanner](https://github.com/sandermak/modulescanner) that is run on [Sonatype](https://sonatype.com)s hardware on every uploaded JAR file and stored in an AWS S3 [bucket](s3://java9plusadoption), setup together with the [AdoptOpenJDK](https://adoptopenjdk.net/) team at their [#java9plusadoption](https://adoptopenjdk.slack.com/archives/CB7L6GDUK) Slack channel. Some of those uploaded JAR files are Java modules. They are the interesting subjects of this overview as they contain a `module-info.class`, a compiled module descriptor with a stable name and an explicit API their author(s) comitted to.

Let us start with looking at the raw numbers. As of today (February 18, 2021) the [scan report](https://github.com/sormuras/modules/runs/1923991084?check_suite_focus=true#step:7:11) reads:
```text
Scanning 72598 files in bucket...
  5,204,605 lines
  2,020,116 distinct scan lines
    204,617 artifacts
    189,650 JAR files are plain
     10,276 JAR files with Automatic-Module-Name
      4,691 JAR files with module-info.class
      3,779 distinct modules
      2,900 unique modules
```
- Over 2 (out of 5) million distinct lines were read from **72,598** CSV files downloaded from the S3 bucket. Somebody should reduce the 3 million lines overhead sometime.
- **204,617** JAR file artifacts were analyzed, with
  - the vast majority of **189,650** JAR files were plain old JAR files,
  - **10,276** of them contained an `Automatic-Module-Name` manifest entry, and
  - **4,691** of them contained (at least one) `module-info.class` file.
- Out of the 4,691 Java modules only **3,779** distinct modules could be filtered out. Due to wrong repackaging/shadowing of 3rd-party libraries like ASM or Log4J, there are already many unintended modules, I like to call them impostor modules, published at Maven Central. If [Java modules names were treated as first-class library properties](https://github.com/sonatype-nexus-community/search-maven-org/issues/6) one could search [Maven Central Repository](https://search.maven.org) for `org.objectweb.asm`, `org.apache.logging.log4j`, or other module names of well-known and often shadowed libraries to see many hits for various GroupIDs.
- Out of the 3,779 distinct modules, I selected **2,900** unique modules by applying a filter comparing module names to the their Maven Group identifiers.

## Unique Java Modules

I consider a Java module to be **unique** on Maven Central

- if it is an explicit module with a compiled module descriptor,
- and if its module name starts with its Maven Group identifier or a well-known alias.

Here are some examples of unique modules, showing their module names and Maven `Group:Artifact` identifiers:

|                     Module | Group ID : Artifact ID |
|---------------------------:|------------------------|
| `ch.qos.logback.core`      | `ch.qos.logback:logback-core` |
| `ch.qos.logback.classic`   | `ch.qos.logback:logback-classic` |
| `com.google.gson`          | `com.google.code.gson:gson` |
| `java.xml.bind`            | `javax.xml.bind:jaxb-api` |
| `net.bytebuddy`            | `net.bytebuddy:byte-buddy` |
| `net.bytebuddy.agent`      | `net.bytebuddy:byte-buddy-agent` |
| `org.apache.logging.log4j` | `org.apache.logging.log4j:log4j-api` |
| `org.junit.jupiter `       | `org.junit.jupiter:junit-jupiter` |
| `org.junit.jupiter.api`    | `org.junit.jupiter:junit-jupiter-api` |
| `org.junit.jupiter.engine` | `org.junit.jupiter:junit-jupiter-engine` |
| `org.slf4j`                | `org.slf4j:slf4j-api` |


Well-known [aliases](https://github.com/sormuras/modules/blob/261bfed3276bab4e410134748b14f9d04badea3a/src/Scanner.java#L132-L146) for Maven Group identifiers are defined as:

```java
String computeMavenGroupAlias(String group) {
  return switch (group) {
    case "com.fasterxml.jackson.core" -> "com.fasterxml.jackson";
    case "com.github.almasb" -> "com.almasb";
    case "javax.json" -> "java.json";
    case "net.colesico.framework" -> "colesico.framework";
    case "org.jetbrains.kotlin" -> "kotlin";
    case "org.jfxtras" -> "jfxtras";
    case "org.openjfx" -> "javafx";
    case "org.ow2.asm" -> "org.objectweb.asm";
    case "org.projectlombok" -> "lombok";
    case "org.swimos" -> "swim";
    default -> group.replace("-", "");
  };
}
```
...which also permits Maven Group identifiers to contain `-` characters, hence matching `org.foo-bar` to module names starting with `org.foobar`.

These aliases yield more hits increasing the number of unique modules. For example:

|                             Module | Group ID : Artifact ID |
|-----------------------------------:|------------------------|
| `com.fasterxml.jackson.core`       | `com.fasterxml.jackson.core:jackson-core` |
| `com.fasterxml.jackson.annotation` | `com.fasterxml.jackson.core:jackson-annotations` |
| `com.fasterxml.jackson.databind`   | `com.fasterxml.jackson.core:jackson-databind` |
| `com.fasterxml.jackson.kotlin`     | `com.fasterxml.jackson.module:jackson-module-kotlin` |
| `kotlin.stdlib`                    | `org.jetbrains.kotlin:kotlin-stdlib` |
| `kotlin.reflect`                   | `org.jetbrains.kotlin:kotlin-reflect` |
| `kotlin.stdlib.jdk7`               | `org.jetbrains.kotlin:kotlin-stdlib-jdk7` |
| `kotlin.stdlib.jdk8`               | `org.jetbrains.kotlin:kotlin-stdlib-jdk8` |
| `lombok`                           | `org.projectlombok:lombok` |
| `org.objectweb.asm`                | `org.ow2.asm:asm` |
| `org.objectweb.asm.tree`           | `org.ow2.asm:asm-tree` |
| `org.objectweb.asm.tree.analysis`  | `org.ow2.asm:asm-analysis` |
| `org.objectweb.asm.util`           | `org.ow2.asm:asm-util` |

Find a daily updated listing of unique modules at: [modules.properties](https://github.com/sormuras/modules/blob/main/com.github.sormuras.modules/com/github/sormuras/modules/modules.properties)

Find module `com.github.sormuras.modules` also attached as an executable JAR and [ToolProvider](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/spi/ToolProvider.html) in the assets of [releases/tag/0-ea](https://github.com/sormuras/modules/releases/tag/0-ea). Stable versions of it are published to [releases](https://github.com/sormuras/modules/releases); with [releases/latest](https://github.com/sormuras/modules/releases/latest) pointing to the latest stable release.

> Keep in mind, that the raw and filtered numbers may alter with changes made to the `Scanner` and the `modulescanner` programs. This also goes for changes made to the Maven Group Alias function.

## More Modules

The [doc](https://github.com/sormuras/modules/tree/main/doc) directory of the `sormuras/modules` project hosts lists of Maven `Group:Artifact` coordinates in 📜 text files. They are taken as an input of the scan process. The `Scanner` generates overview tables showing the state of modularization for each `Group:Artifact` coordinate.

You will find the following summary at the start of each overview.

- 🧩 denotes a Java module that contains a compiled module descriptor.
  It therefore provides a stable module name and an explicit modular API using `exports`, `provides`, `opens` and other directives.

- ⬜ denotes an automatic Java module, with its stable module name derived from `Automatic-Module-Name` manifest entry.
  Its API is derived from JAR content and therefore may **not** be stable.

- ⚪ denotes an automatic Java module, with its **not** stable module name derived from the JAR filename.
  Its API is derived from JAR content and therefore may **not** be stable.

- ➖ denotes an unrelated artifact, like BOM, POM, and other non-JAR packaging types.
  It also denotes old JAR files, as the scan process can only evaluate artifacts that were deployed after mid August 2018.

### WatchList

📜 [WatchList](https://github.com/sormuras/modules/tree/main/doc/WatchList.txt.md) overview.

Compiled from [WatchList.txt](https://github.com/sormuras/modules/tree/main/doc/WatchList.txt), which contains a community-curated list of Maven `Group:Artifact` lines.

### Top1000-2020

📜 [Top1000-2020.txt.md](https://github.com/sormuras/modules/tree/main/doc/Top1000-2020.txt.md)

[Top1000-2010.txt](https://github.com/sormuras/modules/tree/main/doc/Top1000-2020.txt) contains 1,000 Maven `Group:Artifact` lines sorted by download popularity as of December 2020. This list may include some non-JAR entries (`pom`, `bom`, ...). It also contains entries that were not updated since August 2018.

### Top1000-2019

📜 [Top1000-2019.txt.md](https://github.com/sormuras/modules/tree/main/doc/Top1000-2019.txt.md)

[Top1000-2019.txt](https://github.com/sormuras/modules/tree/main/doc/Top1000-2019.txt) contains 1,000 Maven `Group:Artifact` lines sorted by download popularity as of December 2019. This list also includes non-JAR entries (`pom`, `bom`, ...). It also contains entries that were not updated since August 2018.

## Summary And Outlook

Java modules are here to stay. Their number increase steadily.

Taking numbers of the **Top1000-2020** list as reference, I hope to see 20% Java modules here next year. This can be achieved by a) filtering out more unrelated artifacts and b) adding module descriptors to more libraries.

| Icon | Numbers | Description |
|------|-------------|-------------|
| 🧩  | 116 (11,6%) | Java modules (**module descriptor** with stable name and API) |
| ⬜  | 254 (25,4%) | Automatic Java modules (name derived from JAR **manifest**)
| ⚪  | 474 (47,4%) | Automatic Java modules (name derived from JAR **filename**)
| ➖  | 156 (15,6%) | Unrelated artifacts (BOM, POM, ... or not recently updated)

When do you publish your first Java module to Maven Central?

Cheers,
Christian