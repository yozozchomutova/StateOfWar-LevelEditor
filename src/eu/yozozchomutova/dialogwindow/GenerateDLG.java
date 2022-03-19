package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.Generator;
import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.CheckBox;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.Scrollable;
import eu.yozozchomutova.ui.SelectBox;

import javax.swing.*;

public class GenerateDLG extends DialogWindow {

    private CheckBox generateSurface, generateObjects;
    private Scrollable noiseSize, addGreenBases, whiteBuildings;
    public SelectBox biomes;

    private ImageUI generateBtn;

    public GenerateDLG(JFrame owner) {
        super(owner, 500, 350, true, "Generate");
    }

    @Override
    public void setupUI() {
        generateSurface = new CheckBox(this, 10, 30, 100, 20, "Surface");
        generateObjects = new CheckBox(this, 10, 60, 100, 20, "Objects");

        noiseSize = new Scrollable(this, 0, 2000, 500, 110, 25, 250, 20, "Noise size");
        addGreenBases = new Scrollable(this, 0, 10, 0, 110, 75, 250, 20, "Additional green bases");
        whiteBuildings = new Scrollable(this, 0, 100, 15, 110, 125, 250, 20, "White buildings");

        biomes = new SelectBox(this, Main.biomesStr, 110, 175, 80, 20, "Biome");

        generateBtn = new ImageUI(this, null, 394, 308, 96, 32, "src/ui/btn_generate");
        generateBtn.addActionListener(e -> {
            setVisible(false);

            Generator.Generate(generateSurface.isSelected(), generateObjects.isSelected(), noiseSize.getValue(), addGreenBases.getValue(), whiteBuildings.getValue(), biomes.getSelectedIndex());
        });
    }

    public void setGenerateSettings(boolean generateSurface, boolean generateObjects) {
        this.generateSurface.setSelected(generateSurface);
        this.generateObjects.setSelected(generateObjects);
    }
}
