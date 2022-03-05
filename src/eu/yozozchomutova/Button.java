package eu.yozozchomutova;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {

    public Button(Main panel, int x, int y, int width, int height, ImageIcon img) {
        super();

        setBounds(x, y, width, height);
        setIcon(img);
        setBackground(Color.RED);
        setForeground(Color.WHITE);
        setBorder(null);

        addMouseListener(panel);
        addKeyListener(panel);

        panel.add(this);
    }

    public Button(Main panel, int x, int y, int width, int height, String text) {
        super(text);

        setBounds(x, y, width, height);
        setBackground(Color.WHITE);

        addMouseListener(panel);
        addKeyListener(panel);

        panel.add(this);
    }

    public Button(JDialog dialog, int x, int y, int width, int height, String text) {
        super(text);

        setBounds(x, y, width, height);
        setBackground(Color.WHITE);

        dialog.add(this);
    }
}
