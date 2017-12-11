# module-version.properties

Location: project root

Intention: specify the required version of all modules referred to by all
module descriptors, that is all "module-info.java" files, in your project.

A build tool can use the version information to retrieve the matching artifact,
read JAR file.
 
"All modules": you must specify an exact version for every internal and
external module that is a direct target of a "requires" statement. Indirectly
required modules need a version entry as well. It is up to the build tool to
provide an automatic functionality to figure out the versions of indirectly
required modules.

"all module descriptors": "main" and "test", and any other "source set".


```javascript
# "internal" - hosted by this project
application = 47.11-M8
library = 2.4
tool = 44.3-SNAPSHOT

# "external" - 3rd-party modules
org.slf4j = 1.8.0
org.apache.commons.lang3 = 1.0.0
org.junit.jupiter.api = 5.0.1
org.junit.jupiter.engine = 5.0.1
org.junit.jupiter.params = 5.0.1
org.junit.platform = 1.0.1
org.assertj.core = 3.8.0
```


# Maven and Modules

Having a `module-version.properties` specifying the exact versions of all
required modules it is almost possible to retrieve the artifacts needed for
the project. Only a mapping from the name of a module to a `GroupID` and
`ArtifactID` is missing.

Here's a mapping using properties syntax:

	[group]
	org.junit.jupiter.api = org.junit.jupiter
	[artifact]
	org.junit.jupiter.api = junit-jupiter-api
	[version]
	org.junit.jupiter.api = 5.0.1

Modules names will be as unique as GroupID nowadays. So it would be cool
if Maven supported a "modular view" on a repository.
Meaning, when I request "module: org.junit.jupiter.api" with "version: 5.0.1"
it automatically knows the correct group and artifact ID.

