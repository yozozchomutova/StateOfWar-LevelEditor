package eu.yozozchomutova.toolpanel;

import eu.yozozchomutova.Building;
import eu.yozozchomutova.ProducingUnit;
import eu.yozozchomutova.Rasterizer;
import eu.yozozchomutova.Unit;
import eu.yozozchomutova.ui.SelectBox;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class EditObjectToolPanel extends ToolPanel {

    public static final String[] buildingsTeamsSTR = {"Blue", "Green", "White"};
    public static final String[] unitTeamsSTR = {"Blue", "Green", "Neutral"};

    public Object selectedObject; //Can be Building or Unit

    public Building holdingBuilding;
    public Unit holdingUnit;

    //UI
    private JLabel objectSelectedBCG;
    private JLabel largeIcon;

    //Tools for Buildings
    private SelectBox buildingList;
    private SelectBox team;

//    private SelectBox[] productionUnitLists = new SelectBox[5];
//    private SelectBox[] productionList = new SelectBox[5];

    //Tools for Units
    private SelectBox unitList;

    public EditObjectToolPanel(JFrame frame) {
        super(frame);


    }

    @Override
    public void setupUI() {
        largeIcon = new JLabel();
        largeIcon.setBounds(50, 50, 200, 200);
        largeIcon.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(largeIcon);

        objectSelectedBCG = new JLabel(new ImageIcon("src/ui/objectSelectedBcg.png"));
        objectSelectedBCG.setBounds(50, 50, 200, 200);
        panel.add(objectSelectedBCG);

        //UI tools for buildings
        Building.BuildingType[] buildingTypes = Building.BuildingType.values();
        String[] buildingListSTR = new String[buildingTypes.length];
        for (int i = 0; i < buildingListSTR.length; i++) {
            buildingListSTR[i] = buildingTypes[i].name;
        }

        buildingList = new SelectBox(panel, buildingListSTR, 25, 280, 100, 20, "Building type");
        team = new SelectBox(panel, buildingsTeamsSTR, 150+25, 280, 100, 20, "Team color");

        SelectBox[] productionUnitLists = new SelectBox[5];
        for (int i = 0; i < productionUnitLists.length; i++) {
            SelectBox productionUnitList = productionUnitLists[i];
            productionUnitList = new SelectBox(panel, buildingListSTR, 25, 340 + i * 50, 100, 20, "Prod. unit " + i);
        }
    }

    @Override
    public void setVisibility(boolean visibility) {
        setVisible(visibility);
    }

    public void selectBuilding(Building b) {
        this.selectedObject = b;

        BufferedImage largeIconBI = Rasterizer.createBI(b.buildingType.getBuildingPixels(b.teamColor), b.buildingType.width, b.buildingType.height);
        largeIcon.setIcon(new ImageIcon(largeIconBI));
    }

    public void selectUnit(Unit u) {
        this.selectedObject = u;

        BufferedImage largeIconBI = Rasterizer.createBI(u.unitType.getUnitPixels(u.teamColor), u.unitType.width, u.unitType.height);
        largeIcon.setIcon(new ImageIcon(largeIconBI));
    }

    private void showBuildingToolsUI() {

    }

    private void showUnitToolsUI() {

    }
}
