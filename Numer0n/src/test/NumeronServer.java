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
    static int[] flag = new int[] { 0, 0 };

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

            System.out.println("プレイヤー1に数値入力を求めます。");
            int[] answer1 = generateRandomNumber(in1, out1);
            System.out.println("プレイヤー1の入力完了。");

            System.out.println("プレイヤー2に数値入力を求めます。");
            int[] answer2 = generateRandomNumber(in2, out2);
            System.out.println("プレイヤー2の入力完了。");

            // TODOターンの概念をつくる。
            while (flag[0] == 0 && flag[1] == 0) {
                if (playRound(in1, out1, answer2, "プレイヤー1", 0)) {
                    System.out.println(flag[0]);
                }
                if (playRound(in2, out2, answer1, "プレイヤー2", 1)) {
                    System.out.println(flag[1]);
                }
            }

            // flagの値に応じて、結果の出力を変更する
            if (flag[0] == 1 && flag[1] == 0) {
                out1.println("あなたの勝利です");
                out2.println("プレイヤー1の勝利です");
            } else if (flag[0] == 0 && flag[1] == 1) {
                out1.println("プレイヤー2の勝利です");
                out2.println("あなたの勝利です");
            } else {
                out1.println("引き分けです");
                out2.println("引き分けです");
            }

            player1.close();
            player2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] generateRandomNumber(BufferedReader in, PrintWriter out) throws IOException {
        out.println("3桁の異なる数字を入力してください: ");
        out.flush(); // フラッシュして即座に送信
        while (true) {
            String input = in.readLine();
            System.out.println("入力を受信: " + input); // デバッグメッセージ
            if (input.length() != 3 || !input.matches("\\d{3}")) {
                out.println("無効な入力です。3桁の数字を入力してください: ");
                out.flush(); // フラッシュして即座に送信
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
                out.println("数字は異なる3桁でなければなりません。再度入力してください: ");
                out.flush(); // フラッシュして即座に送信
            }
        }
    }

    private static boolean playRound(BufferedReader in, PrintWriter out, int[] answer, String playerName, int flagindex)
            throws IOException {
        out.println(playerName + "のターンです。予想を入力してください: ");
        out.flush(); // フラッシュして即座に送信
        String input = in.readLine();
        System.out.println(playerName + "の入力: " + input); // デバッグメッセージ
        if (input.length() != 3 || !input.matches("\\d{3}")) {
            out.println("無効な入力です。3桁の数字を入力してください。");
            out.flush(); // フラッシュして即座に送信
            return false;
        }

        int[] guess = new int[3];
        for (int i = 0; i < 3; i++) {
            guess[i] = Character.getNumericValue(input.charAt(i));
        }

        if (!isValidGuess(guess)) {
            out.println("数字は異なる3桁でなければなりません。");
            out.flush(); // フラッシュして即座に送信
            return false;
        }

        int[] result = evaluateGuess(answer, guess);
        out.println("EAT: " + result[0] + ", BITE: " + result[1]);
        out.flush(); // フラッシュして即座に送信

        if (result[0] == 3) {
            out.println("おめでとうございます！正解です！");
            out.flush(); // フラッシュして即座に送信
            flag[flagindex]++;
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

        return new int[] { eat, bite };
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
