# Invalid `Automatic-Module-Name`s

Already [over 600](https://github.com/jodastephen/jpms-module-names/blob/master/error/error-syntax.txt) modules were uploaded to Maven Central lately, containing invalid names!

TLDR: Run `jar --describe-module --file ARTIFACT.jar` to verify the selected module name adheres at least valid Java syntax before publishing.

## Examples of invalid names

- `org.apache.jena.jena-fuseki-core` ❌ no `-` allowed
- `org.drools.wb.enum.editor.api` ❌ `enum` is a Java keyword
- `org.kie.wb.common.default.editor.api` ❌ `default` is a Java keyword
- `org.neo4j.tooling.import` ❌ `import` is a Java keyword
- `org.talend.sdk.component.runtime..standalone` ❌ no `..` allowed

If you find your published library, or that of a friend, listed below [suspicious modules](https://github.com/jodastephen/jpms-module-names/blob/master/README.md#suspicious-modules), please correct the module name as soon as possible.

An invalid module name is even worse than no module name.

## How to name a module?

This summary is copied over from [module-names-for-java-se-9-jpms](https://github.com/jodastephen/jpms-module-names#module-names-for-java-se-9-jpms):

* Module names must be valid Java identifiers! I.e. no Java keywords, no dashes, no...
* Module names must be reverse-DNS, just like package names, e.g. `org.joda.time`.
* Modules are a group of packages. As such, the module name must be related to the package names.
* Module names are strongly recommended to be the same as the name of the super-package.
* Creating a module with a particular name takes ownership of that package name and everything beneath it.
* As the owner of that namespace, any sub-packages may be grouped into sub-modules as desired so long as no package is in two modules.

Make sure to also read the full blog at [java-se-9-jpms-module-naming](https://blog.joda.org/2017/04/java-se-9-jpms-module-naming.html)

## Build tool improvements

- Jar ?
- Maven Archiver [MSHARED-773](https://issues.apache.org/jira/projects/MSHARED/issues/MSHARED-773)
- Gradle "Core" [7752](https://github.com/gradle/gradle/issues/7752)
