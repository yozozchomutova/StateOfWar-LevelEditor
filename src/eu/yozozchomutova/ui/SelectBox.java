package eu.yozozchomutova.ui;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class SelectBox extends JComboBox {

    public JLabel title;

    public SelectBox(Container container, String[] items, int x, int y, int width, int height, String titleText) {
        super(items);

        setBounds(x, y + (titleText != null ? 20 : 0), width, height);

        setForeground(Color.WHITE);
        setBackground(Color.BLACK);

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
