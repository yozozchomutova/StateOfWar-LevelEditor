package eu.yozozchomutova.ui;

import javax.swing.*;
import java.awt.*;

public class TextField extends JTextField {

    public JLabel title;

    public TextField(Container container, int x, int y, int width, int height, String titleText) {
        super();

        setBounds(x, y + (titleText != null ? 20 : 0), width, height);

        if (titleText != null) {
            title = new JLabel(titleText);
            title.setVerticalAlignment(SwingConstants.TOP);
            title.setForeground(Color.WHITE);
            title.setBounds(x, y, width, 20);

            container.add(title);
            container.add(this);
        }

        container.add(this);
    }
}
