package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.Level;
import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.Scrollable;

import javax.swing.*;

public class NewProjectDLG extends DialogWindow {

    private Scrollable mapWidth, mapHeight;
    private ImageUI apply;

    public NewProjectDLG(JFrame owner) {
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
                Level.newProject(mapWidth.getValue(), mapHeight.getValue());
            } catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(NewProjectDLG.this, "Invalid map size", "Map size error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
    }
}
