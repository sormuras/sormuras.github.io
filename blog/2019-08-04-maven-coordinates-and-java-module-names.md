# Maven Coordinates and Java Module Names

This is an example of my current naming pattern for Maven Coordinates in modular Java world: 

Maven

- **Group** `de.sormuras.blog`
- **Artifact** `de.sormuras.blog.example`
- **Version** `1-ea`

Java

- **Java Module Name** `de.sormuras.blog.example`
- **Java Module Version** `1-ea`
- **Java Package Name Pattern** `de.sormuras.blog.example[.<NAME>]*`

## In words
- The Java Module Name equals the Maven Artifact ID
- Let the Maven Artifact ID start exactly with the Group ID
- Use only valid Java names between the dots
- All packages contained within the module must start with module name
- The version string must be parseable by ModuleDescriptor.Version

## Benefits
- Artifact JAR file name starts with Java module name, i.e. module name in plain sight
- No split packages by default
- Version comparision via foundation algorithm in ModuleDescriptor.Version

## That'd be nice
- "Easier" mapping from Java module name to Maven GA coordinates
- Encode number of Maven Group elements into Version string?
  - `a.b.c:a.b.c.d:4.5-ALPHA-(3)` with `-(3)` the first three elements are the Group ID.

## Open ends
- What to do with Maven Classifiers?
