package eu.yozozchomutova;

import eu.yozozchomutova.ui.WindowBar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static eu.yozozchomutova.Main.VERSION;

public class Generator {

    private static double[][] mudWeight;
    private static double[][] waterWeight;

    private static boolean[][] blueBaseConnectedTiles;

    public static void Generate(boolean generateSurface, boolean generateObjects, double noiseSiye, int additionalGreenHeadquartersCount, int whiteBuildingCount, int biomeIndex) {
        //New Thread
        Thread t = new Thread(() -> {
            Main.doingTaskDLG.showDialog("Generating");

            //Generate data
            if (generateSurface) {
                Level.newProject(Level.renderImgWidth32, Level.renderImgHeight32);
                GenerateSurface(noiseSiye);
                rasterizeSurface();
            } else {
                Level.clearObjects();
            }

            if (generateObjects) {
                GameProperties.GenerateProperties();
                GenerateBuildings(additionalGreenHeadquartersCount, whiteBuildingCount);
            }

            //Done
            Level.rasterize();
            Main.doingTaskDLG.setVisible(false);
        });

        t.start();
    }

    public static void GenerateSurface(double noiseSiye) {
        Main.doingTaskDLG.updateProgress("Randomizing weights", 10, 100);

        MapManager.Tile[][] tiles = MapManager.tiles;

        //Generate noise
        OpenSimplexNoise noise = new OpenSimplexNoise();
        Random rand = new Random();
        double seedX = rand.nextInt(100000000);
        double seedY = rand.nextInt(100000000);

        //Height map
        waterWeight = new double[Level.renderImgWidth][Level.renderImgHeight];
        for (int y = 0; y < Level.renderImgHeight; y++)
        {
            for (int x = 0; x < Level.renderImgWidth; x++)
            {
                double value = noise.eval(x / noiseSiye + seedX, y / noiseSiye + seedY, 0.0);
                waterWeight[x][y] = value;
            }
        }

        //Ground
        for (int y = 0; y < Level.renderImgHeight32; y++)
        {
            for (int x = 0; x < Level.renderImgWidth32; x++)
            {
                if (waterWeight[x*32][y*32] > 0.2f) {
                    MapManager.tiles[x][y].forbidGround();
                }
            }
        }
    }

    public static void rasterizeSurface() {
        //Edges
        EdgeGenerator.generate();

        //Select base texture
        int biomeIndex = Main.generateDLG.biomes.getSelectedIndex();
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
        Level.backgroundLayer = new BufferedImage(Level.renderImgWidth, Level.renderImgHeight, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < Level.renderImgHeight; y++) {
            for (int x = 0; x < Level.renderImgWidth; x++) {
                Level.backgroundLayer.setRGB(x, y, baseSrfRA[(x % 512) + (y % 512) * 512]);
            }
        }
    }

    public static void GenerateBuildings(int additionalGreenHeadquartersCount, int whiteBuildingCount) {
        MapManager.Tile[][] tiles = MapManager.tiles;

        int headquartersTileWidth = Building.BuildingType.HEADQUARTERS.tileWidth;
        int headquartersTileHeight = Building.BuildingType.HEADQUARTERS.tileHeight;

        //Generate headquarters
        int x = 1, y = 1;

        //1st Blue headquarter
        Main.doingTaskDLG.updateProgress("Generating headquarters", 10, 100);
        do {
            y++;

            if (y == Level.renderImgHeight32-4) {
                y = 1;
                x++;
            }
        } while (CollidesWithSomething(tiles, x, y, headquartersTileWidth, headquartersTileHeight));

//        setPixels(x*32, y*32, b.baseWidth, b.baseHeight, b.basePixels, resultPixels);
        Level.buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.BLUE, 100f, true, x, y));
        GenerateTurrets(Building.BuildingType.ANTIAIR, Building.TeamColor.BLUE, x, y, 3, 5);
        GenerateTurrets(Building.BuildingType.DEFRAGMENTATOR, Building.TeamColor.BLUE, x, y, 3, 2);
        GenerateTurrets(Building.BuildingType.PLASMA, Building.TeamColor.BLUE, x, y, 3, 2);

        blueBaseConnectedTiles = PathFiller.startPathing(x, y);

        //1st Green headquarter
        x = Level.renderImgWidth32-4;
        y = Level.renderImgHeight32-4;

        do {
            y--;

            if (y == 0) {
                y = Level.renderImgHeight32-4;
                x--;
            }
        } while (CollidesWithSomething(tiles, x, y, headquartersTileWidth, headquartersTileHeight));

//        setPixels(x*32, y*32, g.baseWidth, g.baseHeight, g.basePixels, resultPixels);
        Level.buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.GREEN, 100f, true, x, y));
        GenerateTurrets(Building.BuildingType.ANTIAIR, Building.TeamColor.GREEN, x, y, 3, 5);
        GenerateTurrets(Building.BuildingType.DEFRAGMENTATOR, Building.TeamColor.GREEN, x, y, 3, 2);
        GenerateTurrets(Building.BuildingType.PLASMA, Building.TeamColor.GREEN, x, y, 3, 2);

        //Next Green headquarters
        for (int i = 0; i < additionalGreenHeadquartersCount; i++) {
            do {
                x = Main.random.nextInt(Level.renderImgWidth32 - 4);
                y = Main.random.nextInt(Level.renderImgHeight32 - 4);
            } while (CollidesWithSomething(tiles, x, y, headquartersTileWidth, headquartersTileHeight));

            Level.buildings.add(new Building(Building.BuildingType.HEADQUARTERS, Building.TeamColor.GREEN, 100f, true, x, y));
            GenerateTurrets(Building.BuildingType.ANTIAIR, Building.TeamColor.GREEN, x, y, 3, 5);
            GenerateTurrets(Building.BuildingType.DEFRAGMENTATOR, Building.TeamColor.GREEN, x, y, 3, 2);
            GenerateTurrets(Building.BuildingType.PLASMA, Building.TeamColor.GREEN, x, y, 3, 2);
        }

        OpenSimplexNoise noise = new OpenSimplexNoise();
        int seedX = Main.random.nextInt(100000000);
        int seedY = Main.random.nextInt(100000000);

        //Player/Enemy/Neutral zones
        double[][] zoneWeights = new double[Level.renderImgWidth32][Level.renderImgHeight32];
        for (int yy = 0; yy < zoneWeights[0].length; yy++)
        {
            for (int xx = 0; xx < zoneWeights.length; xx++)
            {
                double value = noise.eval(xx / 30.0 + seedX, yy / 30.0 + seedY, 0.0);

                if (value < -0.2f) {
                    tiles[xx][yy].team = Building.TeamColor.BLUE;
                } else if (value > 0.2f) {
                    tiles[xx][yy].team = Building.TeamColor.GREEN;
                }
            }
        }

        //Generate random white buildings
        for (int i = 0; i < whiteBuildingCount; i++) {
            Main.doingTaskDLG.updateProgress("Generating buildings", i, whiteBuildingCount);

            Building.BuildingType buildingType;
            int randomBuildingTypeID = Main.random.nextInt(8)+1;
            buildingType = Building.BuildingType.values()[randomBuildingTypeID];

            x = Main.random.nextInt(Level.renderImgWidth32-4);
            y = Main.random.nextInt(Level.renderImgHeight32-4-1)+1;

            if (CollidesWithSomething(tiles, x, y, buildingType.tileWidth, buildingType.tileHeight)) {
                i--;
            } else {
                Building.TeamColor team = tiles[x][y].team;
                int[] buildingPixels = buildingType.getBuildingPixels(team);

                if (buildingPixels != null) {
                    Level.buildings.add(new Building(buildingType, team, 100f, false, x, y));
                    if (team != Building.TeamColor.WHITE) {
                        GenerateTurrets(Building.BuildingType.ANTIAIR, team, x, y, 2, 3);
                        GenerateTurrets(Building.BuildingType.PLASMA, team, x, y, 2, 2);
                    }
                }
            }
        }
    }

    public static void GenerateTurrets(Building.BuildingType turret, Building.TeamColor team, int x, int y, int rangeGenerate, int count) {
        for (int i = 0; i < count; i++) {
            GenerateTurret(turret, team, x, y, rangeGenerate);
        }
    }

    public static void GenerateTurret(Building.BuildingType turret, Building.TeamColor team, int x, int y, int rangeGenerate) {
        int randX = 0, randY = 0;
        int attempts = 100, attempt = 0;

        MapManager.Tile[][] tiles = MapManager.tiles;
        do {
            attempt++;
            int minX = x - rangeGenerate;
            int minY = y - rangeGenerate;

            minX = Math.max(minX, 0);
            minY = Math.max(minY, 1);

            randX = Main.random.nextInt(rangeGenerate + 3 + rangeGenerate) + minX;
            randY = Main.random.nextInt(rangeGenerate + 3 + rangeGenerate) + minY;

            randX = Math.min(Level.renderImgWidth32-1, randX);
            randY = Math.min(Level.renderImgHeight32-1, randY);
        } while (CollidesWithSomething(tiles, randX, randY, turret.tileWidth, turret.tileHeight) && attempt < attempts);

        if (attempt < attempts) {
            tiles[randX][randY].placeBuilding();
            Level.buildings.add(new Building(turret, team, 100f, false, randX, randY));
        }
        else {
            System.out.println("Turret not placed!");
        }
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
}
