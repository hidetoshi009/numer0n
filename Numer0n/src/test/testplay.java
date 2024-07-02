package test;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class testplay {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] answer = generateRandomNumber();
        System.out.println("Numer0nへようこそ！3桁の異なる数字を当ててください。");

        while (true) {
            System.out.print("あなたの予想を入力してください: ");
            String input = scanner.nextLine();
            if (input.length() != 3 || !input.matches("\\d{3}")) {
                System.out.println("無効な入力です。3桁の数字を入力してください。");
                continue;
            }

            int[] guess = new int[3];
            for (int i = 0; i < 3; i++) {
                guess[i] = Character.getNumericValue(input.charAt(i));
            }

            if (!isValidGuess(guess)) {
                System.out.println("数字は異なる3桁でなければなりません。");
                continue;
            }

            int[] result = evaluateGuess(answer, guess);
            System.out.println("EAT: " + result[0] + ", BITE: " + result[1]);

            if (result[0] == 3) {
                System.out.println("おめでとうございます！正解です！");
                break;
            }
        }

        scanner.close();
    }

    private static int[] generateRandomNumber() {
        Random random = new Random();
        Set<Integer> digits = new HashSet<>();
        int[] number = new int[3];
        int index = 0;

        while (digits.size() < 3) {
            int digit = random.nextInt(10);
            if (digits.add(digit)) {
                number[index++] = digit;
            }
        }

        return number;
    }

    private static boolean isValidGuess(int[] guess) {
        Set<Integer> digits = new HashSet<>();
        for (int digit : guess) {
            if (!digits.add(digit)) {
                return false;
            }
        }
        return true;
    }

    private static int[] evaluateGuess(int[] answer, int[] guess) {
        int eat = 0;
        int bite = 0;

        for (int i = 0; i < 3; i++) {
            if (guess[i] == answer[i]) {
                eat++;
            } else if (contains(answer, guess[i])) {
                bite++;
            }
        }

        return new int[]{eat, bite};
    }

    private static boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
}
