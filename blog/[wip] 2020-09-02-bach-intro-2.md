# 🎼 Bach.java - Intro 2 - World of Modules

Welcome to the series of blog posts describing how [Bach.java](https://github.com/sormuras/bach) builds modular Java projects.

## Project `bach-intro-2-world-of-modules`

- [X] Use IntelliJ [IDEA] to create an empty Java project: `bach-intro-2-world-of-modules`
- [ ] Build! [Bach]: `jshell https://sormuras.de/bach@11.8/build`

## Module `com.github.sormuras.wom`

- [ ] Create module: `com.github.sormuras.wom`
- [ ] Create class: `com.github.sormuras.wom.World`
- [ ] Create class: `com.github.sormuras.wom.Main`
- [ ] Run program: `.bach/workspace/image/bin/wom`

## Module `com.github.sormuras.wom.util`

- [ ] Create module: `com.github.sormuras.wom.util`
- [ ] Create class: `com.github.sormuras.wom.util.Modules`

## Include more Modules

- [ ] Include system module: `java.net.http`
- [ ] Include external module: `com.fasterxml.jackson.core`

## Custom Build Program

- [ ] Create build module: `build` in `.bach/src`
- [ ] Create build program: `build.Build`

## Testing in the World of Modules

- [ ] Create test module: `test.base`
- [ ] Create test module: `test.checks`
- [ ] Create test module: `com.github.sormuras.wom`


## Snippets

HttpClient

```java
    System.out.println("HttpClient -> " + HttpClient.class.getModule().getDescriptor().toNameAndVersion());
    var uri = URI.create("https://inside.java");
    var request = HttpRequest.newBuilder(uri).build();
    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(uri + " -> " + response.statusCode());
```

Jackson

```java
// requires com.fasterxml.jackson.core;
//   import com.fasterxml.jackson.core.JsonFactory;
    System.out.println("Jackson -> " + JsonFactory.class.getModule().getDescriptor().toNameAndVersion());
    var json = new StringWriter();
    try (var generator = new JsonFactory().createGenerator(json)) {
      generator.useDefaultPrettyPrinter();
      generator.writeStartObject();
      generator.writeStringField("name", "value");
      generator.writeEndObject();
    }
    System.out.println(json);
```


[IDEA]: https://www.jetbrains.com/idea
[Bach]: https://github.com/sormuras/bach
