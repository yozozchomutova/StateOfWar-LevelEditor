package eu.yozozchomutova;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class ImportManager {

    public static void importAll(File edtFile, File mapFile, File srfFile) throws IOException {
        if (mapFile.exists()) importMap(mapFile);
        if (srfFile.exists()) importSrf(srfFile);
        if (edtFile.exists()) importEdt(edtFile);

        //Update UI / Render
        Level.rasterize();
    }

    public static void importSrf(File srfFile) throws IOException {
        byte[] srfBytes = Files.readAllBytes(srfFile.toPath());

        //Convert to list
        ArrayList<Byte> srfByteList = new ArrayList<>();
        for (int i = 0; i < srfBytes.length; i++) {
            srfByteList.add(srfBytes[i]);
        }

        byte off189 = srfBytes[189];
        byte off190 = srfBytes[190];

        if (off189 == (byte) 0xFF && off190 == (byte) 0xD8) {
            srfByteList.subList(0, 189).clear(); //Remove first 189 bytes
        } else {
            srfByteList.subList(0, 55).clear(); //Remove first 55 bytes

            //Add .jpg signiture
            srfByteList.add(0, (byte) 0xD8);
            srfByteList.add(0, (byte) 0xFF);
        }

        //Convert back to array
        srfBytes = new byte[srfByteList.size()];
        for (int i = 0; i < srfBytes.length; i++) {
            srfBytes[i] = srfByteList.get(i);
        }

        //Replace and done!
        InputStream srfIS = new ByteArrayInputStream(srfBytes);

        Level.backgroundLayer = ImageIO.read(srfIS);
    }

    public static void importMap(File mapFile) throws IOException {
        byte[] mapBytes = Files.readAllBytes(mapFile.toPath());
        FileManager fm = new FileManager(0, mapBytes);

        fm.position += 5; //Header
        fm.position += 2; //Screen vision X
        fm.position += 2; //Offset
        fm.position += 2; //Screen vision Y
        fm.position += 2; //Offset

        int mapWidth = fm.readInt();
        int mapHeight = fm.readInt();

        Level.newProject(mapWidth, mapHeight);

        //Main data
        MapManager.Tile[][] tiles = MapManager.tiles;
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                int tileY = fm.readByte();
                int tileX = fm.readByte();

                MapManager.Tile tile = tiles[tileX][tileY];
                tile.groundForbidden = fm.readInt() != 0;
                tile.airForbidden = fm.readInt() != 0;

                fm.position += 2;
                tile.buildingsForbidden = fm.readByte() == 0x01;
                fm.position += 4; //Ending bytes
            }
        }
    }

    public static void importEdt(File edtFile) throws IOException {
        byte[] edtBytes = Files.readAllBytes(edtFile.toPath());
        FileManager fm = new FileManager(0, edtBytes);

        fm.readInt(); //Header
        fm.readInt(); //Map index

        GameProperties.blueMoney = fm.readInt();
        GameProperties.greenMoney = fm.readInt();
        fm.readInt();
        GameProperties.blueBombers = fm.readInt();
        GameProperties.greenBombers = fm.readInt();
        fm.readInt();
        GameProperties.blueMeteors = fm.readInt();
        GameProperties.greenMeteors = fm.readInt();
        fm.readInt();
        GameProperties.blueCarrier = fm.readInt();
        GameProperties.greenCarrier = fm.readInt();
        fm.readInt();
        GameProperties.blueTrojans = fm.readInt();
        GameProperties.greenTrojans = fm.readInt();
        fm.readInt();
        GameProperties.blueFighters = fm.readInt();
        GameProperties.greenFighters = fm.readInt();
        fm.position += 16; //??

        GameProperties.blueCannon = fm.readByte() == (byte) 0x01;
        GameProperties.blueAntiair = fm.readByte() == (byte) 0x01;
        GameProperties.bluePlasma = fm.readByte() == (byte) 0x01;
        GameProperties.blueRotary = fm.readByte() == (byte) 0x01;
        GameProperties.blueDefragmentator = fm.readByte() == (byte) 0x01;
        fm.position += 5; //??
        GameProperties.greenCannon = fm.readByte() == (byte) 0x01;
        GameProperties.greenAntiair = fm.readByte() == (byte) 0x01;
        GameProperties.greenPlasma = fm.readByte() == (byte) 0x01;
        GameProperties.greenRotary = fm.readByte() == (byte) 0x01;
        GameProperties.greenDefragmentator = fm.readByte() == (byte) 0x01;
        fm.position += 25; //??

        GameProperties.bluePVT = fm.readInt() == 1;
        GameProperties.greenPVT = fm.readInt() == 1;
        fm.position += 25; //??

        GameProperties.blueResearch = fm.readInt();
        GameProperties.greenResearch = fm.readInt();
        fm.position += 203; //??

        while (true) {
            int signature = fm.readInt();

            if (signature == 7656) {//End
                break;
            } else if (signature == 123) { //Building
                byte buildingType = (byte) fm.readInt();
                byte productionsAvailable = (byte) fm.readInt();

                ProducingUnit[] producingUnits = new ProducingUnit[5];
                producingUnits[0] = ProducingUnit.getPUnitType((byte) fm.readInt());
                producingUnits[1] = ProducingUnit.getPUnitType((byte) fm.readInt());
                producingUnits[2] = ProducingUnit.getPUnitType((byte) fm.readInt());
                producingUnits[3] = ProducingUnit.getPUnitType((byte) fm.readInt());
                producingUnits[4] = ProducingUnit.getPUnitType((byte) fm.readInt());

                int[] productionUnitUpgradeItems = new int[5];
                productionUnitUpgradeItems[0] = fm.readInt();
                productionUnitUpgradeItems[1] = fm.readInt();
                productionUnitUpgradeItems[2] = fm.readInt();
                productionUnitUpgradeItems[3] = fm.readInt();
                productionUnitUpgradeItems[4] = fm.readInt();

                int hp = fm.readInt();
                byte teamID = (byte) fm.readInt();
                boolean satelliteProtection = fm.readInt() == 1;

                int x = fm.readInt();
                int y = fm.readInt();
                int hpPercentage = fm.readInt();

                Level.buildings.add(new Building(buildingType, teamID, productionsAvailable, producingUnits, productionUnitUpgradeItems, hpPercentage, satelliteProtection, x, y));
            } else if (signature == 224) { //Unit
                byte unitType = (byte) fm.readInt();
                byte teamID = (byte) fm.readInt();
                int x = fm.readInt();
                int y = fm.readInt();

                Level.units.add(new Unit(unitType, teamID, x, y));
            } else {
                System.out.println("Unknown signature: " + signature);
            }
        }
    }
}
