package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.Level;
import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.Scrollable;

import javax.swing.*;

public class SettingsDLG extends DialogWindow {

    private Scrollable mapWidth, mapHeight;
    private ImageUI apply;

    public SettingsDLG(JFrame owner) {
        super(owner, 350, 250, true, "Settings");
    }

    @Override
    public void setupUI() {
        apply = new ImageUI(this, null, 144, 208, 96, 32, "src/ui/btn_apply");
        apply.addActionListener(e -> {
            setVisible(false);
        });
    }
}
