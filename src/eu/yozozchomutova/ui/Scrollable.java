package eu.yozozchomutova.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class Scrollable extends JSlider implements ChangeListener {

    public JLabel title;

    private String titleText;

    public Scrollable(Container container, int min, int max, int defValue, int x, int y, int width, int height, String titleText) {
        super(HORIZONTAL);

        this.titleText = titleText;

        setMinimum(min);
        setMaximum(max);
        setValue(defValue);

        setOpaque(false);
        setBackground(null);
        setBorder(null);

        setBounds(x, y + (titleText != null ? 20 : 0), width, height);

        if (titleText != null) {
            title = new JLabel(titleText);
            title.setVerticalAlignment(SwingConstants.TOP);
            title.setForeground(Color.WHITE);
            title.setBounds(x, y, width, 20);

            container.add(title);
            container.add(this);
        }

        addChangeListener(this);

        container.add(this);

        stateChanged(null); //Update text
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (title != null) {
            title.setText(titleText + " (" + getValue() + ")");
        }
    }
}
