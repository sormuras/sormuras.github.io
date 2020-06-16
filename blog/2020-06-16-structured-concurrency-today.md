# Structured Concurrency - Today

Alan Bateman describes the core concept of [Structured Concurrency](https://wiki.openjdk.java.net/display/loom/Structured+Concurrency) and provides some examples at the OpenJDK Wiki.

In this blog I explain how to achieve something similar with the features and API provided by Java **14**, today.
Yes, Java 8 should be fine, too.
Replace `var` keyword with explicit types declarations and unroll the `try-with-resource` blocks.

## AutoCloseableExecutorService

Ron Pressler writes in the [**State of Loom**](https://cr.openjdk.java.net/~rpressler/loom/loom/sol1_part2.html#structured-concurrency) article

> In our current prototype we represent a structured concurrency scope,
> the code block that confines the lifetime of child threads, by
> making the `java.util.concurrent.ExecutorService` an `AutoCloseable`,
> with close shutting down the service and awaiting termination.
> This guarantees that all tasks submitted to the service will
> have terminated by the time we exit the try-with-resources
> block [...]

Let's take some [inspiration](https://github.com/openjdk/loom/blob/7a081efa66f3779bc9e7db2c54930c52fd3b4823/src/java.base/share/classes/java/util/concurrent/ExecutorService.java#L378-L424) how that "shutdown-on-close" works.
Place that inspiration into a service-decorating class.

```java
public class AutoCloseableExecutorService implements AutoCloseable, ExecutorService {

  private final ExecutorService service;

  public AutoCloseableExecutorService(ExecutorService service) {
    this.service = service;
  }

  @Override
  public void close() {
    boolean terminated = isTerminated();
    if (!terminated) {
        shutdown();
        // wait for termination...
    }
  }

  @Override
  public boolean isTerminated() {
    return service.isTerminated();
  }

  @Override
  public void shutdown() {
    service.shutdown();
  }
 
  // remaining API from ExecutorService...
...
```

## ThreadFactory and Executors

Next, replace `Thread.builder().virtual().factory()` with:

```
Executors.defaultThreadFactory()
```

The default thread factory creates heavy-weight normal threads.
That's all we got, without Loom's Virtual Threads. 


Also, the `Executors.newThreadExecutor(factory)` doesn't exist today.
Let's use a fixed-size thread pool executor service:

```java
Executors.newFixedThreadPool(nThreads, factory)
```

## Sticking it all together

Finally, here's the example showing structured concurrency without using Loom.
The test program:

```java

System.out.println("BEGIN");

var factory = Executors.defaultThreadFactory();

try (var executor = new AutoCloseableExecutorService(Executors.newFixedThreadPool(1, factory))) {

  executor.submit(this::printThreadNameAndSleep);

  try (var nested = new AutoCloseableExecutorService(Executors.newFixedThreadPool(2, factory))) {
    nested.submit(this::printThreadNameAndSleep);
    nested.submit(this::printThreadNameAndSleep);
    nested.submit(this::printThreadNameAndSleep);
  }

  executor.submit(this::printThreadNameAndSleep);
}

System.out.println("END.");

  int printThreadNameAndSleep() throws Exception {
    System.out.println(Thread.currentThread().getName());
    Thread.sleep(500);
    return 0;
  }
}
```

yields (or slightly different, depending on which thread gets to assigned to work)

```text
BEGIN
pool-1-thread-1
pool-1-thread-2
pool-1-thread-3
pool-1-thread-2
pool-1-thread-1
END.
```

So far so good.

I'm looking forward to the next project Loom updates and seeing it in the Java main line, soon!
In the meantime, test-driving Loom with https://github.com/sormuras/junit5-looming ... 1.000.000 tests in parallel. 

## Want more?

- https://wiki.openjdk.java.net/display/loom/Structured+Concurrency **Structured Concurrency**, Alan Bateman, June 2020
- https://cr.openjdk.java.net/~rpressler/loom/loom/sol1_part1.html **State of Loom** Ron Pressler, May 2020
- https://jdk.java.net/loom **Project Loom Early-Access Builds**
- https://www.youtube.com/watch?v=NV46KFV1m-4 **Project Loom Update** Alan Bateman, Rickard Bäckman, July 2019
