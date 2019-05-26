# Closing `jdk.internal.loader.Loader`...

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

    // loader = null; // Collect garbage...
    // System.gc(); System.gc();  System.gc();
    // Thread.yield(); Thread.sleep(1000); Thread.yield();

    Files.delete(jar); // throws "FileSystemException", can't access "a.jar"...
  }
}    
```

Find more tests in [JarLock.java](https://github.com/sormuras/sormuras.github.io/tree/master/demo/test/jdk/JarLock.java).
It contains a case using `URLClassLoader` that shows the desired `close()` feature.
