import java.nio.file.Path;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.spi.ToolProvider;

/**
 * Lists observable tool providers.
 *
 * <p>Output reads like:
 *
 * <pre>{@code
 * jar (jdk.jartool@17/sun.tools.jar.JarToolProvider)
 *   Create, manipulate, inspect, and extract an archive of classes and resources.
 * javac (jdk.compiler@17/com.sun.tools.javac.main.JavacToolProvider)
 *   Read Java declarations and compile them into class files.
 * javadoc (jdk.javadoc@17/jdk.javadoc.internal.tool.JavadocToolProvider)
 *   Generate HTML pages of API documentation from Java source files.
 * javap (jdk.jdeps@17/com.sun.tools.javap.Main.JavapToolProvider)
 *   No description of javap available.
 * jdeps (jdk.jdeps@17/com.sun.tools.jdeps.Main.JDepsToolProvider)
 *   No description of jdeps available.
 * jlink (jdk.jlink@17/jdk.tools.jlink.internal.Main.JlinkToolProvider)
 *   No description of jlink available.
 * jmod (jdk.jlink@17/jdk.tools.jmod.Main.JmodToolProvider)
 *   No description of jmod available.
 * jpackage (jdk.jpackage@17/jdk.jpackage.internal.JPackageToolProvider)
 *   No description of jpackage available.
 * Listed 8 tools
 * }</pre>
 */
public class ListTools {
  public static void main(String... args) {
    var tools =
        ServiceLoader.load(ToolProvider.class).stream()
            .map(ServiceLoader.Provider::get)
            .sorted(Comparator.comparing(ToolProvider::name))
            .toList();
    tools.stream().map(ListTools::toString).forEach(System.out::print);
    System.out.printf("Listed %d tool%s%n", tools.size(), tools.size() == 1 ? "" : "s");
  }

  static String toString(ToolProvider provider) {
    return """
           %s (%s)
             %s.
           """
        .formatted(provider.name(), locate(provider), describe(provider));
  }

  static String locate(ToolProvider provider) {
    var type = provider.getClass();
    var module = type.getModule();
    var service = type.getCanonicalName();
    if (module.isNamed()) return module.getDescriptor().toNameAndVersion() + "/" + service;
    var source = type.getProtectionDomain().getCodeSource();
    if (source != null) {
      var location = source.getLocation();
      if (location != null)
        try {
          return Path.of(location.toURI()).resolve(service).toUri().toString();
        } catch (Exception ignore) {
          // fall-through and use unnamed module's string representation
        }
    }
    return module + "/" + service;
  }

  static String describe(ToolProvider provider) {
    return switch (provider.name()) {
      case "jar" -> "Create, manipulate, inspect, and extract an archive of classes and resources";
      case "javac" -> "Read Java declarations and compile them into class files";
      case "javadoc" -> "Generate HTML pages of API documentation from Java source files";
      default -> "No description of %s available".formatted(provider.name());
    };
  }
}
