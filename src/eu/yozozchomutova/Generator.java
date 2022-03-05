package eu.yozozchomutova;

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

    private static JDialog progressDialog;
    private static JLabel progressText;
    private static JProgressBar progressBar;

    private static double[][] mudWeight;
    private static double[][] waterWeight;

    public static ArrayList<Building> buildings = new ArrayList<>();

    private static void createProgressDialog() {
        progressDialog = new JDialog(Main.frame, "Please wait...");
        progressDialog.setBounds(400, 400, 500, 220);
        progressDialog.getContentPane().setBackground(Color.BLACK);
        progressDialog.setLayout(null);

        progressText = new JLabel("...");
        progressText.setForeground(Color.WHITE);
        progressText.setBounds(10, 10, 500, 70);
        progressText.setVerticalAlignment(SwingConstants.TOP);
        progressText.setFont(new Font("arial", Font.PLAIN, 20));
        progressDialog.add(progressText);

        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(Color.RED);
        progressBar.setBounds(0, 150, 500, 70);
        progressDialog.add(progressBar);

        progressDialog.setVisible(true);
    }

    public static void Generate(double size, int additionalGreenHeadquartersCount, int whiteBuildingCount, int biomeIndex) {
        //New Thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                createProgressDialog();

                //Clear
                buildings.clear();

                resultImage = new BufferedImage(Main.renderImgWidth, Main.renderImgHeight, BufferedImage.TYPE_INT_RGB);
                srfImage = new BufferedImage(Main.renderImgWidth, Main.renderImgHeight, BufferedImage.TYPE_INT_RGB);
                propertiesImage = new BufferedImage(300, Main.renderImgHeight, BufferedImage.TYPE_INT_RGB);

                //Generate
                GenerateSurface(size);
                GameProperties.GenerateProperties();

                //Render
                Color[][] resultPixels = new Color[Main.renderImgWidth][Main.renderImgHeight];
                Color[][] srfPixels = new Color[Main.renderImgWidth][Main.renderImgHeight];
                Color[][] propertiesPixels = new Color[propertiesImage.getWidth()][propertiesImage.getHeight()];

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
                GeneratePropertiesBackground(propertiesPixels);
                Render(propertiesImage, propertiesPixels);
                GenerateProperties();

                Main.surfaceRenderer.setIcon(new ImageIcon(resultImage));
                Main.playerPropertiesRenderer.setBounds(Main.frame.getWidth()-propertiesImage.getWidth(), 0, propertiesImage.getWidth(), Main.frame.getHeight());
                Main.playerPropertiesRenderer.setIcon(new ImageIcon(propertiesImage));

                //Done
                progressDialog.setVisible(false);
            }
        });

        t.start();
    }

    public static void GenerateSurface(double size) {
        //Generate noise
        OpenSimplexNoise noise = new OpenSimplexNoise();
        Random rand = new Random();
        double seedX = rand.nextInt(1000000);
        double seedY = rand.nextInt(1000000);

        /*mudWeight = new double[Main.renderImgWidth][Main.renderImgHeight];
        for (int y = 0; y < Main.renderImgHeight; y++)
        {
            for (int x = 0; x < Main.renderImgWidth; x++)
            {
                double value = noise.eval(x / 900.0 + seed, y / 900.0 + seed, 0.0);
                mudWeight[x][y] = value;
            }
        }*/

        waterWeight = new double[Main.renderImgWidth][Main.renderImgHeight];
        for (int y = 0; y < Main.renderImgHeight; y++)
        {
            for (int x = 0; x < Main.renderImgWidth; x++)
            {
                double value = noise.eval(x / size + seedX, y / size + seedY, 0.0);
                waterWeight[x][y] = value;
            }
        }

        progressText.setText("Generating tiles");
        MapManager.generateNewTiles(Main.renderImgWidth32, Main.renderImgHeight32);

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
        progressText.setText("Blue headquarters");
        do {
            y++;

            if (y == Main.renderImgHeight32-4) {
                y = 1;
                x++;
            }
        } while (CollidesWithSomething(tiles, x, y, 4, 4));

        setPixels(x*32, y*32, b.baseWidth, b.baseHeight, b.basePixels, resultPixels);
        buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.BLUE, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0, 100f, true, x, y));

        //1st Green headquarter
        progressText.setText("Green headquarters");
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

        //Next Green headquarters
        for (int i = 0; i < additionalGreenHeadquartersCount; i++) {
            do {
                x = Main.random.nextInt(Main.renderImgWidth32 - 4);
                y = Main.random.nextInt(Main.renderImgHeight32 - 4);
            } while (MapManager.tiles[x][y].groundForbidden || MapManager.tiles[x][y + 4].groundForbidden || MapManager.tiles[x + 4][y].groundForbidden || MapManager.tiles[x + 4][y + 4].groundForbidden);

            setPixels(x * 32, y * 32, g.baseWidth, g.baseHeight, g.basePixels, resultPixels);
            buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.GREEN, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100f, true, x, y));
        }

        //Generate random white buildings
        for (int i = 0; i < whiteBuildingCount; i++) {
            progressText.setText("Generating building: " + i);

            int[] buildingPixels;
            int buildingWidth, buildingHeight;

            Building.BuildingType buildingType;
            int randomBuildingTypeID = Main.random.nextInt(Building.BuildingType.values().length-1)+1;
            buildingType = Building.BuildingType.values()[randomBuildingTypeID];

            switch (buildingType) {
                case HEADQUARTERS: //Base
                    buildingPixels = w.basePixels;
                    buildingWidth = w.baseWidth;
                    buildingHeight = w.baseHeight;
                    break;
                case SMALL_FACTORY: //Factory light
                    buildingPixels = w.FlightPixels;
                    buildingWidth = w.FlightWidth;
                    buildingHeight = w.FlightHeight;
                    break;
                case MEDIUM_FACTORY: //Factory medium
                    buildingPixels = w.FmediumPixels;
                    buildingWidth = w.FmediumWidth;
                    buildingHeight = w.FmediumHeight;
                    break;
                case BIG_FACTORY: //Factory big
                    buildingPixels = w.FheavyPixels;
                    buildingWidth = w.FheavyWidth;
                    buildingHeight = w.FheavyHeight;
                    break;
                case ROBOT_FACTORY: //Factory robot
                    buildingPixels = w.FrobotPixels;
                    buildingWidth = w.FrobotWidth;
                    buildingHeight = w.FrobotHeight;
                    break;
                case LABORATORY: //Lab
                    buildingPixels = w.labPixels;
                    buildingWidth = w.labWidth;
                    buildingHeight = w.labHeight;
                    break;
                case GOLD_MINE: //Mine
                    buildingPixels = w.minePixels;
                    buildingWidth = w.mineWidth;
                    buildingHeight = w.mineHeight;
                    break;
                case RADAR: //Radar
                    buildingPixels = w.radarPixels;
                    buildingWidth = w.radarWidth;
                    buildingHeight = w.radarHeight;
                    break;
                case POWER_PLANT: //Wind
                    buildingPixels = w.windPixels;
                    buildingWidth = w.windWidth;
                    buildingHeight = w.windHeight;
                    break;
                default:
                    buildingPixels = new int[0];
                    buildingWidth = 0;
                    buildingHeight = 0;
                    break;
            }

            x = Main.random.nextInt(Main.renderImgWidth32-4);
            y = Main.random.nextInt(Main.renderImgHeight32-4);

            if (CollidesWithSomething(tiles, x, y, 4, 4)) {
                i--;
            } else {
                setPixels(x * 32, y * 32, buildingWidth, buildingHeight, buildingPixels, resultPixels);
                buildings.add(new Building(buildingType, Building.TeamColor.WHITE, 100f, false, x, y));
            }
        }
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

    public static void GeneratePropertiesBackground(Color[][] propertiesPixels) {
        int[] propsBcg = Main.propsBcg;

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
        g2d.setFont(new Font("Arial", Font.PLAIN, 28));

        WriteTextToRightTab(g2d, 10, 30, whiteColor, "SOW-LG version: " + VERSION);
        WriteTextToRightTab(g2d, 10, 60, whiteColor, "Map size: " + Main.renderImgWidth + "x" + Main.renderImgHeight);

        g2d.setFont(new Font("Arial", Font.PLAIN, 28));

        WriteTextToRightTab(g2d, 10, 90, blueColor, "€: " + GameProperties.blueMoney);
        WriteTextToRightTab(g2d, 160, 90, greenColor, "€: " + GameProperties.greenMoney);
        WriteTextToRightTab(g2d, 10, 120, blueColor, "R: " + GameProperties.blueResearch);
        WriteTextToRightTab(g2d, 160, 120, greenColor, "R: " + GameProperties.greenResearch);

        g2d.setFont(new Font("Arial", Font.PLAIN, 22));

        WriteTextToRightTab(g2d, 10, 175, blueColor, "PVT: " + GameProperties.bluePVT);
        WriteTextToRightTab(g2d, 160, 175, greenColor, "PVT: " + GameProperties.greenPVT);
        WriteTextToRightTab(g2d, 10, 195, blueColor, "Cannon: " + GameProperties.blueCannon);
        WriteTextToRightTab(g2d, 160, 195, greenColor, "Cannon: " + GameProperties.greenCannon);
        WriteTextToRightTab(g2d, 10, 215, blueColor, "Anti-Air: " + GameProperties.blueAntiair);
        WriteTextToRightTab(g2d, 160, 215, greenColor, "Anti-Air: " + GameProperties.greenAntiair);
        WriteTextToRightTab(g2d, 10, 235, blueColor, "Plasma: " + GameProperties.bluePlasma);
        WriteTextToRightTab(g2d, 160, 235, greenColor, "Plasma: " + GameProperties.greenPlasma);
        WriteTextToRightTab(g2d, 10, 255, blueColor, "Rotary: " + GameProperties.blueRotary);
        WriteTextToRightTab(g2d, 160, 255, greenColor, "Rotary: " + GameProperties.greenRotary);
        WriteTextToRightTab(g2d, 10, 275, blueColor, "Defrag.: " + GameProperties.blueDefragmentator);
        WriteTextToRightTab(g2d, 160, 275, greenColor, "Defrag.: " + GameProperties.greenDefragmentator);

        WriteTextToRightTab(g2d, 10, 325, blueColor, "Fighters: " + GameProperties.blueFighters);
        WriteTextToRightTab(g2d, 160, 325, greenColor, "Fighters: " + GameProperties.greenFighters);
        WriteTextToRightTab(g2d, 10, 345, blueColor, "Bombers: " + GameProperties.blueBombers);
        WriteTextToRightTab(g2d, 160, 345, greenColor, "Bombers: " + GameProperties.greenBombers);
        WriteTextToRightTab(g2d, 10, 365, blueColor, "Trojans: " + GameProperties.blueTrojans);
        WriteTextToRightTab(g2d, 160, 365, greenColor, "Trojans: " + GameProperties.greenTrojans);
        WriteTextToRightTab(g2d, 10, 385, blueColor, "Carriers: " + GameProperties.blueCarrier);
        WriteTextToRightTab(g2d, 160, 385, greenColor, "Carriers: " + GameProperties.greenCarrier);
        WriteTextToRightTab(g2d, 10, 405, blueColor, "Meteors: " + GameProperties.blueMeteors);
        WriteTextToRightTab(g2d, 160, 405, greenColor, "Meteors: " + GameProperties.greenMeteors);

        g2d.dispose();
    }

    private static void WriteTextToRightTab(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setPaint(color);
        g2d.drawString(text, x, y);
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
                if (tiles[j+x][k+y].groundForbidden || tiles[j+x][k+y].placedBuilding) {
                    return true;
                }
            }
        } return false;
    }

    public static void GenerateSurfacePixels(Color[][] resultPixels, int biomeIndex) {
        //Select base texture
        int[] baseSrfRA = new int[0];
        switch (biomeIndex) {
            case 0:
                baseSrfRA = Main.snowRA;
                //TODO baseSrfRA = Main.grassRA;
                break;
            case 1:
                baseSrfRA = Main.mudRA;
                break;
            case 2:
                baseSrfRA = Main.grassRA;
                break;
            case 3:
                baseSrfRA = Main.snowRA;
                break;
            case 4:
                baseSrfRA = Main.desertRA;
                break;
        }

        //#1 layer - Base
        progressText.setText("Applying base texture");

        for (int y = 0; y < Main.renderImgHeight; y++) {
            for (int x = 0; x < Main.renderImgWidth; x++) {
                resultPixels[x][y] = new Color(baseSrfRA[(x % 512) + (y % 512) * 512], false);
            }
        }

        //Map Edges
        progressText.setText("Generating random edges");

        //Tiles blocks
        progressText.setText("TILES - BLOCKS");
        MapManager.Tile[][] tiles = MapManager.tiles;
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                if (tiles[x][y].groundForbidden) {
                    fillColorPixels(x*32, y*32, 32, 32, new Color(0, 0, 0, 50), resultPixels);
                }
            }
        }

        Main.snowEdges.Generate(MapManager.tiles, resultPixels);

        //Done
        progressText.setText("Done");
        progressDialog.setVisible(false);
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
