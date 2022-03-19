package eu.yozozchomutova;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Building {

    public static final ProducingUnit[] BASIC_FACTORY_PU = {ProducingUnit.ANTIAIR1, ProducingUnit.ARTILLERY1, ProducingUnit.TANK1, ProducingUnit.FLAME1, ProducingUnit.SPECIAL1};
    public static final ProducingUnit[] GOLD_MINE_PU = {ProducingUnit.GOLD1, ProducingUnit.GOLD2, ProducingUnit.GOLD3, ProducingUnit.GOLD3, ProducingUnit.GOLD3};
    public static final ProducingUnit[] RESEARCH_PU = {ProducingUnit.RESEARCH1, ProducingUnit.RESEARCH2, ProducingUnit.RESEARCH3, ProducingUnit.RESEARCH3, ProducingUnit.RESEARCH3};
    public static final ProducingUnit[] ENERGY_PU = {ProducingUnit.ENERGY1, ProducingUnit.ENERGY2, ProducingUnit.ENERGY3, ProducingUnit.ENERGY3, ProducingUnit.ENERGY3};
    public static final ProducingUnit[] RADAR_PU = {ProducingUnit.BOMBER, ProducingUnit.CARRIER, ProducingUnit.FIGHTER, ProducingUnit.TRIPLER, ProducingUnit.METEORITES};
    public static final ProducingUnit[] ROBOT_FACTORY_PU = {ProducingUnit.KODIAK_R, ProducingUnit.ANTIAIR_R, ProducingUnit.JAGUAR_R, ProducingUnit.ROTARY_R, ProducingUnit.ACHILLES_R, ProducingUnit.NAB};

    public BuildingType buildingType;
    public TeamColor teamColor;

    public int totalProducts;
    public int productionsAvailableFromStart;

    public ProducingUnit[] productionUnits = new ProducingUnit[5];
    public int[] productionUnitUpgradeItems = new int[5];

    public int HP;

    public boolean hasSatelliteProtection;

    public int xTilePos;
    public int yTilePos;

    public int hpPercentage; //0-100 %

    //UI
    public JLabel mainImage, pu1, pu2, pu3, pu4, pu5;

    public Building(BuildingType buildingType, TeamColor teamColor, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        //If it's NOT TURRET
        if (buildingType != BuildingType.CANNON &&
                buildingType != BuildingType.ANTIAIR &&
                buildingType != BuildingType.PLASMA &&
                buildingType != BuildingType.ROTARY &&
                buildingType != BuildingType.DEFRAGMENTATOR) {

            totalProducts = Main.random.nextInt(3)+1;
            productionsAvailableFromStart = Main.random.nextInt(totalProducts+1);

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
            } else {
                totalProducts = 0;
            }

            //Upgrade count
            if (buildingType == BuildingType.MEDIUM_FACTORY) {
                for (int i = 0; i < totalProducts; i++) {
                    productionUnitUpgradeItems[i] = 1;
                }
            } else if (buildingType == BuildingType.BIG_FACTORY) {
                for (int i = 0; i < totalProducts; i++) {
                    productionUnitUpgradeItems[i] = 2;
                }
            }
        }

        setValues(buildingType, teamColor, productionsAvailableFromStart, hpPercentage, hasSatelliteProtection, xTilePos, yTilePos);
    }

    public Building(BuildingType buildingType, TeamColor teamColor, int productionsAvailableFromStart, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        setValues(buildingType, teamColor, productionsAvailableFromStart, hpPercentage, hasSatelliteProtection, xTilePos, yTilePos);
    }

    public Building(byte buildingType, byte teamColor, int productionsAvailableFromStart, ProducingUnit[] productionUnits, int[] productionUnitUpgradeItems, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        setValues(getBuildingType(buildingType), TeamColor.getTeamColor(teamColor), productionsAvailableFromStart, hpPercentage, hasSatelliteProtection, xTilePos, yTilePos);

        this.productionUnits = productionUnits;
        this.productionUnitUpgradeItems = productionUnitUpgradeItems;

        //Count total products
        for (ProducingUnit pu : productionUnits) {
            if (pu != null)
                totalProducts++;
        }
    }

    public void setValues(BuildingType buildingType, TeamColor teamColor, int productionsAvailableFromStart, float hpPercentage, boolean hasSatelliteProtection, int xTilePos, int yTilePos) {
        this.buildingType = buildingType;
        this.teamColor = teamColor;
        this.productionsAvailableFromStart = productionsAvailableFromStart;
        this.HP = (int) (hpPercentage / 100f * 13536f);
        this.hasSatelliteProtection = hasSatelliteProtection;
        this.xTilePos = xTilePos;
        this.yTilePos = yTilePos;
        this.hpPercentage = (int)hpPercentage;

        //Update tiles
        for (int k = 0; k < buildingType.tileHeight; k++) {
            for (int j = 0; j < buildingType.tileWidth; j++) {
                try {
                    MapManager.tiles[j + xTilePos][k + yTilePos].placeBuilding();
                } catch (ArrayIndexOutOfBoundsException ar) {

                }
            }
        }

        //DO NOT BLOCK exit path
        if (buildingType == BuildingType.SMALL_FACTORY || buildingType == BuildingType.MEDIUM_FACTORY || buildingType == BuildingType.BIG_FACTORY || buildingType == BuildingType.ROBOT_FACTORY) {
            MapManager.placeBuilding(xTilePos + buildingType.tileWidth, yTilePos + buildingType.tileHeight-1);
            MapManager.placeBuilding(xTilePos + buildingType.tileWidth, yTilePos + buildingType.tileHeight);
            MapManager.placeBuilding(xTilePos + buildingType.tileWidth-1, yTilePos + buildingType.tileHeight);

            MapManager.placeBuilding(xTilePos + buildingType.tileWidth + 1, yTilePos + buildingType.tileHeight);
            MapManager.placeBuilding(xTilePos + buildingType.tileWidth + 1, yTilePos + buildingType.tileHeight + 1);
            MapManager.placeBuilding(xTilePos + buildingType.tileWidth, yTilePos + buildingType.tileHeight + 1);
        }
    }

    public static BuildingType getBuildingType(byte buildingByte) {
        BuildingType[] buildingTypes = BuildingType.values();

        for (int i = 0; i < buildingTypes.length; i++) {
            if (buildingTypes[i].id == buildingByte)
                return buildingTypes[i];
        } return null;
    }

    public enum BuildingType {
        HEADQUARTERS("Headquarters", 0x64, 4, 5, "base.png", true),
        SMALL_FACTORY("Small factory", 0x65, 3, 3, "Flight.png", true),
        MEDIUM_FACTORY("Medium factory", 0x66, 4, 4, "Fmedium.png", true),
        BIG_FACTORY("Big factory", 0x67, 4, 4, "Fheavy.png", true),
        RADAR("Radar", 0x68, 3, 4, "radar.png", true),
        GOLD_MINE("Gold mine", 0x69, 3, 3, "mine.png", true),
        LABORATORY("Laboratory", 0x6A, 3, 3, "lab.png", true),
        POWER_PLANT("Wind", 0x6B, 2, 4, "wind.png", true),
        ROBOT_FACTORY("Robot factory", 0x6C, 4, 3, "Frobot.png", true),

        CANNON("Cannon", 0x19, 1, 1, "cannon.png", false, Unit.UnitStand.TURRET_1, 0, 0),
        ANTIAIR("Anti-air", 0x1A, 1, 1, "antiair.png", false, Unit.UnitStand.TURRET_2, 0, 0),
        PLASMA("Plasma", 0x1B, 1, 1, "plasma.png", false, Unit.UnitStand.TURRET_2, 0, 0),
        ROTARY("Rotary", 0x1C, 1, 2, "rotary.png", false, Unit.UnitStand.TURRET_3, 0, 0),
        DEFRAGMENTATOR("Defragmentator", 0x1D, 1, 2, "defrag.png", false, Unit.UnitStand.TURRET_3, 0, 0),
        ;

        public String name;
        public int id;
        public int tileWidth, tileHeight;
        public Unit.UnitStand stand;
        public int soX, soY;

        public int[] blueVariantPixels;
        public int[] greenVariantPixels;
        public int[] whiteVariantPixels;
        public int[] neutralVariantPixels;

        public int width;
        public int height;

        private boolean hasWhiteVariant;

        BuildingType(String name, int id, int tileWidth, int tileHeight, String buildingPath, boolean hasWhiteVariant) {
            this(name, id, tileWidth, tileHeight, buildingPath, hasWhiteVariant, null, 0, 0);
        }

        BuildingType(String name, int id, int tileWidth, int tileHeight, String buildingPath, boolean hasWhiteVariant, Unit.UnitStand stand, int soX, int soY) { //soX/soY = Stand offset X/Y
            this.name = name;
            this.id = id;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.hasWhiteVariant = hasWhiteVariant;
            this.stand = stand;
            this.soX = soX;
            this.soY = soY;

            try {
                //Blue
                BufferedImage blueVariant = ImageIO.read(new File("src/buildings/blue/" + buildingPath));

                this.width = blueVariant.getWidth();
                this.height = blueVariant.getHeight();

                blueVariantPixels = blueVariant.getRGB(0, 0, width, height, null, 0, width);

                //Green
                BufferedImage greenVariant = ImageIO.read(new File("src/buildings/green/" + buildingPath));
                greenVariantPixels = greenVariant.getRGB(0, 0, width, height, null, 0, width);

                //White
                if (hasWhiteVariant) {
                    BufferedImage whiteVariant = ImageIO.read(new File("src/buildings/white/" + buildingPath));
                    whiteVariantPixels = whiteVariant.getRGB(0, 0, width, height, null, 0, width);
                } else {
                    //Neutral
                    BufferedImage neutralVariant = new BufferedImage(greenVariant.getWidth(), greenVariant.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Rasterizer.applyBlackWhiteFilter(greenVariant, neutralVariant, 25);
                    neutralVariantPixels = neutralVariant.getRGB(0, 0, width, height, null, 0, width);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int[] getBuildingPixels(TeamColor team) {
            int[] buildingPixels;

            if (team == Building.TeamColor.BLUE) {
                buildingPixels = blueVariantPixels;
            } else if (team == Building.TeamColor.GREEN) {
                buildingPixels = greenVariantPixels;
            } else if (hasWhiteVariant) {
                buildingPixels = whiteVariantPixels;
            } else {
                buildingPixels = neutralVariantPixels;
            }

            return buildingPixels;
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

        public static TeamColor getTeamColor(byte teamByte) {
            TeamColor[] teamColors = TeamColor.values();

            for (int i = 0; i < teamColors.length; i++) {
                if (teamColors[i].colorID == teamByte)
                    return teamColors[i];
            } return null;
        }
    }
}
