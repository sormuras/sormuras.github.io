package jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.module.ModuleFinder;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;

// "ZipFile/JarFile should open zip/JAR file with FILE_SHARE_DELETE sharing mode on Windows"
// See: https://bugs.openjdk.java.net/browse/JDK-8224794
class JarLock {

  private static Path copyJar() throws Exception {
    var temp = Files.createTempDirectory("jar-lock-");
    return Files.copy(Path.of("demo/test/jdk/com.greetings.jar"), temp.resolve("a.jar"));
  }

  @Test
  void deleteJar() throws Exception {
    var jar = copyJar();

    assertTrue(Files.exists(jar));
    Files.delete(jar);
    assertTrue(Files.notExists(jar));
  }

  @Test
  void viaURLClassLoader() throws Exception {
    var jar = copyJar();

    var loader = URLClassLoader.newInstance(new URL[] {jar.toUri().toURL()});
    var mainClass = loader.loadClass("com.greetings.Main");
    assertEquals("Main", mainClass.getSimpleName());

    // Files.delete(jar); // throws "FileSystemException", can't access "a.jar"...
    assertThrows(Exception.class, () -> Files.delete(jar));
  }

  @Test
  void viaURLClassLoaderAndClose() throws Exception {
    var jar = copyJar();

    var loader = URLClassLoader.newInstance(new URL[] {jar.toUri().toURL()});
    var mainClass = loader.loadClass("com.greetings.Main");
    assertEquals("Main", mainClass.getSimpleName());

    loader.close();

    Files.delete(jar);
    assertTrue(Files.notExists(jar));
  }

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
    assertEquals("Main", mainClass.getSimpleName());

    // loader.close(); // Where art thou?

    layer = null;
    loader = null;
    mainClass = null;

    System.gc();
    Thread.sleep(2000);
    Files.delete(jar);
  }
}
