package org.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SequentialPrimeGap {
    private static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2 || number == 3) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public static long runAlg(int n) {
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

        System.out.println("\nПочаток обчислень (послідовний метод)...");
        long startTime = System.currentTimeMillis();

        int maxGap = 0;
        int prime1 = 0, prime2 = 0;
        int prevPrime = -1;

        for (int i = 2; i < n; i++) {
            if (isPrime(i)) {
                if (prevPrime != -1) {
                    int gap = i - prevPrime;
                    if (gap > maxGap) {
                        maxGap = gap;
                        prime1 = prevPrime;
                        prime2 = i;
                    }
                }
                prevPrime = i;
            }
        }


        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String resultText = String.format("Найбільший проміжок: %d (між простими числами %d та %d)\nЧас виконання: %d мс", maxGap, prime1, prime2, duration);

        System.out.println(resultText);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String header = String.format("=====\nАлгоритм: Послідовний - %s\nВхідне число: %d\n=====\n", timestamp, n);

        try (BufferedWriter outWriter = new BufferedWriter(new FileWriter("output.txt", true))) {
            outWriter.write(header);
            outWriter.write(resultText + "\n");
        } catch (IOException e) {

            System.err.println("Помилка запису у файл output.txt");
        }
        return duration;
    }
}