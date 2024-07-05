package test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

    private List<Integer> selectedNumbers;
    private JLabel selectedLabel;
    private JTextArea messageArea;
    private JTextField inputField; // 入力中の数値を表示するためのフィールド
    private NumeronClient client;

    public NumeronGUI(NumeronClient client) {
        this.client = client;
        selectedNumbers = new ArrayList<>();
        initializeUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.close();
                System.exit(0);
            }
        });
    }

    private void initializeUI() {
        setTitle("Numeron 数値入力");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // メッセージエリアを上に配置
        messageArea = new JTextArea(5, 20);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        mainPanel.add(scrollPane, BorderLayout.NORTH);

        // カードパネルを中央に配置
        JPanel cardPanel = new JPanel(new GridLayout(2, 5, 5, 5)); // 5pxの間隔を追加
        for (int i = 0; i < 10; i++) {
            JButton cardButton = new JButton(String.valueOf(i));
            cardButton.setPreferredSize(new Dimension(50, 50)); // ボタンのサイズを固定
            cardButton.addActionListener(new CardButtonListener());
            cardPanel.add(cardButton);
        }
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
        okButton.setEnabled(false);  // 最初は無効化
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
            messageArea.append("サーバー: " + message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
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

            // 入力中の数値を更新
            updateInputField();
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
            client.sendMessage(sb.toString());  // サーバーに送信
            selectedNumbers.clear();
            updateSelectedLabel();
            updateInputField(); // 入力中の数値をクリア
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedNumbers.clear();
            updateSelectedLabel();
            updateInputField(); // 入力中の数値をクリア
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

    private void updateInputField() {
        StringBuilder sb = new StringBuilder();
        for (int num : selectedNumbers) {
            sb.append(num);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NumeronClient client = new NumeronClient("localhost");
            new NumeronGUI(client);
        });
    }
}
