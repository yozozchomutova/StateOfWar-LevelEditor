package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.ui.ImageUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoingTaskDLG extends DialogWindow {

    private JLabel textLabel;
    private JProgressBar progressBar;

    public DoingTaskDLG(JFrame owner) {
        super(owner, 400, 150, false, "");
    }

    @Override
    public void setupUI() {
        textLabel = new JLabel("");
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("arial", Font.PLAIN, 24));
        textLabel.setBounds(5, 25, 400, 150);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        add(textLabel);

        progressBar = new JProgressBar();
        progressBar.setBounds(0, 120, 400, 30);
        progressBar.setForeground(Color.RED);
        progressBar.setBorderPainted(false);
        add(progressBar);
    }

    public void showDialog(String titleText) {
        windowBar.windowTItleBar.setText(titleText);
        updateProgress("...", 0, 100);
        setVisible(true);
    }

    public void updateProgress(String text, int value, int maxValue) {
        textLabel.setText(text);
        progressBar.setMaximum(maxValue);
        progressBar.setValue(value);
    }
}
