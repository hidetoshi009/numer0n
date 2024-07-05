package test_CUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NumeronServer {
    static int[] flag = new int[] { 0, 0 };
    static int[] itemcount = new int[] { 1, 1 };

    public static void main(String[] args) {
        while (true) {
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
                int[] answer1 = generateRandomNumber(in1, out1, out2);
                System.out.println("プレイヤー1の入力完了。");

                System.out.println("プレイヤー2に数値入力を求めます。");
                int[] answer2 = generateRandomNumber(in2, out2, out1);
                System.out.println("プレイヤー2の入力完了。");

                // ターンの概念をつくる。
                while (flag[0] == 0 && flag[1] == 0) {
                    playRound(in1, out1, out2, answer2, "プレイヤー1", 0, answer1);
                    playRound(in2, out2, out1, answer1, "プレイヤー2", 1, answer2);
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
    }

    // 初期値の入力
    private static int[] generateRandomNumber(BufferedReader in, PrintWriter out, PrintWriter enemy)
            throws IOException {
        out.println("3桁の異なる数字を入力してください: ");
        enemy.println("相手が数値を決めるまでお待ち下さい");
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

    // アイテム１
    private static String maxMin(int opponentAnswer) {
        if (5 <= opponentAnswer) {
            return "high";
        } else {
            return "low";
        }
    }

    // アイテム２
    private static int random() {
        Random random = new Random();
        return random.nextInt(2);
    }

    // 予想の入力
    private static void playRound(BufferedReader in, PrintWriter out, PrintWriter enemy, int[] answer,
            String playerName, int flagindex, int[] opponentAnswer)
            throws IOException {
        while (true) {
            out.println(playerName + "のターンです。予想を入力してください: ");
            enemy.println("相手の入力が終わるまでお待ち下さい");
            out.flush(); // フラッシュして即座に送信
            String input = in.readLine();
            System.out.println(playerName + "の入力: " + input); // デバッグメッセージ

            // アイテムの使用１
            if (input.equals("a")) {

                // アイテムの使用数を判定
                if (itemcount[flagindex] <= 2) {
                    // 相手の答えを表示
                    out.println("相手の答え: " + maxMin(opponentAnswer[0]) + " , " + maxMin(opponentAnswer[1]) + " , "
                            + maxMin(opponentAnswer[2]));
                    out.flush(); // フラッシュして即座に送信
                    itemcount[flagindex]++;
                    continue; // ターンを継続
                } else {
                    out.println("アイテムは2個までしか使用できません");
                    continue;
                }
            }

            if (input.equals("b")) {

                // アイテムの使用数を判定
                if (itemcount[flagindex] <= 2) {
                    out.println("どこかの数字が" + opponentAnswer[random()] + "です。");
                    itemcount[flagindex]++;
                    continue;
                } else {
                    out.println("アイテムは2個までしか使用できません");
                    continue;
                }
            }

            if (input.equals("c")) {
                if (itemcount[flagindex] <= 2) {
                    out.println("このアイテムは使用後、相手のターンに変わります。");
                    enemy.println("相手がアイテムCを使用しました");
                    out.flush(); // フラッシュして即座に送信

                    int[] newAnswer = generateRandomNumber(in, out, enemy);
                    System.arraycopy(newAnswer, 0, answer, 0, newAnswer.length);
                    itemcount[flagindex]++;
                    break; // ターン終了
                } else {
                    out.println("アイテムは2個までしか使用できません");
                    continue;
                }
            }

            // 不適切な値の入力
            if (input.length() != 3 || !input.matches("\\d{3}")) {
                out.println("無効な入力です。3桁の数字を入力してください。");
                out.flush(); // フラッシュして即座に送信
                continue; // ターンを継続
            }

            int[] guess = new int[3];
            for (int i = 0; i < 3; i++) {
                guess[i] = Character.getNumericValue(input.charAt(i));
            }

            if (!isValidGuess(guess)) {
                out.println("数字は異なる3桁でなければなりません。");
                out.flush(); // フラッシュして即座に送信
                continue; // ターンを継続
            }

            int[] result = evaluateGuess(answer, guess);
            out.println("EAT: " + result[0] + ", BITE: " + result[1]);
            enemy.println("相手の入力  EAT: " + result[0] + ", BITE: " + result[1]);
            out.flush(); // フラッシュして即座に送信

            if (result[0] == 3) {
                out.println("おめでとうございます！正解です！");
                out.flush(); // フラッシュして即座に送信
                flag[flagindex]++;
                break;
            }
            break;
        }
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
