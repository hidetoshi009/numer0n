package test_CUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NumeronClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage); // デバッグメッセージ
                if (serverMessage.contains("入力してください")) {
                    String userInput = stdIn.readLine();
                    out.println(userInput);
                    out.flush(); // フラッシュして即座に送信
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}