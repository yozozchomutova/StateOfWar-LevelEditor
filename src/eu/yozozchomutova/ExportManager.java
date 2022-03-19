package eu.yozozchomutova;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;

public class ExportManager {

    private static final byte[] EDT_HEADER = {(byte) 0x04, (byte) 0x00, (byte) 0x8e, (byte) 0x26};
    private static final byte[] EDT_ENDER = {(byte) 0xE8, (byte) 0x1D, (byte) 0x00, (byte) 0x00};

    private static final byte[] MAP_HEADER = {(byte) 0x04, (byte) 0x56, (byte) 0x45, (byte) 0x52, (byte) 0x37};
    private static final byte[] MAP_ENDER = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    private static final byte[] MAP_FALSE = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private static final byte[] MAP_FALSE2 = {(byte) 0x00, (byte) 0x00, (byte) 0x00};
    private static final byte[] MAP_TRUE = {(byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0x00};
    private static final byte[] MAP_TRUE2 = {(byte) 0x00, (byte) 0x00, (byte) 0x01};

    private static final byte[] MAP_BORDER_BYTES = {(byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte)0, (byte) 0, (byte)0};

    private static final byte[] TIL_ENDER = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public byte[] edtBytes;
    public byte[] mapBytes;
    public byte[] srfBytes;
    public byte[] tilBytes;
    public byte[] tmiBytes;

    public void exportAll(File edtFile, File mapFile, BufferedImage srfImage) throws IOException {
        exportEdt(edtFile);
        exportMap(mapFile);
        exportSrf(srfImage);
        exportTmiTil();
    }

    public void exportEdt(File file) throws IOException {
        byte[] origBytes = Files.readAllBytes(file.toPath());

        //Concat
        ArrayList<Byte> byteList = new ArrayList<>();

        writeToList(byteList, EDT_HEADER); //Header
        writeToList(byteList, new byte[]{(byte) 0x00, 0x00, 0x00, 0x00}); //Map index

        //Game properties
        edtWriteGameProperty(byteList, GameProperties.blueMoney, GameProperties.greenMoney);
        edtWriteGameProperty(byteList, GameProperties.blueBombers, GameProperties.greenBombers);
        edtWriteGameProperty(byteList, GameProperties.blueMeteors, GameProperties.greenMeteors);
        edtWriteGameProperty(byteList, GameProperties.blueCarrier, GameProperties.greenCarrier);
        edtWriteGameProperty(byteList, GameProperties.greenTrojans, GameProperties.greenTrojans);
        edtWriteGameProperty(byteList, GameProperties.greenFighters, GameProperties.greenFighters);

        writeToList(byteList, new byte[12]); // Unknown bytes

        //Blue turrets
        writeToList(byteList, new byte[]{(byte) (GameProperties.blueCannon ? 0x01 : 0x00)}); // Cannon
        writeToList(byteList, new byte[]{(byte) (GameProperties.blueAntiair ? 0x01 : 0x00)}); // Antiair
        writeToList(byteList, new byte[]{(byte) (GameProperties.bluePlasma ? 0x01 : 0x00)}); // Plasma
        writeToList(byteList, new byte[]{(byte) (GameProperties.blueRotary ? 0x01 : 0x00)}); // Rotary
        writeToList(byteList, new byte[]{(byte) (GameProperties.blueDefragmentator ? 0x01 : 0x00)}); // Defragmentator
        writeToList(byteList, new byte[5]); // Unknown bytes

        //Green turrets
        writeToList(byteList, new byte[]{(byte) (GameProperties.greenCannon ? 0x01 : 0x00)}); // Cannon
        writeToList(byteList, new byte[]{(byte) (GameProperties.greenAntiair ? 0x01 : 0x00)}); // Antiair
        writeToList(byteList, new byte[]{(byte) (GameProperties.greenPlasma ? 0x01 : 0x00)}); // Plasma
        writeToList(byteList, new byte[]{(byte) (GameProperties.blueRotary ? 0x01 : 0x00)}); // Rotary
        writeToList(byteList, new byte[]{(byte) (GameProperties.greenDefragmentator ? 0x01 : 0x00)}); // Defragmentator
        writeToList(byteList, new byte[17]); // Unknown bytes

        //Damage maybe?
        edtWriteGameProperty(byteList, 20, 20);

        edtWriteGameProperty(byteList, GameProperties.bluePVT ? 0 : 1, GameProperties.greenPVT ? 0 : 1);
        edtWriteGameProperty(byteList, GameProperties.blueResearch, GameProperties.greenResearch);

        writeToList(byteList, new byte[216]); // Unknown bytes
        //TODO Timer

        //Buildings
        for (int i = 0; i < Level.buildings.size(); i++) {
            Building b = Level.buildings.get(i);

            writeToList(byteList, FileManager.toInt(123, FileManager.Order.LITTLE_ENDIAN)); // Building unit
            writeToList(byteList, new byte[]{(byte) b.buildingType.id, 0, 0, 0}); // Type
            writeToList(byteList, FileManager.toInt(b.productionsAvailableFromStart, FileManager.Order.LITTLE_ENDIAN)); // Production available from start
            writeToList(byteList, FileManager.toInt(b.productionUnits[0] != null ? b.productionUnits[0].id : 0, FileManager.Order.LITTLE_ENDIAN)); // 1. Production unit
            writeToList(byteList, FileManager.toInt(b.productionUnits[1] != null ? b.productionUnits[1].id : 0, FileManager.Order.LITTLE_ENDIAN)); // 2. Production unit
            writeToList(byteList, FileManager.toInt(b.productionUnits[2] != null ? b.productionUnits[2].id : 0, FileManager.Order.LITTLE_ENDIAN)); // 3. Production unit
            writeToList(byteList, FileManager.toInt(b.productionUnits[3] != null ? b.productionUnits[3].id : 0, FileManager.Order.LITTLE_ENDIAN)); // 4. Production unit
            writeToList(byteList, FileManager.toInt(b.productionUnits[4] != null ? b.productionUnits[4].id : 0, FileManager.Order.LITTLE_ENDIAN)); // 5. Production unit
            writeToList(byteList, FileManager.toInt(b.productionUnitUpgradeItems[0], FileManager.Order.LITTLE_ENDIAN)); // 1. Production unit Upgrade count
            writeToList(byteList, FileManager.toInt(b.productionUnitUpgradeItems[1], FileManager.Order.LITTLE_ENDIAN)); // 2. Production unit Upgrade count
            writeToList(byteList, FileManager.toInt(b.productionUnitUpgradeItems[2], FileManager.Order.LITTLE_ENDIAN)); // 3. Production unit Upgrade count
            writeToList(byteList, FileManager.toInt(b.productionUnitUpgradeItems[3], FileManager.Order.LITTLE_ENDIAN)); // 4. Production unit Upgrade count
            writeToList(byteList, FileManager.toInt(b.productionUnitUpgradeItems[4], FileManager.Order.LITTLE_ENDIAN)); // 5. Production unit Upgrade count
            writeToList(byteList, FileManager.toInt(b.HP, FileManager.Order.LITTLE_ENDIAN)); // HP
            writeToList(byteList, FileManager.toInt(b.teamColor.colorID, FileManager.Order.LITTLE_ENDIAN)); // Team color
            writeToList(byteList, FileManager.toInt(b.hasSatelliteProtection ? 1 : 0, FileManager.Order.LITTLE_ENDIAN)); // Satellite protection
            writeToList(byteList, FileManager.toInt(b.xTilePos, FileManager.Order.LITTLE_ENDIAN)); // tile X
            writeToList(byteList, FileManager.toInt(b.yTilePos, FileManager.Order.LITTLE_ENDIAN)); // tile Y
            writeToList(byteList, FileManager.toInt(b.hpPercentage, FileManager.Order.LITTLE_ENDIAN)); // HP (Percentage)
        }

        //Return
        writeToList(byteList, EDT_ENDER);
        edtBytes = byteListToArray(byteList);
    }

    public void exportMap(File file) throws IOException {
        //Concat
        ArrayList<Byte> byteList = new ArrayList<>();

        writeToList(byteList, MAP_HEADER); //Header
        writeToList(byteList, FileManager.toShort((short) 0, FileManager.Order.LITTLE_ENDIAN)); //Screen vision X
        writeToList(byteList, FileManager.toShort((short) 0, FileManager.Order.LITTLE_ENDIAN)); //Offset
        writeToList(byteList, FileManager.toShort((short) 0, FileManager.Order.LITTLE_ENDIAN)); //Screen vision Y
        writeToList(byteList, FileManager.toShort((short) 0, FileManager.Order.LITTLE_ENDIAN)); //Offset

        writeToList(byteList, FileManager.toInt(Level.renderImgWidth32, FileManager.Order.LITTLE_ENDIAN)); //Map width
        writeToList(byteList, FileManager.toInt(Level.renderImgHeight32, FileManager.Order.LITTLE_ENDIAN)); //Map height

        //Main data
        MapManager.Tile[][] tiles = MapManager.tiles;
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                writeToList(byteList, new byte[]{(byte) y, (byte) x}); //Position

                if (x == 0 || y == 0 || y == tiles[0].length-1 || x == tiles.length-1) { // Check if it's border
                    writeToList(byteList, MAP_BORDER_BYTES);
                } else {
                    MapManager.Tile tile = tiles[x][y];

                    writeToList(byteList, tile.groundForbidden ? MAP_TRUE : MAP_FALSE); //Ground units can't pass
                    writeToList(byteList, tile.airForbidden ? MAP_TRUE : MAP_FALSE); //Air units can't pass
                    writeToList(byteList, tile.buildingsForbidden ? MAP_TRUE2 : MAP_FALSE2); //Buildings units can't pass
                    writeToList(byteList, new byte[4]); //Ending bytes
                }
            }
        }

        //Return
        writeToList(byteList, MAP_ENDER);
        mapBytes = byteListToArray(byteList);
    }

    public void exportSrf(BufferedImage bufferedImage) throws IOException {
        byte[] srfHeaderBytes = Files.readAllBytes(new File("src/srfHeader.file").toPath());

        //Fetch copy + paste bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        //Concat
        byte[] allByteArray = new byte[srfHeaderBytes.length + byteArrayOutputStream.size()];

        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(srfHeaderBytes);
        buff.put(byteArrayOutputStream.toByteArray());

        //Return
        srfBytes = buff.array();
    }

    public void exportTmiTil() throws IOException {
        //TMI
        ArrayList<Byte> tmiByteList = new ArrayList<>();

        //Header
        writeToList(tmiByteList, FileManager.toShort((short) Level.renderImgWidth32, FileManager.Order.LITTLE_ENDIAN));
        writeToList(tmiByteList, FileManager.toShort((short) Level.renderImgHeight32, FileManager.Order.LITTLE_ENDIAN));

        //Data
        for (int y = 0; y < Level.renderImgHeight32; y++) {
            for (int x = 0; x < Level.renderImgWidth32; x++) {
                writeToList(tmiByteList, FileManager.toShort((short) 0, FileManager.Order.LITTLE_ENDIAN));
            }
        }

        tmiBytes = byteListToArray(tmiByteList);

        //TIL
        byte[] tilHeaderBytes = Files.readAllBytes(new File("src/tilHeader.file").toPath());
        ArrayList<Byte> tilByteList = new ArrayList<>();

        writeToList(tilByteList, tilHeaderBytes);
        writeToList(tilByteList, TIL_ENDER);

        tilBytes = byteListToArray(tilByteList);
    }

    private static void writeToList(ArrayList<Byte> byteList, byte[] bytes) {
        for (Byte byte_ : bytes) {
            byteList.add(byte_);
        }
    }

    private static byte[] byteListToArray(ArrayList<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];

        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }

        return bytes;
    }

    private static void edtWriteGameProperty(ArrayList<Byte> byteList, int blueValue, int greenValue) {
        writeToList(byteList, FileManager.toInt(blueValue, FileManager.Order.LITTLE_ENDIAN));
        writeToList(byteList, FileManager.toInt(greenValue, FileManager.Order.LITTLE_ENDIAN));
        writeToList(byteList, new byte[]{0x00, 0x00, 0x00, 0x00});
    }
}
