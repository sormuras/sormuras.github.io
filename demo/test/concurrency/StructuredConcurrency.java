package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class StructuredConcurrency {

  @Test
  void today() {
    System.out.println("BEGIN");

    var factory = Executors.defaultThreadFactory();

    try (var executor = shutdownOnClose(Executors.newFixedThreadPool(1, factory))) {

      executor.submit(this::printThreadNameAndSleep);

      try (var nested = shutdownOnClose(Executors.newFixedThreadPool(2, factory))) {
        nested.submit(this::printThreadNameAndSleep);
        nested.submit(this::printThreadNameAndSleep);
        nested.submit(this::printThreadNameAndSleep);
      }

      executor.submit(this::printThreadNameAndSleep);
    }

    System.out.println("END.");
  }

  AutoCloseableExecutorService shutdownOnClose(ExecutorService service) {
    return new AutoCloseableExecutorService(service);
  }

  int printThreadNameAndSleep() throws Exception {
    System.out.println(Thread.currentThread().getName());
    Thread.sleep(500);
    return 0;
  }
}
