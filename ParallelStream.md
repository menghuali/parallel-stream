# Parallel Stream
## What is Parallel Stream?
Java's parallel streams are part of the Streams API introduced in Java 8, designed to simplify and optimize the processing of large collections of data. A parallel stream enables parallel processing of data, meaning that the operations on the elements of the stream can be distributed across multiple threads to improve performance, especially when dealing with large data sets or complex computations.

By default, a Java stream is sequential, meaning its operations are processed in a single thread, one after the other. When you convert a stream to a parallel stream, it can utilize multiple threads from the ForkJoinPool common pool, splitting the data into chunks that can be processed concurrently.

### Key Concepts
* **Concurrency**: Parallel streams divide the source data into substreams, which are processed independently and simultaneously.
* **ForkJoinPool**: This is the thread pool used by parallel streams by default. You can control the parallelism level by setting the java.util.concurrent.ForkJoinPool system property. Also see [Configure Parallelism](#configure-parallelism).
* **Order**: Parallel streams may not preserve the encounter order of elements. If order is important, methods like ```forEachOrdered()``` should be used instead of ```forEach()```.
    ```java
    numbers.parallelStream()
       .forEachOrdered(System.out::println);
    ```
* **Associative Reduction**: the reduction of your stream processing must be associative. Otherwise, you will get incorrect results. See [Associative Reduction](#associative-reduction) for more details.

## How to Create a Parallel Stream?
You can create a parallel stream from the source using the ```parallelStream()``` method.
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
numbers.parallelStream()
       .forEach(System.out::println);
```

You can also convert a sequential stream into a parallel stream using the ```parallel()``` method.
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
Stream<Integer> stream = numbers.stream();
stream.parallel()
      .forEach(System.out::println);
```

## Associative Reduction
Associative reduction refers to a type of reduction operation that can be applied to parallel streams in Java (or similar frameworks) where the operation combines elements in such a way that the order of grouping does not affect the result. In other words, the operation is associative, meaning the grouping of operands does not change the outcome.

### Associative Property
For an operation to be **associative**, the following must hold true:
```
(𝑎 ⊕ 𝑏) ⊕ 𝑐 = 𝑎 ⊕ (𝑏 ⊕ 𝑐)
```
Where ```⊕``` is some binary operation like addition or multiplication.

Consider the following addition operation:
```
(2 + 3) + 4 = 2 + (3 + 4) = 9
```

Here, addition is associative because it doesn’t matter how you group the numbers — the result is the same.

Substraction is not associative. Consider the following subsctractions:
```
(5 - 3) - 1 = 1
5 - (3 - 1) = 3
```

**Example of Associative Reduction Operations:**
* Sum of elements (addition is associative)
* Multiplication of elements (multiplication is associative)
* Finding maximum/minimum (finding the maximum or minimum of elements is associative)
* String Concatenation
* Logical AND/OR (for boolean values)

**Non-Associative Example:**
* Subtraction
* Division

### Why Associativity Matters in Parallel Streams?
In parallel streams, the data is split into multiple substreams and processed concurrently across multiple threads. Each thread performs a reduction on its subset of the data, and the results are then combined. <span style="color:red">If the reduction operation is not associative, the final result can vary depending on how the data is split and combined, leading to incorrect results.</span>

## Performance Considerations
Parallel streams are most beneficial when:
* The operations on the stream elements are independent and can be performed in parallel.
* The workload is substantial enough to offset the overhead of managing multiple threads.

However, parallel streams may not always be faster, particularly if:
* The dataset is small.
* The operations are not CPU-bound (e.g., they involve I/O).
* There is high synchronization overhead or contention for shared resources. For example, ```Stream.limit()``` and ```Stream.findFirst()``` is high synchronization overhead, you should consider alternative reduction such as ```Stream.findAny()```. 

Other considerations:
* Autoboxing and Unboxing: avoid autoboxing and unboxing between primitive types and wrapper classes. See [Autoboxing and Unboxing](https://www.geeksforgeeks.org/autoboxing-unboxing-java/) for more details.
* Pointer Chasing: Use primitive types as much as possible. If using Object can't be avoided, choose cache-friendly data structures. See [How does Pointer Chasing impact performance](#how-does-pointer-chasing-impact-performance) for more details.
* CPU Cores: the performance of parellel streams depends on the number of CPU cores. So, you need to tune the parallelism of your program based on the CPU cores of your machine.
* Data structure: if a data set can be evenly and easily divided into subsets, parallel stream will be beneficial; otherwise, parallel stream can be even slower than sequecial stream. Data structures that is easy to divided: array and ```ArrayList```; samples of un-dividable data structure: file and ```Set```; data structures which are not easy to divide: ```LinkedList```.

### How does Pointer Chasing impact performance?
If the data in a collection are primitive types, CPU can load the actual data into the closer memory. However, if the data in a collection are objects, the collection actually have pointers to the objects. When processing these objects, CPU need to load the actual data from memory through the pointers. Finding and loading the actual data incurrs overhead.

So, to benefits the most from parallel stream, you should avoid Pointer Chasing as much as possible. See [Pointer Chasing](./PointerChasing.md) for more details.

### How to Benchmark Performance?
[Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh?tab=readme-ov-file#java-microbenchmark-harness-jmh) is a Java harness for building, running, and analysing nano/micro/milli/macro benchmarks written in Java and other languages targeting the JVM. It is widely considered the de facto standard for benchmarking Java performance at the micro-level. It is the preferred tool for measuring the performance of specific pieces of code (such as methods, algorithms, or loops). You use JMH to benchmark the performance of your stream process. See [Java Microbenchmark Harness (JMH)](./JMH.md) for more details.

When benchmarking performance, the test environment should be made as close as possible to the target environment on which your code will run.

## Configure Parallelism
You can control the parallelism level (i.e., the number of threads used for parallel processing) in Java's parallel streams by setting the size of the ForkJoinPool that handles the parallel tasks. This can be done either programmatically or through JVM system properties.

### Using System Property
You can set the parallelism level globally by defining the java.util.concurrent.ForkJoinPool.common.parallelism system property when starting your application. This property controls the number of threads in the common pool used by parallel streams.

Here’s how you can do it via the command line:

```bash
java -Djava.util.concurrent.ForkJoinPool.common.parallelism=8 MyApplication
```
In the above example, the parallelism level is set to 8, meaning the ForkJoinPool will use up to 8 threads for parallel stream processing.

### Programmatically Setting Parallelism
If you want to control the parallelism programmatically for specific sections of code without affecting the entire application, you can create a custom ```ForkJoinPool```. This allows you to avoid altering the global behavior of parallel streams in other parts of your application.

Here's how you can use a custom ```ForkJoinPool```:
```java
import java.util.concurrent.ForkJoinPool;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ParallelStreamExample {

    public static void main(String[] args) throws Exception {
        // Create a custom ForkJoinPool with a specific parallelism level
        ForkJoinPool customPool = new ForkJoinPool(4);  // 4 threads

        // Submit the task to the custom ForkJoinPool
        customPool.submit(() -> {
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            List<Integer> result = numbers.parallelStream()
                                          .map(num -> num * 2)
                                          .collect(Collectors.toList());

            System.out.println(result);
        }).get();  // Wait for the task to complete
    }
}
```
In this example:
* A custom ForkJoinPool is created with 4 threads.
* The parallel stream processing is submitted to the custom pool using submit().

This way, you can control the parallelism level locally, and it won’t interfere with other parts of the application that use the default common pool.

### Important Considerations
* **Global impact**: When using the -Djava.util.concurrent.ForkJoinPool.common.parallelism system property, it affects the parallelism level of all parallel streams and ForkJoinTasks that use the common pool.
* **Custom ForkJoinPool**: This approach is more flexible as it allows you to create different pools with varying levels of parallelism, ensuring that one section of the code can run in parallel without affecting the rest of the program.
* **Thread count**: The number you specify as parallelism represents the number of worker threads. Keep in mind that it's best to set this based on the number of available CPU cores to avoid excessive context switching and overhead.

## Reference
* [Pointer Chasing](./PointerChasing.md)
* [Autoboxing and Unboxing](https://www.geeksforgeeks.org/autoboxing-unboxing-java/)
* [Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh?tab=readme-ov-file#java-microbenchmark-harness-jmh)