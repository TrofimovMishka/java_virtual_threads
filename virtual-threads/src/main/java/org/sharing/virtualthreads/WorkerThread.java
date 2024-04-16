package org.sharing.virtualthreads;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class WorkerThread implements Runnable {
    private Path dir;
    private AtomicInteger counter;
    private DateTimeFormatter formatter;

    public WorkerThread(Path dir, AtomicInteger counter, DateTimeFormatter formatter){
        this.dir=dir;
        this.counter=counter;
        this.formatter=formatter;
    }

    @Override
    public void run() {
        simulateProcessorLoad(dir);
//        withoutProcessorLoad();
    }

    private void withoutProcessorLoad() {
        try {
            String formattedTime = LocalDateTime.now().format(formatter);
            Thread.currentThread().sleep(Duration.ofSeconds(1));
            String formattedTime2 = LocalDateTime.now().format(formatter);
            System.out.println(" ["+counter.getAndIncrement()+"] , 0 files processed from " + formattedTime + " to " + formattedTime2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void simulateProcessorLoad(Path dir) {
        try {
            String formattedTime = LocalDateTime.now().format(formatter);
            long count = fileCount(dir);
            String formattedTime2 = LocalDateTime.now().format(formatter);
            System.out.println(" ["+counter.getAndIncrement()+"], "+count + " files processed from " + formattedTime + " to " + formattedTime2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long fileCount(Path dir) throws IOException {
        try (Stream<Path> walkStream = Files.walk(dir, Integer.MAX_VALUE)) {
//        try (Stream<Path> walkStream = Files.walk(dir, Integer.MAX_VALUE).parallel()) { // mixing virtual threads with asynch. produce memory leak occur 2GB per 60 sec
            return walkStream
                    .filter(p -> !p.toFile().isDirectory())
                    .count();
        }
    }
}
