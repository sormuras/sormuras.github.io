# Closing jdk.internal.loader.Loader...

Why doesn't `jdk.internal.loader.Loader` implement `AutoCloseable`?

_or_

How to dispose a custom `ModuleLayer` gracefully if you don't start a new process?

```java
class JarLock {

  @Test
  void viaModuleLayer() throws Exception {  
    var jar = copyJar();
    
    var finder = ModuleFinder.of(jar.getParent());
    var parent = ModuleLayer.boot();
    var cf = parent.configuration().resolve(finder, ModuleFinder.of(), Set.of("com.greetings"));
    var scl = ClassLoader.getSystemClassLoader();
    var layer = parent.defineModulesWithOneLoader(cf, scl);
    
    var loader = layer.findLoader("com.greetings");
    var mainClass = loader.loadClass("com.greetings.Main");
    Assertions.assertEquals("Main", mainClass.getSimpleName());

    // loader.close(); // Where art thou?

    Files.delete(jar); // throws "FileSystemException", can't access "a.jar"...
  }
}    
```

Find more tests in [JarLock.java](https://github.com/sormuras/sormuras.github.io/tree/master/demo/test/jdk/JarLock.java).
It contains a case using `URLClassLoader` that shows the desired `close()` feature.

## Work-around via System.gc()

Alan Bateman suggested to [null out all references](http://mail.openjdk.java.net/pipermail/jigsaw-dev/2019-May/014228.html) in play.
That did the trick! ✅

Looking forward to the change of _"Windows sharing mode JarFile/ZipFile uses to open JAR files"_.

```java
class JarLock {

  @Test
  void viaModuleLayer() throws Exception {  
    // ...

    // loader.close(); // Where art thou?

    layer = null;
    loader = null;
    mainClass = null;

    System.gc();
    Thread.sleep(2000);
    Files.delete(jar); // ✅
  }
}    
```
