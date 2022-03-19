package eu.yozozchomutova.ui;

import eu.yozozchomutova.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class WindowBar implements MouseListener {

    public static final int BAR_HEIGHT = 20;

    public static LinkedList<WindowBar> windowBars = new LinkedList<>();

    public ImageUI windowBar, close, maximize, minimize;

    public Window window;

    public boolean windowIsMoving = false;

    public JLabel windowIcon;
    public JLabel windowTItleBar;

    public WindowBar(Window parent, Container container, Main main, int frameWidth, boolean isDialog, boolean closeAble, String titleText) {
        this.window = parent;

        windowIcon = new JLabel();
        windowIcon.setForeground(Color.WHITE);
        windowIcon.setBounds(2, 0, WindowBar.BAR_HEIGHT, WindowBar.BAR_HEIGHT);
        windowIcon.setVerticalAlignment(SwingConstants.TOP);
        windowIcon.setVisible(false);
        container.add(windowIcon);

        windowTItleBar = new JLabel(titleText);
        windowTItleBar.setForeground(Color.WHITE);
        windowTItleBar.setBounds(0, 0, 1500, WindowBar.BAR_HEIGHT);
        windowTItleBar.setVerticalAlignment(SwingConstants.TOP);
        container.add(windowTItleBar);

        if (closeAble) {
            close = new ImageUI(container, main, frameWidth - 50, 0, 50, BAR_HEIGHT, "src/ui/btn_close");
            close.addActionListener(e -> {
                window.setVisible(false);

                if (!isDialog) { //Probably it's main window
                    System.exit(0);
                }
            });
        }

        if (!isDialog) {
            maximize = new ImageUI(container, main, frameWidth - 100, 0, 50, BAR_HEIGHT, "src/ui/btn_maximize");
            maximize.addActionListener(e -> {
                JFrame frame = (JFrame) window;

                if (frame.getExtendedState() == 0) {
                    frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
                }
            });

            minimize = new ImageUI(container, main, frameWidth - 150, 0, 50, BAR_HEIGHT, "src/ui/btn_minimize");
            minimize.addActionListener(e -> ((JFrame) window).setExtendedState( JFrame.ICONIFIED ));
        }

        windowBar = new ImageUI(container, 0, 0, frameWidth, BAR_HEIGHT, "src/ui/window_bar_bcg.png", Image.SCALE_FAST);
        windowBar.addMouseListener(this);

        windowBars.add(this);
    }

    public void setIcon(String path) {
        windowTItleBar.setBounds(BAR_HEIGHT+3, 0, 1500, WindowBar.BAR_HEIGHT);

        Image iconImg = new ImageIcon(path).getImage().getScaledInstance(BAR_HEIGHT, BAR_HEIGHT, Image.SCALE_FAST);
        windowIcon.setIcon(new ImageIcon(iconImg));
        windowIcon.setVisible(true);
    }

    public void updateUI(int frameWidth) {
        close.setBounds(frameWidth - 50, 0, 50, BAR_HEIGHT);
        maximize.setBounds(frameWidth - 100, 0, 50, BAR_HEIGHT);
        minimize.setBounds(frameWidth - 150, 0, 50, BAR_HEIGHT);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        windowIsMoving = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        windowIsMoving = false;

        if (window instanceof JFrame && e.getYOnScreen() == 0) {
            JFrame frame = (JFrame) window;
            frame.setExtendedState(JFrame.ICONIFIED);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
