package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class NumeronServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("サーバーが起動しました。クライアントの接続を待っています...");

            Socket player1 = serverSocket.accept();
            System.out.println("プレイヤー1が接続しました。");
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));

            Socket player2 = serverSocket.accept();
            System.out.println("プレイヤー2が接続しました。");
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            out1.println("あなたはプレイヤー1です。");
            out2.println("あなたはプレイヤー2です。");

            int[] answer1 = generateRandomNumber(in1, out1);
            int[] answer2 = generateRandomNumber(in2, out2);

            while (true) {
                if (playRound(in1, out1, answer2, "プレイヤー1")) {
                    break;
                }
                if (playRound(in2, out2, answer1, "プレイヤー2")) {
                    break;
                }
            }

            player1.close();
            player2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] generateRandomNumber(BufferedReader in, PrintWriter out) throws IOException {
        out.print("3桁の異なる数字を入力してください: ");
        out.flush();  // フラッシュして即座に送信
        while (true) {
            String input = in.readLine();
            System.out.println("入力を受信: " + input);  // デバッグメッセージ
            if (input.length() != 3 || !input.matches("\\d{3}")) {
                out.print("無効な入力です。3桁の数字を入力してください: ");
                out.flush();  // フラッシュして即座に送信
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
                out.print("数字は異なる3桁でなければなりません。再度入力してください: ");
                out.flush();  // フラッシュして即座に送信
            }
        }
    }

    private static boolean playRound(BufferedReader in, PrintWriter out, int[] answer, String playerName) throws IOException {
        out.print(playerName + "のターンです。予想を入力してください: ");
        out.flush();  // フラッシュして即座に送信
        String input = in.readLine();
        System.out.println(playerName + "の入力: " + input);  // デバッグメッセージ
        if (input.length() != 3 || !input.matches("\\d{3}")) {
            out.println("無効な入力です。3桁の数字を入力してください。");
            out.flush();  // フラッシュして即座に送信
            return false;
        }

        int[] guess = new int[3];
        for (int i = 0; i < 3; i++) {
            guess[i] = Character.getNumericValue(input.charAt(i));
        }

        if (!isValidGuess(guess)) {
            out.println("数字は異なる3桁でなければなりません。");
            out.flush();  // フラッシュして即座に送信
            return false;
        }

        int[] result = evaluateGuess(answer, guess);
        out.println("EAT: " + result[0] + ", BITE: " + result[1]);
        out.flush();  // フラッシュして即座に送信

        if (result[0] == 3) {
            out.println("おめでとうございます！" + playerName + "が正解です！");
            out.flush();  // フラッシュして即座に送信
            return true;
        }
        return false;
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
