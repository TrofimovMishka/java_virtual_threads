package org.sharing.virtualthreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SpringBootApplication
public class VirtualThreadsApplication {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path dir = Paths.get("/home/trofimov/Projects/Java/rbi/old-version-sources/dependencies/");
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        SpringApplication.run(VirtualThreadsApplication.class, args);


        long start = System.currentTimeMillis();

        executeVirtualThreadsPerTask();

//        executeWithFixedThreadPool(250);
//        executeWithFixedThreadPool(500);
//        executeWithFixedThreadPool(1000);
//        executeWithFixedThreadPool(1500);

        long end = System.currentTimeMillis();
        long timeExecution = end - start;
        System.out.printf("Time execution: %.2f s\n", timeExecution / 1000.0);
    }

    private static void executeWithFixedThreadPool(int numberOfThreads) {
        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads)) {
//        try (ExecutorService executor = Executors.newFixedThreadPool(50_000)) { // Process finished with exit code 134 (interrupted by signal 6:SIGABRT)
            IntStream.range(0, 15_000).forEach(i -> {
                Runnable worker = new WorkerThread(dir, counter, formatter);
                executor.execute(worker);
            });
            executor.shutdown();
        }
    }
    private static void executeVirtualThreadsPerTask() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 15_000).forEach(i -> {
                Runnable worker = new WorkerThread(dir, counter, formatter);
                executor.execute(worker);
            });
            executor.shutdown();
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); // main wait for releasing all virtual threads from scheduler queue
        }
    }

    private static void methodsToCreateVirtualThreads() throws InterruptedException {
        //1
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future1 = executor.submit(VirtualThreadsApplication::pass);
            var future2 = executor.submit(() -> pass());
        }

        //2
        Thread vt1 = Thread.ofVirtual().name("Bob").start(VirtualThreadsApplication::pass);
        vt1.join(); // force `main` wait

        //3

    }

    private static void pass(){
        System.out.printf("Pass from thread name = %s\n ", Thread.currentThread().getName());
    }
}
