# module-version.properties

Intention: specify the required version of all modules(+1) referred to by all
module descriptors(+2) in your project.

A build tool can use the version information to retrieve the required artifact
or to package the version information into the artifact(s) created by it.

**all modules**(+1): you must specify an exact version for every internal and
external module that is a direct target of a "requires" statement. Indirectly
required modules need a version entry as well. It is up to the build tool to
provide an automatic functionality to figure out the versions of indirectly
required modules.

**all module descriptors**(+2): all `module-info.java` file in "main" and "test",
and any other *source set*.

## Example

```properties
# "internal" - hosted by this project
com.foo.bar = 1.4
com.foo.bas = 2.5-M2
com.foo.bat = 47.11-SNAPSHOT

# "external" - 3rd-party modules
org.slf4j = 1.8.0-beta0
org.apache.commons.lang3 = 1.0.0
org.junit.jupiter.api = 5.0.2
org.junit.jupiter.engine = 5.0.2
org.junit.jupiter.params = 5.0.2
org.junit.platform = 1.0.2
org.assertj.core = 3.8.0
```

# Modules and Maven Repository Coordinates

Having a `module-version.properties` specifying the exact versions of all
required modules it is almost possible to retrieve the artifacts needed for
the project. Only a mapping from the name of a module to a `GroupID` and
`ArtifactID` is missing.

Stephen Colebourne started collecting such a mapping here: https://github.com/jodastephen/jpms-module-names

Modules names will be as unique as GroupID nowadays. So it would be cool
if Maven supported a "modular view" on a repository.
Meaning, when I request "module: org.junit.jupiter.api" with "version: 5.0.2"
it automatically knows the correct group and artifact ID.

That idea is discussed here: https://github.com/jodastephen/jpms-module-names/issues/7

