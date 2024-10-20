package com.mli.parallelstream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class M05_Set {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Set<String> lines = Files.lines(Path.of("files/words.txt")).collect(Collectors.toSet());
        List<String> list = new ArrayList<>(lines);

        Map<String, Long> threads = new ConcurrentHashMap<>();

        ForkJoinPool pool = new ForkJoinPool(8);
        pool.submit(() -> list.stream().parallel()
                .mapToInt(String::length)
                .peek(w -> threads.merge(Thread.currentThread().getName(), 1L, Long::sum))
                .sum()).get();
        System.out.println("=== List ===");
        threads.forEach((k, v) -> System.out.println(k + ": " + v));

        threads.clear();
        pool.submit(() -> lines.stream().parallel()
                .mapToInt(String::length)
                .peek(w -> threads.merge(Thread.currentThread().getName(), 1L, Long::sum))
                .sum()).get();
        System.out.println("=== Set ===");
        threads.forEach((k, v) -> System.out.println(k + ": " + v));

        pool.shutdown();
    }

}
