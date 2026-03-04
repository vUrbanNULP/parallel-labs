package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введіть N: ");
        int userInputN = sc.nextInt();
        System.out.print("Скільки макс беремо потоків?: ");
        int userInputT = sc.nextInt();

        System.out.printf("Обрано потоків: %d\n", userInputT);
        long seqTime = SequentialPrimeGap.runAlg(userInputN);
        for (int currentThreads = 2; currentThreads <= userInputT; currentThreads++) {
            long parTime = ParallelPrimeGap.runAlg(userInputN, userInputT);
            System.out.printf("%d | %d | %d\n", currentThreads, seqTime, parTime);
        }

    }
}
