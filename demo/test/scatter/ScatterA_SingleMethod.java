package scatter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScatterA_SingleMethod {

  @Test
  void test() {
    var object = new Object();
    assertNotNull(object);
    assertNotEquals(new Object(), object);
    assertThrows(IllegalMonitorStateException.class, object::wait);
  }
}
