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

    public JLabel windowTItleBar;

    public WindowBar(Window parent, Container container, Main main, int frameWidth, boolean isDialog, boolean closeAble, String titleText) {
        this.window = parent;

        windowTItleBar = new JLabel(titleText);
        windowTItleBar.setForeground(Color.WHITE);
        windowTItleBar.setBounds(5, 0, 1500, WindowBar.BAR_HEIGHT);
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
            maximize.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame frame = (JFrame) window;
                    frame.setExtendedState( JFrame.ICONIFIED );
                    frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
                }
            });

            minimize = new ImageUI(container, main, frameWidth - 150, 0, 50, BAR_HEIGHT, "src/ui/btn_minimize");
            minimize.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((JFrame) window).setExtendedState( JFrame.ICONIFIED );
                }
            });
        }

        windowBar = new ImageUI(container, 0, 0, frameWidth, BAR_HEIGHT, "src/ui/window_bar_bcg.png", true);
        windowBar.addMouseListener(this);

        windowBars.add(this);
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
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
