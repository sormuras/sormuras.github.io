package jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class JumpToSource {

  @TestFactory
  Stream<DynamicTest> walkAllTextFiles() throws Exception {
    return Files.walk(Path.of("demo/test/jump"), 1)
        .filter(path -> path.toString().endsWith(".txt"))
        .map(this::newDynamicTest);
  }

  @TestFactory
  Iterable<DynamicTest> globAllTextFiles() throws Exception {
    var path = Path.of("demo/test/jump");
    var tests = new ArrayList<DynamicTest>();
    Files.newDirectoryStream(path, "*.txt").forEach(file -> tests.add(newDynamicTest(file)));
    return tests;
  }

  private DynamicTest newDynamicTest(Path file) {
    var displayName = "Checking file '" + file.getFileName() + "'";
    return dynamicTest(displayName, file.toUri(), () -> checkLines(file));
  }

  private void checkLines(Path file) throws Exception {
    var lines = Files.readAllLines(file);
    assertEquals(
        2,
        lines.size(),
        "Expected exactly two lines, but read " + lines.size() + " line(s) from " + file.toUri());
    var expected = lines.get(1);
    var actual = new StringBuilder(lines.get(0)).reverse().toString();
    assertEquals(expected, actual, "Second line is not the reversed first!");
  }
}
