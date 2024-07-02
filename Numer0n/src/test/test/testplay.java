package test;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class testplay {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] answer = generateRandomNumber(scanner); // 手動で数字を入力
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

    // 手動で3桁の異なる数字を入力するメソッド
    private static int[] generateRandomNumber(Scanner scanner) {
        System.out.print("3桁の異なる数字を入力してください: ");
        while (true) {
            String input = scanner.nextLine();
            if (input.length() != 3 || !input.matches("\\d{3}")) {
                System.out.println("無効な入力です。3桁の数字を入力してください: ");
                continue;
            }

            int[] number = new int[3];
            Set<Integer> digits = new HashSet<>();
            boolean valid = true;

            for (int i = 0; i < 3; i++) {
                number[i] = Character.getNumericValue(input.charAt(i));
                if (!digits.add(number[i])) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                return number;
            } else {
                System.out.println("数字は異なる3桁でなければなりません。再度入力してください: ");
            }
        }
    }

    // 入力された数字が異なる3桁かどうかをチェックするメソッド
    private static boolean isValidGuess(int[] guess) {
        Set<Integer> digits = new HashSet<>();
        for (int digit : guess) {
            if (!digits.add(digit)) {
                return false;
            }
        }
        return true;
    }

    // 予想した数字と答えの数字を比較し、EATとBITEの数を返すメソッド
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

    // 数字が配列に含まれているかどうかをチェックするメソッド
    private static boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
}
