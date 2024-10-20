package com.mli.parallelstream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class M04_ForkJoinPool {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Map<String, Long> threads = new ConcurrentHashMap<>();
        ForkJoinPool pool = null;
        try {
            pool = new ForkJoinPool(4);
            int sum = pool.submit(() -> {
                return IntStream.range(0, 1_000_000)
                        .parallel()
                        .peek(i -> threads.merge(Thread.currentThread().getName(), 1L, Long::sum))
                        .sum();
            }).get();
            System.out.println(sum);
            threads.forEach((k, v) -> System.out.println(k + " -> " + v));
        } finally {
            if (pool != null)
                pool.shutdown();
        }
    }

}
