package test_GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class NumeronGUI extends JFrame {

    private List<String> selectedEntries; // Change to List of String to include characters
    private JLabel selectedLabel;
    private JTextArea messageArea;
    private JTextField inputField; // 入力中の数値を表示するためのフィールド
    private NumeronClient client;

    public NumeronGUI(NumeronClient client) {
        this.client = client;
        selectedEntries = new ArrayList<>();
        initializeUI();

        // ウィンドウが閉じられたときの処理を追加
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.close(); // クライアントをクローズ
                System.exit(0); // システムを終了
            }
        });
    }

    private void initializeUI() {
        setTitle("Numeron 数値入力");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // メッセージエリアを上に配置
        messageArea = new JTextArea(25, 20);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        mainPanel.add(scrollPane, BorderLayout.NORTH);

        // カードパネルを中央に配置
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // 5pxの間隔を追加
        for (int i = 0; i < 10; i++) {
            JButton cardButton = new JButton(String.valueOf(i));
            cardButton.setPreferredSize(new Dimension(50, 50)); // ボタンのサイズを固定
            cardButton.addActionListener(new CardButtonListener());
            cardPanel.add(cardButton);
        }
        JButton acardButton = new JButton("a");
        acardButton.setPreferredSize(new Dimension(50, 50)); // ボタンのサイズを固定
        acardButton.addActionListener(new CardButtonListener());
        cardPanel.add(acardButton);
        JButton bcardButton = new JButton("b");
        bcardButton.setPreferredSize(new Dimension(50, 50)); // ボタンのサイズを固定
        bcardButton.addActionListener(new CardButtonListener());
        cardPanel.add(bcardButton);
        JButton ccardButton = new JButton("c");
        ccardButton.setPreferredSize(new Dimension(50, 50)); // ボタンのサイズを固定
        ccardButton.addActionListener(new CardButtonListener());
        cardPanel.add(ccardButton);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // 入力中の数値を表示するフィールドを下に配置
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(10);
        inputField.setEditable(false);
        inputPanel.add(inputField);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // コントロールパネルを下に配置
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.setEnabled(false); // 最初は無効化
        okButton.addActionListener(new OkButtonListener());
        JButton resetButton = new JButton("リセット");
        resetButton.addActionListener(new ResetButtonListener());
        controlPanel.add(okButton);
        controlPanel.add(resetButton);

        selectedLabel = new JLabel("選択した数値: ");
        mainPanel.add(selectedLabel, BorderLayout.LINE_END); // 右側に配置
        mainPanel.add(controlPanel, BorderLayout.PAGE_END);

        add(mainPanel);
        setVisible(true);
    }

    public void displayServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    private class CardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String selectedEntry = button.getText();

            if (selectedEntries.size() < 3 && !selectedEntries.contains(selectedEntry)) {
                selectedEntries.add(selectedEntry);
                updateSelectedLabel();
            }

            // 入力中の数値を更新
            updateInputField();
        }
    }

    private class OkButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // OKボタンが押された時の処理
            System.out.println("OKボタンがクリックされました。選択された数値: " + selectedEntries);
            StringBuilder sb = new StringBuilder();
            for (String entry : selectedEntries) {
                sb.append(entry);
            }
            client.sendMessage(sb.toString()); // サーバーに送信
            selectedEntries.clear();
            updateSelectedLabel();
            updateInputField(); // 入力中の数値をクリア
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedEntries.clear();
            updateSelectedLabel();
            updateInputField(); // 入力中の数値をクリア
        }
    }

    private void updateSelectedLabel() {
        StringBuilder sb = new StringBuilder("選択した数値: ");
        for (String entry : selectedEntries) {
            sb.append(entry).append(" ");
        }
        selectedLabel.setText(sb.toString());

        // 数字が3桁選択されているかをチェックしてOKボタンを有効化する
        enableOkButton(true);
    }

    private void updateInputField() {
        StringBuilder sb = new StringBuilder();
        for (String entry : selectedEntries) {
            sb.append(entry);
        }
        inputField.setText(sb.toString());
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
}
