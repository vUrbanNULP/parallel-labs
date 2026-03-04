package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParallelPrimeGap {
    private static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2 || number == 3) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }

    static class PrimeFinder implements Runnable {
        private final int startRange;
        private final int endRange;
        private final List<Integer> primesFound = new ArrayList<>();

        public PrimeFinder(int startRange, int endRange) {
            this.startRange = startRange;
            this.endRange = endRange;
        }

        @Override
        public void run() {
            for (int i = startRange; i <= endRange; i++) {
                if (isPrime(i)) {
                    primesFound.add(i);
                }
            }
        }

        public List<Integer> getPrimesFound() {
            return primesFound;
        }
    }

    public static long runAlg(int n, int numThreads) {
//        Нижче знаходиться частина старого коду зчитування з файлів, до виконання завдання під час захисту.
//        int n;
//        File inputFile = new File("input.txt");
//
//        if (inputFile.exists()) {
//            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
//                n = Integer.parseInt(reader.readLine().trim());
//                System.out.println("Прочитано n = " + n + " з файлу input.txt");
//            } catch (IOException | NumberFormatException e) {
//                System.err.println("Помилка читання файлу. Переконайтесь, що там лише ціле число.");
//                return;
//            }
//        } else {
//            Random rand = new Random();
//            n = rand.nextInt(10_000_000) + 10_000_000;
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile))) {
//                writer.write(String.valueOf(n));
//                System.out.println("Файл input.txt не знайдено. Згенеровано n = " + n + " та записано у файл.");
//            } catch (IOException e) {
//                System.err.println("Помилка запису у файл input.txt");
//                return;
//            }
//        }

        System.out.println("\nПочаток обчислень (паралельний метод)...");

        //int numThreads = Runtime.getRuntime().availableProcessors();
        //System.out.println("Задіяно потоків: " + numThreads);

        long startTime = System.currentTimeMillis();

        int rangeSize = (n - 2) / numThreads;
        List<PrimeFinder> tasks = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int start = 2 + (i * rangeSize);
            int end = (i == numThreads - 1) ? n - 1 : start + rangeSize - 1;

            PrimeFinder task = new PrimeFinder(start, end);
            tasks.add(task);

            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int maxGap = 0;
        int prime1 = 0, prime2 = 0;
        int prevPrime = -1;

        for (PrimeFinder task : tasks) {
            for (int currentPrime : task.getPrimesFound()) {
                if (prevPrime != -1) {
                    int gap = currentPrime - prevPrime;
                    if (gap > maxGap) {
                        maxGap = gap;
                        prime1 = prevPrime;
                        prime2 = currentPrime;
                    }
                }
                prevPrime = currentPrime;
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String resultText = String.format("Найбільший проміжок: %d (між простими числами %d та %d)\nЧас виконання: %d мс",
                maxGap, prime1, prime2, duration);


        System.out.println(resultText);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String header = String.format("=====\nАлгоритм: Паралельний - %s\nВхідне число: %d\n=====\n", timestamp, n);

        try (BufferedWriter outWriter = new BufferedWriter(new FileWriter("output.txt", true))) {
            outWriter.write(header);
            outWriter.write(resultText + "\n");
        } catch (IOException e) {
            System.err.println("Помилка запису у файл output.txt");
        }
        return duration;
    }
}