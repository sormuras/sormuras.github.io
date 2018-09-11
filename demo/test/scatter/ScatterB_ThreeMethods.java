package scatter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScatterB_ThreeMethods {

  private final Object object = new Object();

  @Test
  void constructor() {
    assertNotNull(object);
  }

  @Test
  void equality() {
    assertNotEquals(new Object(), object);
  }

  @Test
  void waitFails() {
    assertThrows(IllegalMonitorStateException.class, object::wait);
  }
}
