package eu.yozozchomutova.ui;

import javax.swing.*;
import java.awt.*;

public class CheckBox extends JCheckBox {

    public CheckBox(Container container, int x, int y, int width, int height, String titleText) {
        super(titleText);

        setBounds(x, y/* + (titleText != null ? 20 : 0)*/, width, height);

        setForeground(Color.WHITE);
        setBackground(new Color(0, 0, 0, 0));
        setIcon(new ImageIcon("src/ui/cb1_off.png"));
        setSelectedIcon(new ImageIcon("src/ui/cb1_on.png"));

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setBorder(null);

        /*if (titleText != null) {
            title = new JLabel(titleText);
            title.setVerticalAlignment(SwingConstants.TOP);
            title.setForeground(Color.WHITE);
            title.setBounds(x, y, width, 20);

            container.add(title);
            container.add(this);
        }*/

        container.add(this);
    }
}
