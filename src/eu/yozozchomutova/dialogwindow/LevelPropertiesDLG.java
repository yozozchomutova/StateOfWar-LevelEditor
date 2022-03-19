package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.GameProperties;
import eu.yozozchomutova.Level;
import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.CheckBox;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.Scrollable;
import eu.yozozchomutova.ui.TextField;
import jdk.nashorn.internal.objects.Global;

import javax.swing.*;
import java.io.IOException;

public class LevelPropertiesDLG extends DialogWindow {

    private TextField blueMoney, greenMoney;
    private TextField blueResearch, greenResearch;

    private CheckBox bluePVT, greenPVT;

    private CheckBox blueCannon, blueAntiair, bluePlasma, blueRotary, blueDefragmentator;
    private CheckBox greenCannon, greenAntiair, greenPlasma, greenRotary, greenDefragmentator;

    private TextField blueFighter, blueBomber, blueTripler, blueCarryall, blueMeteor;
    private TextField greenFighter, greenBomber, greenTripler, greenCarryall, greenMeteor;

    private ImageUI apply;

    public LevelPropertiesDLG(JFrame owner) {
        super(owner, 300, 500, true, "Level properties");
    }

    @Override
    public void setupUI() {
        int LINE_X = 10;

        blueMoney = new TextField(this, LINE_X, 20, 115, 17, "Blue Money");
        greenMoney = new TextField(this, LINE_X+150, 20, 115, 17, "Green Money");

        blueResearch = new TextField(this, LINE_X, 60, 115, 17, "Blue Research");
        greenResearch = new TextField(this, LINE_X+150, 60, 115, 17, "Green Research");

        bluePVT = new CheckBox(this, LINE_X, 100, 140, 20, "Blue PVT");
        blueCannon = new CheckBox(this, LINE_X, 125, 140, 20, "Blue Cannon");
        blueAntiair = new CheckBox(this, LINE_X, 150, 140, 20, "Blue Antiair");
        bluePlasma = new CheckBox(this, LINE_X, 175, 140, 20, "Blue Plasma");
        blueRotary = new CheckBox(this, LINE_X, 200, 140, 20, "Blue Rotary");
        blueDefragmentator = new CheckBox(this, LINE_X, 225, 100, 20, "Blue Defrag.");

        greenPVT = new CheckBox(this, LINE_X+150, 100, 140, 20, "Green PVT");
        greenCannon = new CheckBox(this, LINE_X+150, 125, 140, 20, "Green Cannon");
        greenAntiair = new CheckBox(this, LINE_X+150, 150, 140, 20, "Green Antiair");
        greenPlasma = new CheckBox(this, LINE_X+150, 175, 140, 20, "Green Plasma");
        greenRotary = new CheckBox(this, LINE_X+150, 200, 140, 20, "Green Rotary");
        greenDefragmentator = new CheckBox(this, LINE_X+150, 225, 140, 20, "Green Defrag.");

        blueFighter = new TextField(this, LINE_X, 250, 115, 17, "Blue Fighters");
        blueBomber = new TextField(this, LINE_X, 290, 115, 17, "Blue Bombers");
        blueTripler = new TextField(this, LINE_X, 330, 115, 17, "Blue Triplers");
        blueCarryall = new TextField(this, LINE_X, 370, 115, 17, "Blue Carryalls");
        blueMeteor = new TextField(this, LINE_X, 410, 115, 17, "Blue Meteors");

        greenFighter = new TextField(this, LINE_X+150, 250, 115, 17, "Green Fighters");
        greenBomber = new TextField(this, LINE_X+150, 290, 115, 17, "Green Bombers");
        greenTripler = new TextField(this, LINE_X+150, 330, 115, 17, "Green Triplers");
        greenCarryall = new TextField(this, LINE_X+150, 370, 115, 17, "Green Carryalls");
        greenMeteor = new TextField(this, LINE_X+150, 410, 115, 17, "Green Meteors");

        apply = new ImageUI(this, null, 102, 460, 96, 32, "src/ui/btn_apply");
        apply.addActionListener(e -> {
            try {
                GameProperties.blueMoney = Integer.parseInt(blueMoney.getText());
                GameProperties.greenMoney = Integer.parseInt(greenMoney.getText());

                GameProperties.blueResearch = Integer.parseInt(blueResearch.getText());
                GameProperties.greenResearch = Integer.parseInt(greenResearch.getText());

                GameProperties.bluePVT = bluePVT.isSelected();
                GameProperties.blueCannon = blueCannon.isSelected();
                GameProperties.blueAntiair = blueAntiair.isSelected();
                GameProperties.bluePlasma = bluePlasma.isSelected();
                GameProperties.blueRotary = blueRotary.isSelected();
                GameProperties.blueDefragmentator = blueDefragmentator.isSelected();

                GameProperties.greenPVT = greenPVT.isSelected();
                GameProperties.greenCannon = greenCannon.isSelected();
                GameProperties.greenAntiair = greenAntiair.isSelected();
                GameProperties.greenPlasma = greenPlasma.isSelected();
                GameProperties.greenRotary = greenRotary.isSelected();
                GameProperties.greenDefragmentator = greenDefragmentator.isSelected();

                GameProperties.blueFighters = Integer.parseInt(blueFighter.getText());
                GameProperties.blueBombers = Integer.parseInt(blueBomber.getText());
                GameProperties.blueTrojans = Integer.parseInt(blueTripler.getText());
                GameProperties.blueCarrier = Integer.parseInt(blueCarryall.getText());
                GameProperties.blueMeteors = Integer.parseInt(blueMeteor.getText());

                GameProperties.greenFighters = Integer.parseInt(greenFighter.getText());
                GameProperties.greenBombers = Integer.parseInt(greenBomber.getText());
                GameProperties.greenTrojans = Integer.parseInt(greenTripler.getText());
                GameProperties.greenCarrier = Integer.parseInt(greenCarryall.getText());
                GameProperties.greenMeteors = Integer.parseInt(greenMeteor.getText());
            } catch (NumberFormatException mfe) {
                JOptionPane.showMessageDialog(this, "Saving values error - Please check, there's no letter in text fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Level.regenerateRightTabProperties();
            setVisible(false);
        });
    }

    public void reasignValues() {
        blueMoney.setText("" + GameProperties.blueMoney);
        greenMoney.setText("" + GameProperties.greenMoney);

        blueResearch.setText("" + GameProperties.blueResearch);
        greenResearch.setText("" + GameProperties.greenResearch);

        bluePVT.setSelected(GameProperties.bluePVT);
        blueCannon.setSelected(GameProperties.blueCannon);
        blueAntiair.setSelected(GameProperties.blueAntiair);
        bluePlasma.setSelected(GameProperties.bluePlasma);
        blueRotary.setSelected(GameProperties.blueRotary);
        blueDefragmentator.setSelected(GameProperties.blueDefragmentator);

        greenPVT.setSelected(GameProperties.greenPVT);
        greenCannon.setSelected(GameProperties.greenCannon);
        greenAntiair.setSelected(GameProperties.greenAntiair);
        greenPlasma.setSelected(GameProperties.greenPlasma);
        greenRotary.setSelected(GameProperties.greenRotary);
        greenDefragmentator.setSelected(GameProperties.greenDefragmentator);

        blueFighter.setText("" + GameProperties.blueFighters);
        blueBomber.setText("" + GameProperties.blueBombers);
        blueTripler.setText("" + GameProperties.blueTrojans);
        blueCarryall.setText("" + GameProperties.blueCarrier);
        blueMeteor.setText("" + GameProperties.blueMeteors);

        greenFighter.setText("" + GameProperties.greenFighters);
        greenBomber.setText("" + GameProperties.greenBombers);
        greenTripler.setText("" + GameProperties.greenTrojans);
        greenCarryall.setText("" + GameProperties.greenCarrier);
        greenMeteor.setText("" + GameProperties.greenMeteors);
    }
}
