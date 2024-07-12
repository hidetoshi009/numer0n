package test_db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class NumeronClient {

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private NumeronGUI gui;
    private boolean isMyTurn = true;

    public NumeronClient(String serverAddress) {
        initializeNetwork(serverAddress);
    }

    private void initializeNetwork(String serverAddress) {
        try {
            socket = new Socket(serverAddress, 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Initialize GUI
            SwingUtilities.invokeLater(() -> {
                gui = new NumeronGUI(this);
                receiveMessages();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    final String message = serverMessage;
                    SwingUtilities.invokeLater(() -> {
                        gui.displayServerMessage(message);
                        if (message.contains("相手のターンです。") || message.contains("入力してください") || message.contains("勝利です")) {
                            isMyTurn = true;
                        } else if (message.contains("相手の入力が終わるまでお待ち下さい") || message.contains("相手が")) {
                            isMyTurn = false;
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String message) {
        if (isMyTurn) {
            out.println(message);
        } else {
            gui.displayServerMessage("今はあなたのターンではありません。");
        }
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumeronClient("localhost"));
    }
}
