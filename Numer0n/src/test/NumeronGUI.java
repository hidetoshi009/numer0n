package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class NumeronGUI extends JFrame {
    //TODOitemボタンを正常利用時のみ無効化
    
    private List<String> selectedEntries;
    private JLabel selectedLabel;
    private JTextArea messageArea;
    private JTextField inputField;
    private NumeronClient client;
    private List<JButton> cardButtons;
    private List<JButton> itemButtons;
    private JButton okButton; // `OK`ボタンの参照を保存

    public NumeronGUI(NumeronClient client) {
        this.client = client;
        selectedEntries = new ArrayList<>();
        cardButtons = new ArrayList<>();
        itemButtons = new ArrayList<>();
        initializeUI();

        // ウィンドウが閉じられたときの処理を追加
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // システムを終了
            }
        });

        // 最初にゲーム説明のウィンドウを表示
        showGameDescription();
    }

    private void initializeUI() {
        setTitle("Numeron 数値入力");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // メッセージエリアを上に配置
        messageArea = new JTextArea(25, 20);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        mainPanel.add(scrollPane, BorderLayout.NORTH);

        // カードパネルを中央に配置
        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // 5pxの間隔を追加
        for (int i = 0; i < 10; i++) {
            JButton cardButton = createCardButton(String.valueOf(i));
            cardButton.setPreferredSize(new Dimension(70, 70));
            cardPanel.add(cardButton);
        }
        JButton AitemButton = createItemButton("a", "H&L.png");
        JButton BitemButton = createItemButton("b", "Sniper.png");
        JButton CitemButton = createItemButton("c", "Change.png");

        AitemButton.setPreferredSize(new Dimension(100, 100));
        BitemButton.setPreferredSize(new Dimension(100, 100));
        CitemButton.setPreferredSize(new Dimension(100, 100));
        cardPanel.add(AitemButton);
        cardPanel.add(BitemButton);
        cardPanel.add(CitemButton);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // 入力中の数値を表示するフィールドを下に配置
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(10);
        inputField.setEditable(false);
        inputPanel.add(inputField);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // コントロールパネルを下に配置
        JPanel controlPanel = new JPanel(new FlowLayout());
        okButton = new JButton("OK");
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

    private JButton createCardButton(String text) {
        JButton cardButton = new JButton(text);
        cardButton.addActionListener(new CardButtonListener());
        cardButton.setActionCommand(text);
        cardButtons.add(cardButton); // リストに追加
        return cardButton;
    }

    private JButton createItemButton(String text, String iconFileName) {
        // 画像ファイルのパスを指定（srcと同じ階層のimageディレクトリから）
        String iconPath = iconFileName;
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        JButton itemButton = new JButton(icon);
        itemButton.setActionCommand(text); // ボタンにテキストを設定
        itemButton.addActionListener(new ItemButtonListener());
        itemButtons.add(itemButton); // アイテムリストに追加
        return itemButton;
    }
    

    private void showGameDescription() {
        JOptionPane.showMessageDialog(this,
                "ゲームの説明：\nこのゲームは数値を入力して遊ぶゲームです。\n3桁の数値を選択してOKボタンを押してください。",
                "ゲーム説明",
                JOptionPane.INFORMATION_MESSAGE);
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
                button.setEnabled(false); // ボタンを無効化
                updateSelectedLabel();
            }

            // 入力中の数値を更新
            updateInputField();
        }
    }

    private class ItemButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String selectedEntry = button.getActionCommand(); // アイテムボタンのテキストを取得

            if (!selectedEntries.contains(selectedEntry)) {
                selectedEntries.add(selectedEntry);
                button.setEnabled(false); // アイテムボタンを無効化
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
            resetCardButtons(); // カードボタンをリセット
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedEntries.clear();
            updateSelectedLabel();
            updateInputField(); // 入力中の数値をクリア
            resetCardButtons(); // カードボタンをリセット
        }
    }

    private void updateSelectedLabel() {
        StringBuilder sb = new StringBuilder("選択した数値: ");
        for (String entry : selectedEntries) {
            sb.append(entry).append(" ");
        }
        selectedLabel.setText(sb.toString());

        // 数字が3桁選択されているか、または特定のアイテムが選択されているかをチェックしてOKボタンを有効化する
        enableOkButton(selectedEntries.size() == 3 || selectedEntries.contains("a") || selectedEntries.contains("b") || selectedEntries.contains("c"));
    }

    private void updateInputField() {
        StringBuilder sb = new StringBuilder();
        for (String entry : selectedEntries) {
            sb.append(entry);
        }
        inputField.setText(sb.toString());
    }

    private void enableOkButton(boolean enable) {
        okButton.setEnabled(enable); // `OK`ボタンの参照を使って有効化
    }

    private void resetCardButtons() {
        for (JButton button : cardButtons) {
            button.setEnabled(true);
        }
        for (JButton button : itemButtons) {
            button.setEnabled(true);
        }
    }
}
