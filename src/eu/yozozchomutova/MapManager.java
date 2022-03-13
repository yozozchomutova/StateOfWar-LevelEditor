package eu.yozozchomutova;

public class MapManager {

    public static Tile[][] tiles;

    public static void generateNewTiles(int width, int height) {
        tiles = new Tile[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x][y] = new Tile();
            }
        }
    }

    public static class Tile {
        public boolean placedBuilding;
        public boolean groundForbidden;
        public boolean airForbidden;
        public boolean buildingsForbidden;

        public boolean tileReplaced;

        public Building.TeamColor team = Building.TeamColor.WHITE;

        public void forbidGround() {
            groundForbidden = true;
        }

        public void forbidAir() {
            airForbidden = true;
        }

        public void forbidBuildings() {
            buildingsForbidden = true;
        }

        public void placeBuilding() {
            placedBuilding = true;
        }
    }
}
