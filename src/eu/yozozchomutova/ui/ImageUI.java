package eu.yozozchomutova.ui;

import eu.yozozchomutova.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ImageUI extends JButton implements MouseListener {

    private ImageIcon imgOff, imgOn;

    public ImageUI(Container container, Main main, int x, int y, int width, int height, String imgPath) {
        this(container, main, x, y, width, height, imgPath, true);
    }

    public ImageUI(Container container, Main main, int x, int y, int width, int height, String imgPath, boolean visible) {
        super();

        this.imgOff = new ImageIcon(imgPath + "_off.png");
        this.imgOn = new ImageIcon(imgPath + "_on.png");

        setBounds(x, y, width, height);
        setImageIcon(imgOff);

        addMouseListener(this);

        if (main != null) {
            addKeyListener(main);
            addMouseListener(main);
        }

        setVisible(visible);
        container.add(this);
    }

    public ImageUI(Container container, int x, int y, int width, int height, String imgPath, int scaleType) {
        super();

        ImageIcon img = new ImageIcon(imgPath);

        if (scaleType != 0) {
            img = new ImageIcon(img.getImage().getScaledInstance(width, height, scaleType));
        }

        setBounds(x, y, width, height);
        setImageIcon(img);

        container.add(this);
    }

    private void setImageIcon(ImageIcon imageIcon) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setBorder(null);

        setIcon(imageIcon);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setIcon(imgOn);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setIcon(imgOff);
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
}
