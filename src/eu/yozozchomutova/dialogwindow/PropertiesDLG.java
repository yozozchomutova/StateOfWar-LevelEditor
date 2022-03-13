package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.Scrollable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PropertiesDLG extends DialogWindow {

    private Scrollable mapWidth, mapHeight;
    private ImageUI apply;

    public PropertiesDLG(JFrame owner) {
        super(owner, 350, 250, true, "Properties");
    }

    @Override
    public void setupUI() {
        mapWidth = new Scrollable(this, Main.MIN_IMG_WIDTH, Main.MAX_IMG_WIDTH, Main.MIN_IMG_WIDTH, 5, 25, 150, 20, "Tiled map width");
        mapHeight = new Scrollable(this, Main.MIN_IMG_HEIGHT, Main.MAX_IMG_HEIGHT, Main.MIN_IMG_HEIGHT, 170, 25, 150, 20, "Tiled map Height");

        mapWidth.setValue(64);
        mapHeight.setValue(64);

        apply = new ImageUI(this, null, 144, 208, 96, 32, "src/ui/btn_apply");
        apply.addActionListener(e -> {
            setVisible(false);

            try {
                Main.newProject(mapWidth.getValue(), mapHeight.getValue());
            } catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(PropertiesDLG.this, "Invalid map size", "Map size error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
    }
}
