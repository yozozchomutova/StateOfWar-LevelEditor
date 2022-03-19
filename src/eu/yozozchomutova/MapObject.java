package eu.yozozchomutova;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static eu.yozozchomutova.MapManager.tiles;

public class MapObject {

    public MapObjectType mapObjectType;

    public int x, y;

    public MapObject(MapObjectType mapObjectType, int x, int y) {
        this.mapObjectType = mapObjectType;

        this.x = x;
        this.y = y;

        //Tile replaced
        for (int i = 0; i < mapObjectType.image.getHeight()/32f; i++) {
            for (int j = 0; j < mapObjectType.image.getWidth()/32f; j++) {
                try {
                    tiles[x/32 + j][y/32 + i].tileReplaced = true;
                    tiles[x/32 + j][y/32 + i].forbidGround();
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }
    }

    public enum BiomeCategory {
        SNOW("snow"),
        ;

        public String subFolder;

        public MapObjectType UP_RightSide;
        public MapObjectType UP_LeftSide;
        public MapObjectType LEFT_UpSide;
        public MapObjectType LEFT_DownSide;
        public MapObjectType RIGHT_UpSide;
        public MapObjectType RIGHT_DownSide;
        public MapObjectType DOWN_RightSide;
        public MapObjectType DOWN_LeftSide;

        BiomeCategory(String subFolder) {
            this.subFolder = subFolder;

            UP_RightSide = new MapObjectType(subFolder, "right");
            DOWN_RightSide = new MapObjectType(subFolder, "right");

            UP_LeftSide = new MapObjectType(subFolder, "left");
            DOWN_LeftSide = new MapObjectType(subFolder, "left");

            LEFT_UpSide = new MapObjectType(subFolder, "top");
            RIGHT_UpSide = new MapObjectType(subFolder, "top");

            LEFT_DownSide = new MapObjectType(subFolder, "bottom");
            RIGHT_DownSide = new MapObjectType(subFolder, "bottom");
        }
    }

    public static class MapObjectType {

        public BufferedImage image;

        public MapObjectType(String subFolder, String fileName) {
            try {
                image = ImageIO.read(new File("src/edges/" + subFolder + "/" + fileName + ".png"));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
