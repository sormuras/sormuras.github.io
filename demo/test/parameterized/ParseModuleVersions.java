package parameterized;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.module.ModuleDescriptor.Version;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParseModuleVersions {

  @ParameterizedTest
  @ValueSource(strings = {"1", "2.3", "4.5-ea"})
  void test(String version) {
    assertDoesNotThrow(() -> Version.parse(version));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "2.3", "4.5-ea"})
  void test(Version version) {
    assertTrue(Version.parse("0").compareTo(version) < 0);
    assertEquals(version, version);
    assertTrue(Version.parse("10").compareTo(version) > 0);
  }
}
