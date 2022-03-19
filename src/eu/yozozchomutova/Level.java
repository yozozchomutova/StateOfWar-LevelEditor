package eu.yozozchomutova;

import com.sun.imageio.spi.RAFImageInputStreamSpi;
import eu.yozozchomutova.ui.WindowBar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import static eu.yozozchomutova.Main.VERSION;

public class Level {

    //Size properties
    public static int renderImgWidth;
    public static int renderImgHeight;
    public static int renderImgWidth32;
    public static int renderImgHeight32;

    public static BufferedImage backgroundLayer;
    public static float[][] grassLayer, mudLayer, desertLayer, snowLayer;

    public static ArrayList<MapObject> mapObjects = new ArrayList<>();
    public static ArrayList<Building> buildings = new ArrayList<>();
    public static ArrayList<Unit> units = new ArrayList<>();

    //Runtime generated
    public static BufferedImage srfImage, mapSchemeImage, levelPropertiesImage;

    public static void clearAll() {
        clearSurface();
        clearObjects();
    }

    public static void clearSurface() {
        backgroundLayer = new BufferedImage(renderImgWidth, renderImgHeight, BufferedImage.TYPE_INT_RGB);
        mapObjects.clear();
    }

    public static void clearObjects() {
        buildings.clear();
        units.clear();

        MapManager.Tile[][] tiles = MapManager.tiles;
        for (int y = 0; y < renderImgHeight32; y++) {
            for (int x = 0; x < renderImgWidth32; x++) {
                tiles[x][y].placedBuilding = false;
            }
        }
    }

    public static void rasterize() {
        Thread t = new Thread(() -> {
            //Setup empty colors
            Color[][] srfPixels = new Color[renderImgWidth][renderImgHeight];
            Color[][] mapSchemePixels = new Color[renderImgWidth][renderImgHeight];
//            Color[][] levelPropertiesPixels = new Color[300][renderImgHeight];

            //Rasterize srf jpg
            generateSurfacePixels(srfPixels, 0);

            //copy srf pixels to mapScheme pixels
            Rasterizer.copyPixels(srfPixels, mapSchemePixels);

            //Generate scheme stuff
            ArrayList<Object> renderableObjects = renderableSort();
            generateBuildingsPixels(renderableObjects, mapSchemePixels);
//            generateUnitPixels(mapSchemePixels);
            generateBuildingIcons(mapSchemePixels);

            //Setup images
            srfImage = new BufferedImage(srfPixels.length, srfPixels[0].length, BufferedImage.TYPE_INT_RGB);
            mapSchemeImage = new BufferedImage(mapSchemePixels.length, mapSchemePixels[0].length, BufferedImage.TYPE_INT_RGB);
//            levelPropertiesImage = new BufferedImage(levelPropertiesPixels.length, levelPropertiesPixels[0].length, BufferedImage.TYPE_INT_RGB);

            //Level properties
            regenerateRightTabProperties();

            //Convert pixels to buffered images
            Rasterizer.renderToImage(srfImage, srfPixels);
            Rasterizer.renderToImage(mapSchemeImage, mapSchemePixels);
//            Rasterizer.renderToImage(levelPropertiesImage, levelPropertiesPixels);

            //Update UI
            Main.surfaceRenderer.setIcon(new ImageIcon(mapSchemeImage));
//            Main.playerPropertiesRenderer.setBounds(Main.frame.getWidth()-levelPropertiesPixels.length, WindowBar.BAR_HEIGHT, levelPropertiesPixels.length, Main.frame.getHeight());
//            Main.playerPropertiesRenderer.setIcon(new ImageIcon(levelPropertiesImage));
        });
        t.start();
    }

    public static void regenerateRightTabProperties() {
        levelPropertiesImage = new BufferedImage(300, renderImgHeight, BufferedImage.TYPE_INT_RGB);
        Color[][] levelPropertiesPixels = new Color[levelPropertiesImage.getWidth()][levelPropertiesImage.getHeight()];

        Rasterizer.fillBackground(levelPropertiesPixels, Main.levelPropertiesBCG);
        Rasterizer.renderToImage(levelPropertiesImage, levelPropertiesPixels);
        GenerateProperties(levelPropertiesImage);

        Main.playerPropertiesRenderer.setBounds(Main.frame.getWidth()-levelPropertiesImage.getWidth(), WindowBar.BAR_HEIGHT, levelPropertiesImage.getWidth(), Main.frame.getHeight());
        Main.playerPropertiesRenderer.setIcon(new ImageIcon(levelPropertiesImage));
    }

    public static void GenerateProperties(BufferedImage referenceImage) {
        //Setup for texts
        Color whiteColor = new Color(240, 240, 240);
        Color blueColor = new Color(74, 134, 255);
        Color greenColor = new Color(0, 219, 40);

        Graphics2D g2d = referenceImage.createGraphics();
        g2d.setFont(new Font("Arial", Font.PLAIN, 21));

        //Map info
        Rasterizer.WriteTextToRightTab(g2d, 10, 25, whiteColor, "Type: " + Main.gameType[0]);
        Rasterizer.WriteTextToRightTab(g2d, 10, 55, whiteColor, "SOW-LG version: " + VERSION);
        Rasterizer.WriteTextToRightTab(g2d, 10, 85, whiteColor, "Map size: " + renderImgWidth + "x" + renderImgHeight);

        //Stats
        Rasterizer.DrawGamePropertyToRightTab(g2d, 15, 105, "src/icons/bCredits");
        Rasterizer.WriteTextToRightTab(g2d, 80, 145, blueColor, "" + GameProperties.blueMoney);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 165, 105, "src/icons/gCredits");
        Rasterizer.WriteTextToRightTab(g2d, 230, 145, greenColor, "" + GameProperties.greenMoney);

        Rasterizer.DrawGamePropertyToRightTab(g2d, 15, 175, "src/icons/bRes");
        Rasterizer.WriteTextToRightTab(g2d, 80, 215, blueColor, "" + GameProperties.blueResearch);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 165, 175, "src/icons/gRes");
        Rasterizer.WriteTextToRightTab(g2d, 230, 215, greenColor, "" + GameProperties.greenResearch);

        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 270, GameProperties.bluePVT, "src/ui/bPVT");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 355, GameProperties.blueCannon, "src/ui/bCannon");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 425, GameProperties.blueAntiair, "src/ui/bAntiair");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 495, GameProperties.bluePlasma, "src/ui/bPlasma");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 565, GameProperties.blueRotary, "src/ui/bRotary");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 635, GameProperties.blueDefragmentator, "src/ui/bDefrag");

        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 270, GameProperties.greenPVT, "src/ui/gPVT");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 355, GameProperties.greenCannon, "src/ui/gCannon");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 425, GameProperties.greenAntiair, "src/ui/gAntiair");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 495, GameProperties.greenPlasma, "src/ui/gPlasma");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 565, GameProperties.greenRotary, "src/ui/gRotary");
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 635, GameProperties.greenDefragmentator, "src/ui/gDefrag");

        //Air forces
        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 710, "src/icons/bFighter");
        Rasterizer.WriteTextToRightTab(g2d, 110, 750, blueColor, "" + GameProperties.blueFighters);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 710, "src/icons/gFighter");
        Rasterizer.WriteTextToRightTab(g2d, 260, 750, greenColor, "" + GameProperties.greenFighters);

        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 780, "src/icons/bBomber");
        Rasterizer.WriteTextToRightTab(g2d, 110, 820, blueColor, "" + GameProperties.blueBombers);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 780, "src/icons/gBomber");
        Rasterizer.WriteTextToRightTab(g2d, 260, 820, greenColor, "" + GameProperties.greenBombers);

        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 850, "src/icons/bTripler");
        Rasterizer.WriteTextToRightTab(g2d, 110, 890, blueColor, "" + GameProperties.blueTrojans);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 850, "src/icons/gTripler");
        Rasterizer.WriteTextToRightTab(g2d, 260, 890, greenColor, "" + GameProperties.greenTrojans);

        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 920, "src/icons/bCarryall");
        Rasterizer.WriteTextToRightTab(g2d, 110, 960, blueColor, "" + GameProperties.blueCarrier);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 920, "src/icons/gCarryall");
        Rasterizer.WriteTextToRightTab(g2d, 260, 960, greenColor, "" + GameProperties.greenCarrier);

        Rasterizer.DrawGamePropertyToRightTab(g2d, 43, 990, "src/icons/bMeteor");
        Rasterizer.WriteTextToRightTab(g2d, 110, 1030, blueColor, "" + GameProperties.blueMeteors);
        Rasterizer.DrawGamePropertyToRightTab(g2d, 193, 990, "src/icons/gMeteor");
        Rasterizer.WriteTextToRightTab(g2d, 260, 1030, greenColor, "" + GameProperties.greenMeteors);

        g2d.dispose();
    }

    public static void generateSurfacePixels(Color[][] resultPixels, int biomeIndex) {
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
        for (int y = 0; y < renderImgHeight; y++) {
            for (int x = 0; x < renderImgWidth; x++) {
                //resultPixels[x][y] = new Color(baseSrfRA[(x % 512) + (y % 512) * 512], false);
                resultPixels[x][y] = new Color(backgroundLayer.getRGB(x, y));
            }

            Main.doingTaskDLG.updateProgress("Applying base texture", y, renderImgHeight);
        }

        //Tiles blocks
        MapManager.Tile[][] tiles = MapManager.tiles;
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                if (tiles[x][y].groundForbidden) {
                    Rasterizer.fillColorPixels(x*32, y*32, 32, 32, new Color(0, 0, 0, 100), resultPixels);
                }
                else if (tiles[x][y].placedBuilding) {
                    //Rasterizer.fillColorPixels(x * 32, y * 32, 32, 32, new Color(255, 0, 0, 100), resultPixels);
                }
            }

            Main.doingTaskDLG.updateProgress("Shadowing", y, tiles[0].length);
        }

        //Map objects
        for (MapObject mapObject : mapObjects) {
            Rasterizer.setPixels(mapObject.x, mapObject.y, mapObject.mapObjectType.image, resultPixels);
        }
    }

    public static ArrayList<Object> renderableSort() {
        ArrayList<Object> renderableObjects = new ArrayList<>();

        for (int y = 0; y < renderImgHeight32; y++) {
            for (int x = 0; x < renderImgWidth32; x++) {
                for (Building b : buildings) {
                    if (b.xTilePos == x && b.yTilePos == y) {
                        renderableObjects.add(b);
                        break;
                    }
                }

                for (Unit u : units) {
                    if ((int)(u.x/32f) == x && (int)(u.y/32f) == y) {
                        renderableObjects.add(u);
                        break;
                    }
                }
            }
        } return renderableObjects;
    }

    public static void generateBuildingsPixels(ArrayList<Object> renderableObjects ,Color[][] targetPixels) {
        for (int i = 0; i < renderableObjects.size(); i++) {
            Object object = renderableObjects.get(i);

            if (object instanceof Building) { //DRAW BUILDING
                Building building = (Building) object;
                Building.BuildingType bt = building.buildingType;

                //First draw stand
                Unit.UnitStand stand = building.buildingType.stand;
                if (stand != null) {
                    Rasterizer.setPixels((int)(building.xTilePos*32 + 16 - stand.width/2f), (int)(building.yTilePos*32 + 16 - stand.height/2f), stand.width, stand.height, stand.getStandPixels(building.teamColor), targetPixels);
                    Rasterizer.setPixels((int)(building.xTilePos*32 + 16 - bt.width/2f), (int)(building.yTilePos*32 + 16 - bt.height/2f - stand.height/2f), bt.width, bt.height, building.buildingType.getBuildingPixels(building.teamColor), targetPixels);
                } else {
                    Rasterizer.setPixels(building.xTilePos*32, building.yTilePos*32, bt.width, bt.height, building.buildingType.getBuildingPixels(building.teamColor), targetPixels);
                }
            } else if (object instanceof Unit) { //DRAW UNIT
                Unit unit = (Unit) object;
                Unit.UnitType ut = unit.unitType;

                //First draw stand
                Unit.UnitStand stand = unit.unitType.stand;
                if (stand != null) {
                    Rasterizer.setPixels((int)(unit.x + 16 - stand.width/2f), (int)(unit.y + 16 - stand.height/2f), stand.width, stand.height, stand.getStandPixels(unit.teamColor), targetPixels);
                    Rasterizer.setPixels((int)(unit.x + 16 - ut.width/2f) + ut.soX, (int)(unit.y + 16 - ut.height/2f - stand.height/2f + ut.soY), ut.width, ut.height, unit.unitType.getUnitPixels(unit.teamColor), targetPixels);
                } else {
                    Rasterizer.setPixels((int)(unit.x + 16 - ut.width/2f) + ut.soX, (int)(unit.y + 16 - ut.height/2f), ut.width, ut.height, unit.unitType.getUnitPixels(unit.teamColor), targetPixels);
                }
            }
        }
    }

    public static void generateBuildingIcons(Color[][] resultPixels) {
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);

            int xIconOffset = 0;
            for (int iconID = 0; iconID < building.totalProducts; iconID++) {
                ProducingUnit pu = building.productionUnits[iconID];

                Color backgroundColorTint = iconID < building.productionsAvailableFromStart ? new Color(0, 255, 0, 85) : new Color(255, 0, 0, 85); //If upgraded from start
                Rasterizer.fillColorPixels(building.xTilePos*32 + xIconOffset, building.yTilePos*32 + 128 - pu.width, pu.width, pu.height, backgroundColorTint, resultPixels);
                Rasterizer.setPixels(building.xTilePos*32 + xIconOffset, building.yTilePos*32 + 128 - pu.height, pu.width, pu.height, pu.iconPixels, 1, resultPixels);

                xIconOffset += pu.width + 2;
            }
        }
    }

    public static void newProject(int tiledMapWidth, int tiledMapHeight) {
        renderImgWidth32 = tiledMapWidth;
        renderImgHeight32 = tiledMapHeight;

        renderImgWidth = renderImgWidth32 * 32;
        renderImgHeight = renderImgHeight32 * 32;

        grassLayer = new float[renderImgWidth][renderImgHeight];
        mudLayer = new float[renderImgWidth][renderImgHeight];
        desertLayer = new float[renderImgWidth][renderImgHeight];
        snowLayer = new float[renderImgWidth][renderImgHeight];

        MapManager.generateNewTiles(Level.renderImgWidth32, Level.renderImgHeight32);

        clearAll();

        Main.setUIVisible(true);
    }
}
