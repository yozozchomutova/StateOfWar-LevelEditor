package eu.yozozchomutova;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Building {

    public static final int[] BASIC_FACTORY_PU = {0x01, 0x04, 0x07, 0x0a, 0x0d};
    public static final int[] GOLD_MINE_PU = {0x10, 0x11, 0x12, 0x12, 0x12};
    public static final int[] RESEARCH_PU = {0x13, 0x14, 0x15, 0x15, 0x15};
    public static final int[] ENERGY_PU = {0x16, 0x17, 0x18, 0x18, 0x18};
    public static final int[] RADAR_PU = {0x1e, 0x1f, 0x20, 0x21, 0x22};
    public static final int[] ROBOT_FACTORY_PU = {0x29, 0x2a, 0x2b, 0x2c, 0x2e};

    public BuildingType buildingType;
    public TeamColor teamColor;

    public int totalProducts;
    public int productionsAvailableFromStart;

    public int productionUnit1;
    public int productionUnit2;
    public int productionUnit3;
    public int productionUnit4;
    public int productionUnit5;

    public int productionUnitUpgradeItem1;
    public int productionUnitUpgradeItem2;
    public int productionUnitUpgradeItem3;
    public int productionUnitUpgradeItem4;
    public int productionUnitUpgradeItem5;

    public int HP;

    public boolean hasSatelliteProtection;

    public int xTilePos;
    public int yTilePos;

    public int hpPercentage; //0-100 %

    public Building(BuildingType buildingType, TeamColor teamColor, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        totalProducts = Main.random.nextInt(3)+1;
        productionsAvailableFromStart = Main.random.nextInt(totalProducts+1);

        int[] productionUnits = new int[5];

        //Random units
        //Basic factories
        if (buildingType == BuildingType.SMALL_FACTORY || buildingType == BuildingType.MEDIUM_FACTORY || buildingType == BuildingType.BIG_FACTORY) {
            for (int i = 0; i < totalProducts; i++) {
                int randomUnit = Main.random.nextInt(BASIC_FACTORY_PU.length);
                productionUnits[i] = BASIC_FACTORY_PU[randomUnit];
            }
        } else if (buildingType == BuildingType.GOLD_MINE) {
            for (int i = 0; i < totalProducts; i++) {
                productionUnits[i] = GOLD_MINE_PU[i];
            }
        } else if (buildingType == BuildingType.LABORATORY) {
            for (int i = 0; i < totalProducts; i++) {
                productionUnits[i] = RESEARCH_PU[i];
            }
        } else if (buildingType == BuildingType.POWER_PLANT) {
            for (int i = 0; i < totalProducts; i++) {
                productionUnits[i] = ENERGY_PU[i];
            }
        } else if (buildingType == BuildingType.RADAR) {
            for (int i = 0; i < totalProducts; i++) {
                int randomUnit = Main.random.nextInt(RADAR_PU.length);
                productionUnits[i] = RADAR_PU[randomUnit];
            }
        } else if (buildingType == BuildingType.ROBOT_FACTORY) {
            for (int i = 0; i < totalProducts; i++) {
                int randomUnit = Main.random.nextInt(ROBOT_FACTORY_PU.length);
                productionUnits[i] = ROBOT_FACTORY_PU[randomUnit];
            }
        }

        //Upgrade count
        int[] upgradableCount = new int[5];

        if (buildingType == BuildingType.MEDIUM_FACTORY) {
            for (int i = 0; i < totalProducts; i++) {
                upgradableCount[i] = 1;
            }
        } else if (buildingType == BuildingType.BIG_FACTORY) {
            for (int i = 0; i < totalProducts; i++) {
                upgradableCount[i] = 2;
            }
        }

        setValues(buildingType, teamColor, productionsAvailableFromStart, productionUnits[0], productionUnits[1], productionUnits[2], productionUnits[3], productionUnits[4], upgradableCount[0], upgradableCount[1], upgradableCount[2], upgradableCount[3], upgradableCount[4], hpPercentage, hasSatelliteProtection, xTilePos, yTilePos);
    }

    public Building(BuildingType buildingType, TeamColor teamColor, int productionsAvailableFromStart, int productionUnit1, int productionUnit2, int productionUnit3, int productionUnit4, int productionUnit5, int productionUnitUpgradeItem1, int productionUnitUpgradeItem2, int productionUnitUpgradeItem3, int productionUnitUpgradeItem4, int productionUnitUpgradeItem5, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        setValues(buildingType, teamColor, productionsAvailableFromStart, productionUnit1, productionUnit2, productionUnit3, productionUnit4, productionUnit5, productionUnitUpgradeItem1, productionUnitUpgradeItem2, productionUnitUpgradeItem3, productionUnitUpgradeItem4, productionUnitUpgradeItem5, hpPercentage, hasSatelliteProtection, xTilePos, yTilePos);
    }

    public void setValues(BuildingType buildingType, TeamColor teamColor, int productionsAvailableFromStart, int productionUnit1, int productionUnit2, int productionUnit3, int productionUnit4, int productionUnit5, int productionUnitUpgradeItem1, int productionUnitUpgradeItem2, int productionUnitUpgradeItem3, int productionUnitUpgradeItem4, int productionUnitUpgradeItem5, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        this.buildingType = buildingType;
        this.teamColor = teamColor;
        this.productionsAvailableFromStart = productionsAvailableFromStart;
        this.productionUnit1 = productionUnit1;
        this.productionUnit2 = productionUnit2;
        this.productionUnit3 = productionUnit3;
        this.productionUnit4 = productionUnit4;
        this.productionUnit5 = productionUnit5;
        this.productionUnitUpgradeItem1 = productionUnitUpgradeItem1;
        this.productionUnitUpgradeItem2 = productionUnitUpgradeItem2;
        this.productionUnitUpgradeItem3 = productionUnitUpgradeItem3;
        this.productionUnitUpgradeItem4 = productionUnitUpgradeItem4;
        this.productionUnitUpgradeItem5 = productionUnitUpgradeItem5;
        this.HP = (int) (hpPercentage / 100f * 13536f);
        this.hasSatelliteProtection = hasSatelliteProtection;
        this.xTilePos = xTilePos;
        this.yTilePos = yTilePos;
        this.hpPercentage = (int)hpPercentage;

        //Update tiles
        for (int k = 0; k < 4; k++) {
            for (int j = 0; j < 4; j++) {
                MapManager.tiles[j+xTilePos][k+yTilePos].placeBuilding();
            }
        }
    }

    public enum BuildingType {
        HEADQUARTERS( 0x64, "na.png"),
        SMALL_FACTORY( 0x65, "na.png"),
        MEDIUM_FACTORY( 0x66, "na.png"),
        BIG_FACTORY(0x67, "na.png"),
        RADAR( 0x68, "na.png"),
        GOLD_MINE( 0x69, "goldBrick.png"),
        LABORATORY( 0x6A, "research.png"),
        POWER_PLANT( 0x6B, "energy.png"),
        ROBOT_FACTORY( 0x6C, "na.png"),
        ;

        int id;

        int unitIconWidth;
        int unitIconHeight;
        int[] unitIconPixels;

        BuildingType(int id, String unitIconFileName) {
            this.id = id;

            try {
                BufferedImage unitIconBI = ImageIO.read(new File("src/icons/" + unitIconFileName));

                this.unitIconWidth = unitIconBI.getWidth();
                this.unitIconHeight = unitIconBI.getHeight();
                unitIconPixels = unitIconBI.getRGB(0, 0, unitIconWidth, unitIconHeight, null, 0, unitIconWidth);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum TeamColor {
        BLUE(0),
        GREEN(1),
        WHITE(2);

        int colorID;

        TeamColor(int colorID) {
            this.colorID = colorID;
        }
    }
}
