package scatter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ScatterE_Parameterized {

  @ParameterizedTest
  @MethodSource("arguments")
  void test(String caption, Executable executable) {
    assertDoesNotThrow(executable, caption);
  }

  static Stream<Arguments> arguments() {
    var object = new Object();
    return Stream.of(
        Arguments.of("constructor", (Executable) () -> assertNotNull(object)),
        Arguments.of("equality", (Executable) () -> assertNotEquals(new Object(), object)),
        Arguments.of("wait", (Executable) () -> assertThrows(Exception.class, object::wait)));
  }
}
