package scatter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScatterC_AssertAll {

  @Test
  void test() {
    var object = new Object();
    assertAll(
        () -> assertNotNull(object),
        () -> assertNotEquals(new Object(), object),
        () -> assertThrows(IllegalMonitorStateException.class, object::notify));
  }
}
