package scatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ParameterizedClass
@MethodSource("arguments")
record ScatterF_ParameterizedClass(String caption, Executable executable) {

  @Test
  void test() {
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
