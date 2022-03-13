package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.ui.ImageUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmDLG extends DialogWindow {

    private ImageUI noButton, yesButton;
    private JLabel textLabel;

    public ConfirmDLG(JFrame owner) {
        super(owner, 400, 150, false, "Confirm");

        textLabel = new JLabel();
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("arial", Font.PLAIN, 24));
        textLabel.setBounds(5, 25, 400, 150);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        add(textLabel);
    }

    private volatile boolean result;
    private volatile boolean responded = false;

    public boolean showDialog(String text) {
        textLabel.setText(text);

        /*while (!responded) {
            Thread.onSpinWait();
        }*/

        responded = false;

        return result;
    }

    @Override
    public void setupUI() {
        noButton = new ImageUI(this, null, 50, 100, 96, 32, "src/ui/btn_no");
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = false;
                responded = true;
            }
        });

        yesButton = new ImageUI(this, null, 254, 100, 96, 32, "src/ui/btn_yes");
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = true;
                responded = true;
            }
        });
    }
}
