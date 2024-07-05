package test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class NumeronInputGUI extends JFrame {

    private List<Integer> selectedNumbers;
    private JLabel selectedLabel;

    public NumeronInputGUI() {
        selectedNumbers = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Numeron 数値入力");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel cardPanel = new JPanel(new GridLayout(2, 5));
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

        add(mainPanel);
        setVisible(true);
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
        SwingUtilities.invokeLater(NumeronInputGUI::new);
    }
}
