package eu.yozozchomutova;

import java.awt.*;
import java.util.ArrayList;

public class PathFiller {

    private static boolean[][] extendedPoints;
    private static ArrayList<Point> extendingPoints;

    public static boolean[][] startPathing(int startX, int startY) {
        MapManager.Tile[][] tiles = MapManager.tiles;

        extendedPoints = new boolean[Main.renderImgWidth32][Main.renderImgHeight32];
        extendingPoints = new ArrayList<>();

        extendedPoints[startX][startY] = true;
        extendingPoints.add(new Point(startX, startY));

        while (!extendingPoints.isEmpty()) {
            for (int i = extendingPoints.size()-1; i >= 0; i--) {
                Point point = extendingPoints.get(i);
                extendingPoints.remove(i);

                //Up
                if (point.y != 0) {
                    expand(tiles, point.x, point.y, 0, -1);
                }

                //Down
                if (point.y != Main.renderImgHeight32-1) {
                    expand(tiles, point.x, point.y, 0, 1);
                }

                //Right
                if (point.x != Main.renderImgWidth32-1) {
                    expand(tiles, point.x, point.y, 1, 0);
                }

                //Left
                if (point.x != 0) {
                    expand(tiles, point.x, point.y, -1, 0);
                }
            }
        }

        return extendedPoints;
    }

    private static void expand(MapManager.Tile[][] tiles, int x, int y, int expandX, int expandY) {
        if (!tiles[x+expandX][y+expandY].groundForbidden && !extendedPoints[x+expandX][y+expandY]) {
            extendedPoints[x+expandX][y+expandY] = true;
            extendingPoints.add(new Point(x+expandX, y+expandY));
        }
    }
}
