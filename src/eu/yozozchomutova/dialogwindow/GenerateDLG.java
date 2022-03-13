package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.Generator;
import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.Scrollable;
import eu.yozozchomutova.ui.SelectBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GenerateDLG extends DialogWindow {

    private eu.yozozchomutova.ui.Scrollable noiseSize, addGreenBases, whiteBuildings;
    public SelectBox biomes;

    private ImageUI generateBtn;

    public GenerateDLG(JFrame owner) {
        super(owner, 500, 350, true, "Generate");
    }

    @Override
    public void setupUI() {
        noiseSize = new Scrollable(this, 0, 2000, 500, 5, 25, 250, 20, "Noise size");
        addGreenBases = new Scrollable(this, 0, 10, 0, 5, 75, 250, 20, "Additional green bases");
        whiteBuildings = new Scrollable(this, 0, 100, 15, 5, 125, 250, 20, "White buildings");

        biomes = new SelectBox(this, Main.biomesStr, 5, 175, 80, 20, "Biome");

        generateBtn = new ImageUI(this, null, 394, 308, 96, 32, "src/ui/btn_generate");
        generateBtn.addActionListener(e -> {
            setVisible(false);

            Generator.Generate(noiseSize.getValue(), addGreenBases.getValue(), whiteBuildings.getValue(), biomes.getSelectedIndex());
        });
    }
}
