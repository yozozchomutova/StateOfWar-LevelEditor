package eu.yozozchomutova;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Unit {

    public UnitType unitType;
    public Building.TeamColor teamColor;
    public int x, y;

    public Unit(byte unitType, byte teamColor, int x, int y) {
        this.unitType = getUnitType(unitType);
        this.teamColor = Building.TeamColor.getTeamColor(teamColor);
        this.x = x;
        this.y = y;
    }

    public static enum UnitType {
        ANTIAIR1((byte) 0x01, "Antiair 1", "ant1", UnitStand.TANK_1, 0, 0),
        ANTIAIR2((byte) 0x02, "Antiair 2", "ant2", UnitStand.TANK_2, 0, 0),
        ANTIAIR3((byte) 0x03, "Antiair 3", "ant3", UnitStand.TANK_3, 0, 10),

        ARTILLERY1((byte) 0x04, "Artillery 1", "art1", UnitStand.TANK_1, 0, 0),
        ARTILLERY2((byte) 0x05, "Artillery 2", "art2", UnitStand.TANK_2, 0, -9),
        ARTILLERY3((byte) 0x06, "Artillery 3", "art3", UnitStand.TANK_3, 0, -9),

        TANK1((byte) 0x07, "Tank 1", "tank1", UnitStand.TANK_1, 0, 0),
        TANK2((byte) 0x08, "Tank 2", "tank2", UnitStand.TANK_2, 0, 0),
        TANK3((byte) 0x09, "Tank 3", "tank3", UnitStand.TANK_3, 0, 0),

        FLAME1((byte) 0x0A, "Flame 1", "flame1", UnitStand.TANK_1, 0, 0),
        FLAME2((byte) 0x0B, "Flame 2", "flame2", UnitStand.TANK_2, 0, 0),
        FLAME3((byte) 0x0C, "Flame 3", "flame3", UnitStand.TANK_3, 0, 0),

        SPECIAL1((byte) 0x0D, "Special 1", "spec1", UnitStand.TANK_1, 0, 0),
        SPECIAL2((byte) 0x0E, "Special 2", "spec2", UnitStand.TANK_2, 0, 0),
        SPECIAL3((byte) 0x0F, "Special 3", "spec3", UnitStand.TANK_3, 0, 9),

        PVT((byte) 0x28, "PVT", "pvt"),
        KODIAK_R((byte) 0x29, "Kodiak", "kodiak", UnitStand.ROBOT, 0, 8),
        ANTIAIR_R((byte) 0x2A, "Antiair", "antiair", UnitStand.ROBOT, 0, 3),
        JAGUAR_R((byte) 0x2B, "Jaguar", "jaguar", UnitStand.ROBOT, 0, 8),
        ROTARY_R((byte) 0x2C, "Rotary", "rotary", UnitStand.ROBOT, 0, 8),
        ACHILLES_R((byte) 0x2D, "Achilles", "achilles", UnitStand.ROBOT, 0, 0),
        NAB((byte) 0x2E, "NAB", "nab"),
        ;

        public byte id;
        public int soX, soY;

        public String name;
        public String iconPath;
        public UnitStand stand;

        //Generated values
        public int[] blueVariantPixels;
        public int[] greenVariantPixels;
        public int[] neutralVariantPixels;

        public int width;
        public int height;

        UnitType(byte id, String name, String iconPath) {
            this(id, name, iconPath, null, 0, 0);
        }

        UnitType(byte id, String name, String iconPath, UnitStand stand, int soX, int soY) {
            this.id = id;
            this.name = name;
            this.iconPath = iconPath;
            this.stand = stand;
            this.soX = soX;
            this.soY = soY;

            try {
                //Blue
                BufferedImage blueVariant = ImageIO.read(new File("src/units/blue/" + iconPath + ".png"));

                this.width = blueVariant.getWidth();
                this.height = blueVariant.getHeight();

                blueVariantPixels = blueVariant.getRGB(0, 0, width, height, null, 0, width);

                //Green
                BufferedImage greenVariant = ImageIO.read(new File("src/units/green/" + iconPath + ".png"));
                greenVariantPixels = greenVariant.getRGB(0, 0, width, height, null, 0, width);

                //Neutral
                BufferedImage neutralVariant = new BufferedImage(greenVariant.getWidth(), greenVariant.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Rasterizer.applyBlackWhiteFilter(greenVariant, neutralVariant, 25);
                neutralVariantPixels = neutralVariant.getRGB(0, 0, width, height, null, 0, width);
            } catch (Exception io) {
                io.printStackTrace();
            }
        }

        public int[] getUnitPixels(Building.TeamColor team) {
            int[] buildingPixels;

            if (team == Building.TeamColor.BLUE) {
                buildingPixels = blueVariantPixels;
            } else if (team == Building.TeamColor.GREEN) {
                buildingPixels = greenVariantPixels;
            } else {
                buildingPixels = neutralVariantPixels;
            }

            return buildingPixels;
        }
    }

    public static UnitType getUnitType(byte unitByte) {
        UnitType[] unitTypes = UnitType.values();

        for (int i = 0; i < unitTypes.length; i++) {
            if (unitTypes[i].id == unitByte)
                return unitTypes[i];
        } return null;
    }

    public static enum UnitStand {
        TURRET_1("Tur1"),
        TURRET_2("Tur2"),
        TURRET_3("Tur3"),
        TANK_1("Tank1"),
        TANK_2("Tank2"),
        TANK_3("Tank3"),
        ROBOT("Robot"),
        ;

        public int width, height;

        public int[] blueVariantPixels;
        public int[] greenVariantPixels;
        public int[] neutralVariantPixels;

        UnitStand(String imgPath) {
            try {
                //Blue
                BufferedImage blueVariant = ImageIO.read(new File("src/stands/b" + imgPath + ".png"));

                this.width = blueVariant.getWidth();
                this.height = blueVariant.getHeight();

                blueVariantPixels = blueVariant.getRGB(0, 0, width, height, null, 0, width);

                //Green
                BufferedImage greenVariant = ImageIO.read(new File("src/stands/g" + imgPath + ".png"));
                greenVariantPixels = greenVariant.getRGB(0, 0, width, height, null, 0, width);

                //Neutral
                BufferedImage neutralVariant = new BufferedImage(greenVariant.getWidth(), greenVariant.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Rasterizer.applyBlackWhiteFilter(greenVariant, neutralVariant, 25);
                neutralVariantPixels = neutralVariant.getRGB(0, 0, width, height, null, 0, width);
            } catch (Exception io) {
                io.printStackTrace();
            }
        }

        public int[] getStandPixels(Building.TeamColor team) {
            int[] buildingPixels;

            if (team == Building.TeamColor.BLUE) {
                buildingPixels = blueVariantPixels;
            } else if (team == Building.TeamColor.GREEN) {
                buildingPixels = greenVariantPixels;
            } else {
                buildingPixels = neutralVariantPixels;
            }

            return buildingPixels;
        }
    }
}
