# Java Microbenchmark Harness (JMH)
JMH is a Java harness, developed by OpenJDK team, for building, running, and analysing nano/micro/milli/macro benchmarks written in Java and other languages targeting the JVM.

**Sample JMH Report**
```sql
Benchmark                                          (N)  Mode  Cnt       Score       Error  Units
M05_SourceSplit.process_int_list              10000000  avgt   15    6595.717 ±   396.711  us/op
M05_SourceSplit.process_int_list_parallel     10000000  avgt   15    1963.705 ±    65.178  us/op
M05_SourceSplit.process_int_set               10000000  avgt   15  229183.951 ± 22124.206  us/op
M05_SourceSplit.process_int_set_parallel      10000000  avgt   15   37577.516 ±  5254.488  us/op
M05_SourceSplit.process_string_list           10000000  avgt   15      18.013 ±     0.305  us/op
M05_SourceSplit.process_string_list_parallel  10000000  avgt   15      27.133 ±     3.040  us/op
M05_SourceSplit.process_string_set            10000000  avgt   15      20.005 ±     0.396  us/op
M05_SourceSplit.process_string_set_parallel   10000000  avgt   15      36.134 ±     0.799  us/op
```

**Key Features of JMH:**
* **Accurate benchmarking**: JMH takes care of JVM optimizations, including JIT compilation, garbage collection, and warm-up phases, ensuring accurate performance measurement.
* **Ease of use**: Provides annotations and configurations to control benchmark behavior, making it easy to write benchmarks.
* **Multiple measurement modes**: Measures throughput, latency, average time, and sample time.
* **Customizable warm-up**: Allows specifying how many iterations to run for warm-up and how many iterations to measure.
* **Multi-threaded benchmarking**: Supports benchmarking with multiple threads to evaluate performance in concurrent environments.

## How to use JMH?
JMH's offical document describes the preferred way to use JMH: [Preferred Usage: Command Line](https://github.com/openjdk/jmh?tab=readme-ov-file#preferred-usage-command-line).

Also, you can add JMH dependencies to your Maven or Gradle project.

Maven dependency:
```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.37</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.37</version>
</dependency>
```

Gradle dependency
```groovy
dependencies {
    implementation 'org.openjdk.jmh:jmh-core:1.37'
    annotationProcessor 'org.openjdk.jmh:jmh-generator-annprocess:1.37'
}
```

## Writing a Basic JMH Benchmark
Here’s how you can create a basic benchmark using JMH.
1. Setup the benchmark class using JMH annotations.
2. Use ```@Benchmark``` to mark the method you want to benchmark.
3. Configure warm-up and measurement iterations if needed.

Example of a Simple JMH Benchmark:
```java
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Measurement;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)  // Each benchmark thread gets its own instance
public class MyBenchmark {

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1) // Run 2 forks, with 1 warmup iteration
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)  // Warmup for 3 iterations of 1 second each
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS) // Measure for 5 iterations of 1 second each
    public void testMethod() {
        // Code you want to benchmark
        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += i;
        }
    }
}
```

### Key Annotations
* ```@Benchmark```: Marks a method as a benchmark target.
* ```@State```: Defines the scope of benchmark instances. ```Scope.Thread``` ensures each benchmark thread gets a separate instance.
* ```@Fork```: Specifies how many times the benchmark will run in a separate JVM. More forks can give more reliable results.
* ```@Warmup```: Defines warm-up iterations that help the JVM optimize and stabilize before actual measurement
* ```@Measurement```: Defines how many times the actual measurement will run after the warm-up phase.
* ```@OutputTimeUnit```: Specifies the time unit for output, such as milliseconds or microseconds.
* ```@BenchmarkMode```: Benchmark mode declares the default modes in which this benchmark would run. See [Modes of Benchmarking](#modes-of-benchmarking) for available benchmark modes.

### Modes of Benchmarking:
Through ```org.openjdk.jmh.annotations.Mode```, JMH provides different modes for measuring the performance of code:
* **Throughput**: Measures how many operations per time unit can be completed.
* **AverageTime**: Measures the average time it takes for each operation.
* **SampleTime**: Measures time for a sample of operations, giving statistical data (e.g., percentiles).
* **SingleShot**: Measures time for a single execution of the benchmark, useful for cold start benchmarks.
* **All**: Runs the benchmark in all modes.

## Reference
* [Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh?tab=readme-ov-file#java-microbenchmark-harness-jmh)
* [Preferred Usage: Command Lin](https://github.com/openjdk/jmh?tab=readme-ov-file#preferred-usage-command-line)
* [JMH Code Samples](https://github.com/openjdk/jmh/tree/master/jmh-samples/src/main/java/org/openjdk/jmh/samples)

## Run Benchmark
If you're using Maven, you can run the benchmark using the JMH plugin:

```bash
mvn clean install
java -jar target/benchmarks.jar
```

You can also pass specific options to customize the benchmark run. For example, to run a specific benchmark method:
``` bash
java -jar target/benchmarks.jar MyBenchmark.testMethod
```