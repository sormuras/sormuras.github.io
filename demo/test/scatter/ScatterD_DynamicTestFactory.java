package scatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ScatterD_DynamicTestFactory
{

  @TestFactory
  Stream<DynamicTest> test() {
    var object = new Object();
    return Stream.of(
        dynamicTest("constructor", () -> assertNotNull(object)),
        dynamicTest("equality", () -> assertNotEquals(new Object(), object)),
        dynamicTest("waitWithoutMonitorFails", () -> assertThrows(Exception.class, object::wait)));
  }
}
