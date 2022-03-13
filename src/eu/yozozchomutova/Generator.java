package eu.yozozchomutova;

import eu.yozozchomutova.ui.WindowBar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static eu.yozozchomutova.Main.VERSION;

public class Generator {

    public static BufferedImage srfImage;
    public static BufferedImage resultImage;
    public static BufferedImage propertiesImage;

    private static double[][] mudWeight;
    private static double[][] waterWeight;

    private static boolean[][] blueBaseConnectedTiles;

    public static ArrayList<Building> buildings = new ArrayList<>();

    public static void Generate(double size, int additionalGreenHeadquartersCount, int whiteBuildingCount, int biomeIndex) {
        //New Thread
        Thread t = new Thread(() -> {
            Main.doingTaskDLG.showDialog("Generating");

            //Clear
            buildings.clear();

            resultImage = new BufferedImage(Main.renderImgWidth, Main.renderImgHeight, BufferedImage.TYPE_INT_RGB);
            srfImage = new BufferedImage(Main.renderImgWidth, Main.renderImgHeight, BufferedImage.TYPE_INT_RGB);

            //Generate
            GenerateSurface(size);
            GameProperties.GenerateProperties();

            //Render
            Color[][] resultPixels = new Color[Main.renderImgWidth][Main.renderImgHeight];
            Color[][] srfPixels = new Color[Main.renderImgWidth][Main.renderImgHeight];

            GenerateSurfacePixels(srfPixels, biomeIndex);

            //copy srf pixels to result pixels
            for (int y = 0; y < resultPixels[0].length; y++) {
                for (int x = 0; x < resultPixels.length; x++) {
                    resultPixels[x][y] = srfPixels[x][y];
                }
            }

            GenerateBuildings(resultPixels, additionalGreenHeadquartersCount, whiteBuildingCount);
            GenerateBuildingIcons(resultPixels);

            Render(resultImage, resultPixels);
            Render(srfImage, srfPixels);

            //Properties right-side tab
            RegenerateRightTabProperties();

            Main.surfaceRenderer.setIcon(new ImageIcon(resultImage));

            //Done
            Main.doingTaskDLG.setVisible(false);
        });

        t.start();
    }

    public static void GenerateSurface(double size) {
        Main.doingTaskDLG.updateProgress("Randomizing weights", 10, 100);

        MapManager.generateNewTiles(Main.renderImgWidth32, Main.renderImgHeight32);
        MapManager.Tile[][] tiles = MapManager.tiles;

        //Generate noise
        OpenSimplexNoise noise = new OpenSimplexNoise();
        Random rand = new Random();
        double seedX = rand.nextInt(100000000);
        double seedY = rand.nextInt(100000000);

        //Height map
        waterWeight = new double[Main.renderImgWidth][Main.renderImgHeight];
        for (int y = 0; y < Main.renderImgHeight; y++)
        {
            for (int x = 0; x < Main.renderImgWidth; x++)
            {
                double value = noise.eval(x / size + seedX, y / size + seedY, 0.0);
                waterWeight[x][y] = value;
            }
        }

        seedX = rand.nextInt(100000000);
        seedY = rand.nextInt(100000000);

        //Player/Enemy/Neutral zones
        double[][] zoneWeights = new double[Main.renderImgWidth32][Main.renderImgHeight32];
        for (int y = 0; y < zoneWeights[0].length; y++)
        {
            for (int x = 0; x < zoneWeights.length; x++)
            {
                double value = noise.eval(x / 30.0 + seedX, y / 30.0 + seedY, 0.0);

                if (value < -0.2f) {
                    tiles[x][y].team = Building.TeamColor.BLUE;
                } else if (value > 0.2f) {
                    tiles[x][y].team = Building.TeamColor.GREEN;
                }
            }
        }

        //Ground
        for (int y = 0; y < Main.renderImgHeight32; y++)
        {
            for (int x = 0; x < Main.renderImgWidth32; x++)
            {
                if (waterWeight[x*32][y*32] > 0.2f) {
                    MapManager.tiles[x][y].forbidGround();
                }
            }
        }
    }

    public static void GenerateBuildings(Color[][] resultPixels, int additionalGreenHeadquartersCount, int whiteBuildingCount) {
        MapManager.Tile[][] tiles = MapManager.tiles;
        BuildingPack b = Main.blueBPack;
        BuildingPack g = Main.greenBPack;
        BuildingPack w = Main.whiteBPack;

        //Generate headquarters
        int x = 1, y = 1;

        //1st Blue headquarter
        Main.doingTaskDLG.updateProgress("Generating headquarters", 10, 100);
        do {
            y++;

            if (y == Main.renderImgHeight32-4) {
                y = 1;
                x++;
            }
        } while (CollidesWithSomething(tiles, x, y, 4, 4));

        setPixels(x*32, y*32, b.baseWidth, b.baseHeight, b.basePixels, resultPixels);
        buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.BLUE, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0, 100f, true, x, y));
        GenerateTurrets(resultPixels, Building.BuildingType.ANTIAIR, Building.TeamColor.BLUE, x, y, 3, 5);
        GenerateTurrets(resultPixels, Building.BuildingType.DEFRAGMENTATOR, Building.TeamColor.BLUE, x, y, 3, 2);
        GenerateTurrets(resultPixels, Building.BuildingType.PLASMA, Building.TeamColor.BLUE, x, y, 3, 2);

        blueBaseConnectedTiles = PathFiller.startPathing(x, y);

        for (int yy = 0; yy < tiles[0].length; yy++) {
            for (int xx = 0; xx < tiles.length; xx++) {
                if (blueBaseConnectedTiles[xx][yy]) {
                    fillColorPixels(xx*32, yy*32, 32, 32, new Color(225, 255, 0, 50), resultPixels);
                }
            }
        }

        //1st Green headquarter
        x = Main.renderImgWidth32-4;
        y = Main.renderImgHeight32-4;

        do {
            y--;

            if (y == 0) {
                y = Main.renderImgHeight32-4;
                x--;
            }
        } while (CollidesWithSomething(tiles, x, y, 4, 4));

        setPixels(x*32, y*32, g.baseWidth, g.baseHeight, g.basePixels, resultPixels);
        buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.GREEN, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0, 100f, true, x, y));
        GenerateTurrets(resultPixels, Building.BuildingType.ANTIAIR, Building.TeamColor.GREEN, x, y, 3, 5);
        GenerateTurrets(resultPixels, Building.BuildingType.DEFRAGMENTATOR, Building.TeamColor.GREEN, x, y, 3, 2);
        GenerateTurrets(resultPixels, Building.BuildingType.PLASMA, Building.TeamColor.GREEN, x, y, 3, 2);

        //Next Green headquarters
        for (int i = 0; i < additionalGreenHeadquartersCount; i++) {
            do {
                x = Main.random.nextInt(Main.renderImgWidth32 - 4);
                y = Main.random.nextInt(Main.renderImgHeight32 - 4);
            } while (MapManager.tiles[x][y].groundForbidden || MapManager.tiles[x][y + 4].groundForbidden || MapManager.tiles[x + 4][y].groundForbidden || MapManager.tiles[x + 4][y + 4].groundForbidden);

            setPixels(x * 32, y * 32, g.baseWidth, g.baseHeight, g.basePixels, resultPixels);
            buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.GREEN, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100f, true, x, y));
            GenerateTurrets(resultPixels, Building.BuildingType.ANTIAIR, Building.TeamColor.GREEN, x, y, 3, 5);
            GenerateTurrets(resultPixels, Building.BuildingType.DEFRAGMENTATOR, Building.TeamColor.GREEN, x, y, 3, 2);
            GenerateTurrets(resultPixels, Building.BuildingType.PLASMA, Building.TeamColor.GREEN, x, y, 3, 2);
        }

        //Generate random white buildings
        for (int i = 0; i < whiteBuildingCount; i++) {
            Main.doingTaskDLG.updateProgress("Generating buildings", i, whiteBuildingCount);

            Building.BuildingType buildingType;
            int randomBuildingTypeID = Main.random.nextInt(8)+1;
            buildingType = Building.BuildingType.values()[randomBuildingTypeID];

            x = Main.random.nextInt(Main.renderImgWidth32-4);
            y = Main.random.nextInt(Main.renderImgHeight32-4-1)+1;

            if (CollidesWithSomething(tiles, x, y, 4, 4)) {
                i--;
            } else {
                Building.TeamColor team = tiles[x][y].team;
                int[] buildingPixels = buildingType.getBuildingPixels(team);

                if (buildingPixels != null) {
                    if (team != Building.TeamColor.WHITE) {
                        GenerateTurrets(resultPixels, Building.BuildingType.ANTIAIR, team, x, y, 2, 3);
                        GenerateTurrets(resultPixels, Building.BuildingType.PLASMA, team, x, y, 2, 2);
                    }

                    setPixels(x * 32, y * 32, buildingType.width, buildingType.height, buildingPixels, resultPixels);
                    buildings.add(new Building(buildingType, team, 100f, false, x, y));
                }
            }
        }
    }

    public static void GenerateTurrets(Color[][] resultPixels, Building.BuildingType turret, Building.TeamColor team, int x, int y, int rangeGenerate, int count) {
        for (int i = 0; i < count; i++) {
            GenerateTurret(resultPixels, turret, team, x, y, rangeGenerate);
        }
    }

    public static void GenerateTurret(Color[][] resultPixels, Building.BuildingType turret, Building.TeamColor team, int x, int y, int rangeGenerate) {
        int randX = 0, randY = 0;

        do {
            int minX = x - rangeGenerate;
            int minY = y - rangeGenerate;

            minX = Math.max(minX, 0);
            minY = Math.max(minY, 1);

            randX = Main.random.nextInt(rangeGenerate + 3 + rangeGenerate) + minX;
            randY = Main.random.nextInt(rangeGenerate + 3 + rangeGenerate) + minY;

            randX = Math.min(Main.renderImgWidth32, randX);
            randY = Math.min(Main.renderImgHeight32, randY);
        } while (CollidesWithSomething(MapManager.tiles, randX, randY, 1, 1));

        int[] buildingPixels = turret.getBuildingPixels(team);

        setPixels(randX * 32, randY * 32, turret.width, turret.height, buildingPixels, resultPixels);
        buildings.add(new Building(turret, team, 100f, false, randX, randY));
    }

    public static void GenerateBuildingIcons(Color[][] resultPixels) {
        for (int i = 0; i < buildings.size(); i++ ) {
            Building building = buildings.get(i);
            Building.BuildingType bt = building.buildingType;

            int xIconOffset = 0;
            for (int iconID = 0; iconID < building.totalProducts; iconID++) {
                float alphaMultiplier = iconID < building.productionsAvailableFromStart ? 1f : 0.28f; //If upgraded from start
                fillColorPixels(building.xTilePos*32 + xIconOffset, building.yTilePos*32 + 128 - bt.unitIconHeight, bt.unitIconWidth, bt.unitIconHeight, new Color(255, 0, 0, 110), resultPixels);
                setPixels(building.xTilePos*32 + xIconOffset, building.yTilePos*32 + 128 - bt.unitIconHeight, bt.unitIconWidth, bt.unitIconHeight, bt.unitIconPixels, alphaMultiplier, resultPixels);

                xIconOffset += bt.unitIconWidth + 2;
            }
        }
    }

    public static void RegenerateRightTabProperties() {
        propertiesImage = new BufferedImage(300, Main.renderImgHeight, BufferedImage.TYPE_INT_RGB);
        Color[][] propertiesPixels = new Color[propertiesImage.getWidth()][propertiesImage.getHeight()];

        GeneratePropertiesBackground(propertiesPixels);
        Render(propertiesImage, propertiesPixels);
        GenerateProperties();

        Main.playerPropertiesRenderer.setBounds(Main.frame.getWidth()-propertiesImage.getWidth(), WindowBar.BAR_HEIGHT, propertiesImage.getWidth(), Main.frame.getHeight());
        Main.playerPropertiesRenderer.setIcon(new ImageIcon(propertiesImage));
    }

    public static void GeneratePropertiesBackground(Color[][] propertiesPixels) {
        int[] propsBcg = Main.levelPropertiesBCG;

        //Background
        for (int y = 0; y < propertiesPixels[0].length; y++) {
            for (int x = 0; x < propertiesPixels.length; x++) {
                propertiesPixels[x][y] = new Color(propsBcg[(x % 149) + (y % 149) * 149]);
            }
        }
    }

    public static void GenerateProperties() {
        //Setup for texts
        Color whiteColor = new Color(240, 240, 240);
        Color blueColor = new Color(74, 134, 255);
        Color greenColor = new Color(0, 219, 40);

        Graphics2D g2d = propertiesImage.createGraphics();
        g2d.setFont(new Font("Arial", Font.PLAIN, 22));

        WriteTextToRightTab(g2d, 10, 30, whiteColor, "Type: " + Main.gameType[0]);
        WriteTextToRightTab(g2d, 10, 60, whiteColor, "SOW-LG version: " + VERSION);
        WriteTextToRightTab(g2d, 10, 90, whiteColor, "Map size: " + Main.renderImgWidth + "x" + Main.renderImgHeight);

        g2d.setFont(new Font("Arial", Font.PLAIN, 23));

        WriteTextToRightTab(g2d, 10, 120, blueColor, "€: " + GameProperties.blueMoney);
        WriteTextToRightTab(g2d, 160, 120, greenColor, "€: " + GameProperties.greenMoney);
        WriteTextToRightTab(g2d, 10, 145, blueColor, "R: " + GameProperties.blueResearch);
        WriteTextToRightTab(g2d, 160, 145, greenColor, "R: " + GameProperties.greenResearch);

        DrawGamePropertyToRightTab(g2d, 43, 170, GameProperties.bluePVT, "src/ui/bPVT");
        DrawGamePropertyToRightTab(g2d, 43, 255, GameProperties.blueCannon, "src/ui/bCannon");
        DrawGamePropertyToRightTab(g2d, 43, 325, GameProperties.blueAntiair, "src/ui/bAntiair");
        DrawGamePropertyToRightTab(g2d, 43, 395, GameProperties.bluePlasma, "src/ui/bPlasma");
        DrawGamePropertyToRightTab(g2d, 43, 465, GameProperties.blueRotary, "src/ui/bRotary");
        DrawGamePropertyToRightTab(g2d, 43, 535, GameProperties.blueDefragmentator, "src/ui/bDefrag");

        DrawGamePropertyToRightTab(g2d, 193, 170, GameProperties.greenPVT, "src/ui/gPVT");

        DrawGamePropertyToRightTab(g2d, 193, 255, GameProperties.greenCannon, "src/ui/gCannon");
        DrawGamePropertyToRightTab(g2d, 193, 325, GameProperties.greenAntiair, "src/ui/gAntiair");
        DrawGamePropertyToRightTab(g2d, 193, 395, GameProperties.greenPlasma, "src/ui/gPlasma");
        DrawGamePropertyToRightTab(g2d, 193, 465, GameProperties.greenRotary, "src/ui/gRotary");
        DrawGamePropertyToRightTab(g2d, 193, 535, GameProperties.greenDefragmentator, "src/ui/gDefrag");

        g2d.setFont(new Font("Arial", Font.PLAIN, 22));

        WriteTextToRightTab(g2d, 10, 650, blueColor, "Fighters: " + GameProperties.blueFighters);
        WriteTextToRightTab(g2d, 160, 650, greenColor, "Fighters: " + GameProperties.greenFighters);
        WriteTextToRightTab(g2d, 10, 670, blueColor, "Bombers: " + GameProperties.blueBombers);
        WriteTextToRightTab(g2d, 160, 670, greenColor, "Bombers: " + GameProperties.greenBombers);
        WriteTextToRightTab(g2d, 10, 690, blueColor, "Trojans: " + GameProperties.blueTrojans);
        WriteTextToRightTab(g2d, 160, 690, greenColor, "Trojans: " + GameProperties.greenTrojans);
        WriteTextToRightTab(g2d, 10, 710, blueColor, "Carriers: " + GameProperties.blueCarrier);
        WriteTextToRightTab(g2d, 160, 710, greenColor, "Carriers: " + GameProperties.greenCarrier);
        WriteTextToRightTab(g2d, 10, 730, blueColor, "Meteors: " + GameProperties.blueMeteors);
        WriteTextToRightTab(g2d, 160, 730, greenColor, "Meteors: " + GameProperties.greenMeteors);

        g2d.dispose();
    }

    private static void WriteTextToRightTab(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setPaint(color);
        g2d.drawString(text, x, y);
    }

    private static void DrawGamePropertyToRightTab(Graphics2D g2d, int x, int y, boolean condition, String imagePath) {
        ImageIcon img = new ImageIcon(imagePath + (condition ? "On" : "Off") + ".png");
        g2d.drawImage(img.getImage(), x, y, img.getIconWidth(), img.getIconHeight(), null);
    }

    public static BufferedImage GenerateSchemeJpg() {
        BufferedImage resultBI = new BufferedImage(resultImage.getWidth() + propertiesImage.getWidth(), resultImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultBI.createGraphics();

        g.drawImage(resultImage, null, 0, 0);
        g.drawImage(propertiesImage, null, resultImage.getWidth(), 0);

        g.dispose();
        return resultBI;
    }

    private static boolean CollidesWithSomething(MapManager.Tile[][] tiles, int x, int y, int width, int height) {
        for (int k = 0; k < height; k++) {
            for (int j = 0; j < width; j++) {
                try {
                    if (tiles[j + x][k + y].groundForbidden || tiles[j + x][k + y].placedBuilding) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException ar) {

                }
            }
        } return false;
    }

    public static void GenerateSurfacePixels(Color[][] resultPixels, int biomeIndex) {
        //Select base texture
        int[] baseSrfRA = new int[0];
        switch (biomeIndex) {
            case 0:
                baseSrfRA = Main.grassRA;
                break;
            case 1:
                baseSrfRA = Main.mudRA;
                break;
            case 2:
                baseSrfRA = Main.desertRA;
                break;
            case 3:
                baseSrfRA = Main.snowRA;
                break;
        }

        //#1 layer - Base
        for (int y = 0; y < Main.renderImgHeight; y++) {
            for (int x = 0; x < Main.renderImgWidth; x++) {
                resultPixels[x][y] = new Color(baseSrfRA[(x % 512) + (y % 512) * 512], false);
            }

            Main.doingTaskDLG.updateProgress("Applying base texture", y, Main.renderImgHeight);
        }

        //Tiles blocks
        MapManager.Tile[][] tiles = MapManager.tiles;
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                if (tiles[x][y].groundForbidden) {
                    fillColorPixels(x*32, y*32, 32, 32, new Color(0, 0, 0, 100), resultPixels);
                }

//                if (tiles[x][y].team == Building.TeamColor.BLUE) {
//                    fillColorPixels(x*32, y*32, 32, 32, new Color(0, 0, 255, 50), resultPixels);
//                } else if (tiles[x][y].team == Building.TeamColor.GREEN) {
//                    fillColorPixels(x*32, y*32, 32, 32, new Color(0, 255, 0, 50), resultPixels);
//                } else {
//                    fillColorPixels(x*32, y*32, 32, 32, new Color(255, 255, 255, 50), resultPixels);
//                }
            }

            Main.doingTaskDLG.updateProgress("Shadowing", y, tiles[0].length);
        }

        Main.snowEdges.Generate(MapManager.tiles, resultPixels);
    }

    public static void Render(BufferedImage targetBI, Color[][] pixels) {
        //Convert color pixels to bufferImage
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                targetBI.setRGB(x, y, pixels[x][y].getRGB());
            }
        }
    }

    public static void setPixels(int x, int y, EdgeGenerator.Piece piece, Color[][] destination) {
        setPixels(x, y, piece.width, piece.height, piece.pixels, destination);
    }

    public static void setPixels(int x, int y, int width, int height, int[] pixels, Color[][] destination) {
        setPixels(x, y, width, height, pixels, 1f, destination);
    }

    public static void setPixels(int x, int y, int width, int height, int[] pixels, float alphaMultiplier, Color[][] destination) {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                try {
                    Color originalCol = destination[x + w][y + h];
                    Color newCol = new Color(pixels[w + h * width], true);

                    int originalR = originalCol.getRed();
                    int originalG = originalCol.getGreen();
                    int originalB = originalCol.getBlue();

                    int newR = newCol.getRed();
                    int newG = newCol.getGreen();
                    int newB = newCol.getBlue();
                    int newA = (int) (newCol.getAlpha() * alphaMultiplier);

                    float differenceR = newR - originalR;
                    float differenceG = newG - originalG;
                    float differenceB = newB - originalB;

                    float affectionRate = newA / 255f;

                    destination[x + w][y + h] = new Color(
                            (int) (originalR + differenceR * affectionRate),
                            (int) (originalG + differenceG * affectionRate),
                            (int) (originalB + differenceB * affectionRate)
                    );

                } catch (ArrayIndexOutOfBoundsException io) {}
            }
        }
    }

    public static void fillColorPixels(int x, int y, int width, int height, Color color, Color[][] destination) {
        int[] colors = new int[width * height];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = color.getRGB();
        }

        setPixels(x, y, width, height, colors, destination);
    }
}
