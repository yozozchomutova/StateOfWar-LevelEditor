package eu.yozozchomutova.toolpanel;

import eu.yozozchomutova.Main;
import eu.yozozchomutova.Rasterizer;
import eu.yozozchomutova.ui.WindowBar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ToolPanel extends JWindow {

    public ToolPanelJPanel panel;

    private JLabel background;

    public ToolPanel(JFrame frame) {
        super(frame);

        //Panel & Window setup
        panel = new ToolPanelJPanel();

        setBounds(frame.getWidth()-300, WindowBar.BAR_HEIGHT, 300, frame.getHeight()-WindowBar.BAR_HEIGHT);
        panel.setBounds(0, 0, getWidth(), getHeight());

        add(panel);

        setupUI();

        //Background
        Color[][] backgroundPixels = new Color[getWidth()][getHeight()];
        Rasterizer.fillBackground(backgroundPixels, Main.levelPropertiesBCG);

        BufferedImage bcgBI = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Rasterizer.renderToImage(bcgBI, backgroundPixels);

        background = new JLabel(new ImageIcon(bcgBI));
        background.setBounds(0, 0, getWidth(), getHeight());
        panel.add(background);

        setVisible(false);
        panel.repaint();
    }

    public abstract void setupUI();

    public abstract void setVisibility(boolean visibility);

    public static class ToolPanelJPanel extends JPanel {

        public ToolPanelJPanel() {
            super();

            setLayout(null);
        }

        @Override
        public void paintComponents(Graphics g) {
            super.paintComponents(g);

            repaint();
        }
    }
}
