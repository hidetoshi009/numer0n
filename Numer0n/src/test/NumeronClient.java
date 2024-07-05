package test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class NumeronClient extends JFrame {

    private List<Integer> selectedNumbers;
    private JLabel selectedLabel;
    private JTextArea messageArea;
    private PrintWriter out;
    private BufferedReader in;
    private JPanel cardPanel;

    public NumeronClient() {
        selectedNumbers = new ArrayList<>();
        initializeUI();
        initializeNetwork();
    }

    private void initializeUI() {
        setTitle("Numeron 数値入力");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        cardPanel = new JPanel(new GridLayout(2, 5));
        for (int i = 0; i < 10; i++) {
            JButton cardButton = new JButton(String.valueOf(i));
            cardButton.addActionListener(new CardButtonListener());
            cardPanel.add(cardButton);
        }

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false);  // 最初は無効化
        okButton.addActionListener(new OkButtonListener());
        JButton resetButton = new JButton("リセット");
        resetButton.addActionListener(new ResetButtonListener());
        controlPanel.add(okButton);
        controlPanel.add(resetButton);

        selectedLabel = new JLabel("選択した数値: ");
        mainPanel.add(selectedLabel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        mainPanel.add(scrollPane, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private void initializeNetwork() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            receiveMessages();

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
                        messageArea.append(message + "\n");
                        messageArea.setCaretPosition(messageArea.getDocument().getLength());
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class CardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int selectedNumber = Integer.parseInt(button.getText());

            if (selectedNumbers.size() < 3 && !selectedNumbers.contains(selectedNumber)) {
                selectedNumbers.add(selectedNumber);
                updateSelectedLabel();
            }
        }
    }

    private class OkButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // OKボタンが押された時の処理
            System.out.println("OKボタンがクリックされました。選択された数値: " + selectedNumbers);
            StringBuilder sb = new StringBuilder();
            for (int num : selectedNumbers) {
                sb.append(num);
            }
            out.println(sb.toString());  // サーバーに送信
            selectedNumbers.clear();
            updateSelectedLabel();
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedNumbers.clear();
            updateSelectedLabel();
        }
    }

    private void updateSelectedLabel() {
        StringBuilder sb = new StringBuilder("選択した数値: ");
        for (int num : selectedNumbers) {
            sb.append(num).append(" ");
        }
        selectedLabel.setText(sb.toString());

        // 数字が3桁選択されているかをチェックしてOKボタンを有効化する
        if (selectedNumbers.size() == 3) {
            enableOkButton(true);
        } else {
            enableOkButton(false);
        }
    }

    private void enableOkButton(boolean enable) {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component innerComponent : panel.getComponents()) {
                    if (innerComponent instanceof JPanel) {
                        JPanel innerPanel = (JPanel) innerComponent;
                        for (Component button : innerPanel.getComponents()) {
                            if (button instanceof JButton && ((JButton) button).getText().equals("OK")) {
                                button.setEnabled(enable);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NumeronClient::new);
    }
}
