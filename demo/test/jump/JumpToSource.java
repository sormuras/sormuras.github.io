package jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class JumpToSource {

  @TestFactory
  Stream<DynamicTest> checkAllTextFiles() throws Exception {
    return Files.walk(Paths.get("demo/test/jump"), 1)
        .filter(path -> path.toString().endsWith(".txt"))
        .map(path -> dynamicTest(
                "> " + path.getFileName(),
                path.toUri(), // test source uri
                () -> checkLines(path)));
  }

  private void checkLines(Path path) throws Exception {
    var lines = Files.readAllLines(path);
    var expected = lines.get(1);
    var actual = new StringBuilder(lines.get(0)).reverse().toString();
    assertEquals(expected, actual, "Second line is not the reversed first!");
  }
}
