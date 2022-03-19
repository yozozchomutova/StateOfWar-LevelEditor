package eu.yozozchomutova;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class EdgeGenerator {

    public static void generate(/*Color[][] resultPixels*/) {
        MapManager.Tile[][] tiles = MapManager.tiles;

        for (int y = 1; y < tiles[0].length-1; y++) {
            for (int x = 1; x < tiles.length-1; x++) {
                if (!tiles[x][y].tileReplaced) {
                    //TEMPLATE
                    /*if (    tiles[x-1][y-1].groundForbidden && tiles[x  ][y-1].groundForbidden && tiles[x+1][y-1].groundForbidden &&
                            tiles[x-1][y  ].groundForbidden && tiles[x  ][y  ].groundForbidden && tiles[x+1][y  ].groundForbidden &&
                            tiles[x-1][y+1].groundForbidden && tiles[x  ][y+1].groundForbidden && tiles[x+1][y+1].groundForbidden) {
                        DrawIntoPixels(x, y, tiles, resultPixels, Piece.RIGHT_UpSide);
                    }*/

                    if (    !tiles[x-1][y-1].groundForbidden && !tiles[x  ][y-1].groundForbidden && !tiles[x+1][y-1].groundForbidden &&
                            tiles[x-1][y  ].groundForbidden && tiles[x  ][y  ].groundForbidden && tiles[x+1][y  ].groundForbidden &&
                            tiles[x-1][y+1].groundForbidden && tiles[x  ][y+1].groundForbidden && tiles[x+1][y+1].groundForbidden) {
                        Level.mapObjects.add(new MapObject(MapObject.BiomeCategory.SNOW.RIGHT_UpSide, x*32, y*32));
                        //DrawIntoPixels(x, y, tiles, resultPixels, Piece.RIGHT_UpSide);
                    } else if (    tiles[x-1][y-1].groundForbidden && tiles[x  ][y-1].groundForbidden && tiles[x+1][y-1].groundForbidden &&
                            tiles[x-1][y  ].groundForbidden && tiles[x  ][y  ].groundForbidden && tiles[x+1][y  ].groundForbidden &&
                            !tiles[x-1][y+1].groundForbidden && !tiles[x  ][y+1].groundForbidden && !tiles[x+1][y+1].groundForbidden) {
                        Level.mapObjects.add(new MapObject(MapObject.BiomeCategory.SNOW.RIGHT_DownSide, x*32, y*32));
                        //DrawIntoPixels(x, y, tiles, resultPixels, Piece.RIGHT_DownSide);
                    } else if (    tiles[x-1][y-1].groundForbidden && tiles[x  ][y-1].groundForbidden && !tiles[x+1][y-1].groundForbidden &&
                            tiles[x-1][y  ].groundForbidden && tiles[x  ][y  ].groundForbidden && !tiles[x+1][y  ].groundForbidden &&
                            tiles[x-1][y+1].groundForbidden && tiles[x  ][y+1].groundForbidden && !tiles[x+1][y+1].groundForbidden) {
                        Level.mapObjects.add(new MapObject(MapObject.BiomeCategory.SNOW.UP_RightSide, x*32, y*32));
                        //DrawIntoPixels(x, y, tiles, resultPixels, Piece.UP_RightSide);
                    } else if (    !tiles[x-1][y-1].groundForbidden && tiles[x  ][y-1].groundForbidden && tiles[x+1][y-1].groundForbidden &&
                            !tiles[x-1][y  ].groundForbidden && tiles[x  ][y  ].groundForbidden && tiles[x+1][y  ].groundForbidden &&
                            !tiles[x-1][y+1].groundForbidden && tiles[x  ][y+1].groundForbidden && tiles[x+1][y+1].groundForbidden) {
                        Level.mapObjects.add(new MapObject(MapObject.BiomeCategory.SNOW.UP_LeftSide, x*32, y*32));
                        //DrawIntoPixels(x, y, tiles, resultPixels, Piece.UP_LeftSide);
                    }
                }
            }
        }
    }

    private void DrawIntoPixels(int x, int y, MapManager.Tile[][] tiles, Color[][] resultPixels, Piece piece) {
        //Pixels
        for (int i = 0; i < piece.height; i++) {
            for (int j = 0; j < piece.width; j++) {
                try {
                    int pixel = piece.pixels[j + i * piece.width];

                    if (pixel != 0)
                        resultPixels[x * 32 + j][y * 32 + i] = new Color(pixel);
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }

        //Tile replaced
        for (int i = 0; i < piece.height/32f; i++) {
            for (int j = 0; j < piece.width/32f; j++) {
                try {
                    tiles[x + j][y + i].tileReplaced = true;
                    tiles[x + j][y + i].forbidGround();
                } catch (ArrayIndexOutOfBoundsException ignore) {}
            }
        }
    }

    // EXPERIMENT 2
    public enum Piece {
        //Start
        START_TO_UP_RightSide(0, 96),
        START_TO_UP_LeftSide(0, 96),
        START_TO_LEFT_UpSide(-96, 0),
        START_TO_LEFT_DownSide(-156, 0),
        START_TO_RIGHT_UpSide(96, 0),
        START_TO_RIGHT_DownSide(156, 0),
        START_TO_DOWN_RightSide(0, -96),
        START_TO_DOWN_LeftSide(0, -96),

        //Straight
        UP_RightSide(0, 170),
        UP_LeftSide(0, 170),
        LEFT_UpSide(-126, 0),
        LEFT_DownSide(-156, 0),
        RIGHT_UpSide(126, 0),
        RIGHT_DownSide(156, 0),
        DOWN_RightSide(0, -170),
        DOWN_LeftSide(0, -170),

        //Turn - 45 degrees | format -> *over* _TO_ *direction*
        UP_LEFT_RightUpSide(-96, 96),
        UP_LEFT_LeftDownSide(-96, 96),
        UP_RIGHT_RightDownSide(96, 96),
        UP_RIGHT_LeftUpSide(96, 96),

        RIGHT_UP_UpLeftSide(96, 96),
        RIGHT_UP_DownRightSide(96, 96),
        RIGHT_DOWN_UpRightSide(96, -96),
        RIGHT_DOWN_DownLeftSide(96, -96),

        DOWN_RIGHT_RightSide(96, -96),
        DOWN_RIGHT_LeftSide(96, -96),
        DOWN_LEFT_RightSide(-96, -96),
        DOWN_LEFT_LeftSide(-96, -96),

        LEFT_UP_UpSide(-96, 96),
        LEFT_UP_DownSide(-96, 96),
        LEFT_DOWN_UpSide(-96, -96),
        LEFT_DOWN_DownSide(-96, -96),

        //Turn - 90 degrees | format -> *direction* _TO_ *direction*
        UP_TO_LEFT_RightSide(-167, 0),
        UP_TO_LEFT_LeftSide(-96, 0),
        UP_TO_RIGHT_RightSide(96, 0),
        UP_TO_RIGHT_LeftSide(167, 0),

        RIGHT_TO_UP_UpSide(0, 96),
        RIGHT_TO_UP_DownSide(0, 199),
        RIGHT_TO_DOWN_UpSide(0, -96),
        RIGHT_TO_DOWN_DownSide(0, -96),

        DOWN_TO_RIGHT_RightSide(96, 0),
        DOWN_TO_RIGHT_LeftSide(96, 0),
        DOWN_TO_LEFT_RightSide(-96, 0),
        DOWN_TO_LEFT_LeftSide(-96, 0),

        LEFT_TO_UP_UpSide(0, 96),
        LEFT_TO_UP_DownSide(0, 199),
        LEFT_TO_DOWN_UpSide(0, -96),
        LEFT_TO_DOWN_DownSide(0, -96),

        //End
        END_FROM_UP_RightSide(0, 0),
        END_FROM_UP_LeftSide(0, 0),
        END_FROM_LEFT_UpSide(0, 0),
        END_FROM_LEFT_DownSide(0, 0),
        END_FROM_RIGHT_UpSide(0, 0),
        END_FROM_RIGHT_DownSide(0, 0),
        END_FROM_DOWN_RightSide(0, 0),
        END_FROM_DOWN_LeftSide(0, 0),
        ;

        int[] pixels;
        int moveX, moveY;
        Piece[] nextPieces;
        Piece endingPiece;

        int width, height;

        static {
            START_TO_UP_RightSide.setValues(new Piece[]{UP_RightSide, UP_TO_RIGHT_RightSide, UP_TO_LEFT_RightSide}, END_FROM_DOWN_RightSide);
            START_TO_UP_LeftSide.setValues(new Piece[]{UP_LeftSide, UP_TO_RIGHT_LeftSide, UP_TO_LEFT_LeftSide}, END_FROM_DOWN_LeftSide);
            START_TO_LEFT_UpSide.setValues(new Piece[]{LEFT_UpSide, LEFT_TO_UP_UpSide, LEFT_TO_DOWN_UpSide}, END_FROM_RIGHT_UpSide);
            START_TO_LEFT_DownSide.setValues(new Piece[]{LEFT_DownSide, LEFT_TO_UP_DownSide, LEFT_TO_DOWN_DownSide}, END_FROM_RIGHT_DownSide);
            START_TO_RIGHT_UpSide.setValues(new Piece[]{RIGHT_UpSide, RIGHT_TO_UP_UpSide, RIGHT_TO_DOWN_UpSide}, END_FROM_LEFT_UpSide);
            START_TO_RIGHT_DownSide.setValues(new Piece[]{RIGHT_DownSide, RIGHT_TO_UP_DownSide, RIGHT_TO_DOWN_DownSide}, END_FROM_LEFT_DownSide);
            START_TO_DOWN_RightSide.setValues(new Piece[]{DOWN_RightSide, DOWN_TO_RIGHT_RightSide, DOWN_TO_LEFT_RightSide}, END_FROM_UP_RightSide);
            START_TO_DOWN_LeftSide.setValues(new Piece[]{DOWN_LeftSide, DOWN_TO_RIGHT_LeftSide, DOWN_TO_LEFT_LeftSide}, END_FROM_UP_LeftSide);

            UP_RightSide.setValues(new Piece[]{UP_RightSide, UP_TO_RIGHT_RightSide, UP_TO_LEFT_RightSide}, END_FROM_DOWN_RightSide);
            UP_LeftSide.setValues(new Piece[]{UP_LeftSide, UP_TO_RIGHT_LeftSide, UP_TO_LEFT_LeftSide}, END_FROM_DOWN_LeftSide);
            LEFT_UpSide.setValues(new Piece[]{LEFT_UpSide, LEFT_TO_UP_UpSide, LEFT_TO_DOWN_UpSide}, END_FROM_RIGHT_UpSide);
            LEFT_DownSide.setValues(new Piece[]{LEFT_DownSide, LEFT_TO_UP_DownSide, LEFT_TO_DOWN_DownSide}, END_FROM_RIGHT_DownSide);
            RIGHT_UpSide.setValues(new Piece[]{RIGHT_UpSide, RIGHT_TO_UP_UpSide, RIGHT_TO_DOWN_UpSide}, END_FROM_LEFT_UpSide);
            RIGHT_DownSide.setValues(new Piece[]{RIGHT_DownSide, RIGHT_TO_UP_DownSide, RIGHT_TO_DOWN_DownSide}, END_FROM_LEFT_DownSide);
            DOWN_RightSide.setValues(new Piece[]{DOWN_RightSide, DOWN_TO_RIGHT_RightSide, DOWN_TO_LEFT_RightSide}, END_FROM_UP_RightSide);
            DOWN_LeftSide.setValues(new Piece[]{DOWN_LeftSide, DOWN_TO_RIGHT_LeftSide, DOWN_TO_LEFT_LeftSide}, END_FROM_UP_LeftSide);

            //Turns - 45 degrees

            //Turns - 90 degrees
            RIGHT_TO_UP_DownSide.setValues(new Piece[]{UP_RightSide, UP_TO_RIGHT_RightSide, UP_TO_LEFT_RightSide}, END_FROM_DOWN_RightSide);
            LEFT_TO_UP_UpSide.setValues(new Piece[]{UP_RightSide, UP_TO_RIGHT_RightSide, UP_TO_LEFT_RightSide}, END_FROM_DOWN_RightSide);
            RIGHT_TO_UP_UpSide.setValues(new Piece[]{UP_LeftSide, UP_TO_RIGHT_LeftSide, UP_TO_LEFT_LeftSide}, END_FROM_DOWN_LeftSide);
            LEFT_TO_UP_DownSide.setValues(new Piece[]{UP_LeftSide, UP_TO_RIGHT_LeftSide, UP_TO_LEFT_LeftSide}, END_FROM_DOWN_LeftSide);

            UP_TO_RIGHT_RightSide.setValues(new Piece[]{RIGHT_DownSide, RIGHT_TO_UP_DownSide, RIGHT_TO_DOWN_DownSide}, END_FROM_LEFT_DownSide);
            DOWN_TO_RIGHT_LeftSide.setValues(new Piece[]{RIGHT_DownSide, RIGHT_TO_UP_DownSide, RIGHT_TO_DOWN_DownSide}, END_FROM_LEFT_DownSide);
            UP_TO_RIGHT_LeftSide.setValues(new Piece[]{RIGHT_UpSide, RIGHT_TO_UP_UpSide, RIGHT_TO_DOWN_UpSide}, END_FROM_LEFT_UpSide);
            DOWN_TO_RIGHT_RightSide.setValues(new Piece[]{RIGHT_UpSide, RIGHT_TO_UP_UpSide, RIGHT_TO_DOWN_UpSide}, END_FROM_LEFT_UpSide);

            RIGHT_TO_DOWN_UpSide.setValues(new Piece[]{DOWN_RightSide, DOWN_TO_RIGHT_RightSide, DOWN_TO_LEFT_RightSide}, END_FROM_UP_RightSide);
            LEFT_TO_DOWN_DownSide.setValues(new Piece[]{DOWN_RightSide, DOWN_TO_RIGHT_RightSide, DOWN_TO_LEFT_RightSide}, END_FROM_UP_RightSide);
            RIGHT_TO_DOWN_DownSide.setValues(new Piece[]{DOWN_LeftSide, DOWN_TO_RIGHT_LeftSide, DOWN_TO_LEFT_LeftSide}, END_FROM_UP_LeftSide);
            LEFT_TO_DOWN_UpSide.setValues(new Piece[]{DOWN_LeftSide, DOWN_TO_RIGHT_LeftSide, DOWN_TO_LEFT_LeftSide}, END_FROM_UP_LeftSide);

            UP_TO_LEFT_LeftSide.setValues(new Piece[]{LEFT_DownSide, LEFT_TO_UP_DownSide, LEFT_TO_DOWN_DownSide}, END_FROM_RIGHT_DownSide);
            DOWN_TO_LEFT_RightSide.setValues(new Piece[]{LEFT_DownSide, LEFT_TO_UP_DownSide, LEFT_TO_DOWN_DownSide}, END_FROM_RIGHT_DownSide);
            UP_TO_LEFT_RightSide.setValues(new Piece[]{LEFT_UpSide, LEFT_TO_UP_UpSide, LEFT_TO_DOWN_UpSide}, END_FROM_RIGHT_UpSide);
            DOWN_TO_LEFT_LeftSide.setValues(new Piece[]{LEFT_UpSide, LEFT_TO_UP_UpSide, LEFT_TO_DOWN_UpSide}, END_FROM_RIGHT_UpSide);
        }

        Piece(int moveX, int moveY) {
            this.moveX = moveX;
            this.moveY = moveY;
        }

        public void setValues(Piece[] pieces, Piece endingPiece) {
            this.nextPieces = pieces;
            this.endingPiece = endingPiece;
        }

        public void setBufferedImage(BufferedImage bi) {
            pixels = loadPixels(bi);
            width = bi.getWidth();
            height = bi.getHeight();
        }
    }

    public EdgeGenerator(String subFolder) {
        BufferedImage bottomEdge_BI, topEdge_BI, leftEdge_BI, rightEdge_BI,
                right_to_top_downSide_BI, left_to_top_downSide_BI,
                leftDownEnd_BI, rightDownEnd_BI,
                greenCircle_BI, redCircle_BI, blueCircle_BI, yellowCircle_BI;

        //Load
        try {
            bottomEdge_BI = ImageIO.read(new File("src/edges/" + subFolder + "bottom.png"));
            topEdge_BI = ImageIO.read(new File("src/edges/" + subFolder + "top.png"));
            leftEdge_BI = ImageIO.read(new File("src/edges/" + subFolder + "left.png"));
            rightEdge_BI = ImageIO.read(new File("src/edges/" + subFolder + "right.png"));

            right_to_top_downSide_BI = ImageIO.read(new File("src/edges/" + subFolder + "right_to_top_downSide.png"));
            left_to_top_downSide_BI = ImageIO.read(new File("src/edges/" + subFolder + "left_to_top_downSide.png"));

            leftDownEnd_BI = ImageIO.read(new File("src/edges/" + subFolder + "left_down_end.png"));
            rightDownEnd_BI = ImageIO.read(new File("src/edges/" + subFolder + "right_down_end.png"));

            greenCircle_BI = ImageIO.read(new File("src/edges/" + subFolder + "testDot.png"));
            redCircle_BI = ImageIO.read(new File("src/edges/" + subFolder + "testDotE.png"));
            blueCircle_BI = ImageIO.read(new File("src/edges/" + subFolder + "testDotB.png"));
            yellowCircle_BI = ImageIO.read(new File("src/edges/" + subFolder + "testDotY.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        //Load pixels
        //Start
        Piece.START_TO_UP_RightSide.setBufferedImage(redCircle_BI);
        Piece.START_TO_UP_LeftSide.setBufferedImage(redCircle_BI);
        Piece.START_TO_LEFT_UpSide.setBufferedImage(redCircle_BI);
        Piece.START_TO_LEFT_DownSide.setBufferedImage(leftDownEnd_BI);
        Piece.START_TO_RIGHT_UpSide.setBufferedImage(redCircle_BI);
        Piece.START_TO_RIGHT_DownSide.setBufferedImage(rightDownEnd_BI);
        Piece.START_TO_DOWN_RightSide.setBufferedImage(redCircle_BI);
        Piece.START_TO_DOWN_LeftSide.setBufferedImage(redCircle_BI);

        //Straight
        Piece.UP_RightSide.setBufferedImage(rightEdge_BI);
        Piece.UP_LeftSide.setBufferedImage(leftEdge_BI);
        Piece.LEFT_UpSide.setBufferedImage(topEdge_BI);
        Piece.LEFT_DownSide.setBufferedImage(bottomEdge_BI);
        Piece.DOWN_RightSide.setBufferedImage(rightEdge_BI);
        Piece.DOWN_LeftSide.setBufferedImage(leftEdge_BI);
        Piece.RIGHT_UpSide.setBufferedImage(topEdge_BI);
        Piece.RIGHT_DownSide.setBufferedImage(bottomEdge_BI);

        //Turns
        Piece.UP_TO_LEFT_RightSide.setBufferedImage(left_to_top_downSide_BI);
        Piece.UP_TO_LEFT_LeftSide.setBufferedImage(yellowCircle_BI);
        Piece.UP_TO_RIGHT_RightSide.setBufferedImage(yellowCircle_BI);
        Piece.UP_TO_RIGHT_LeftSide.setBufferedImage(right_to_top_downSide_BI);

        Piece.RIGHT_TO_UP_UpSide.setBufferedImage(yellowCircle_BI);
        Piece.RIGHT_TO_UP_DownSide.setBufferedImage(right_to_top_downSide_BI);
        Piece.RIGHT_TO_DOWN_UpSide.setBufferedImage(yellowCircle_BI);
        Piece.RIGHT_TO_DOWN_DownSide.setBufferedImage(yellowCircle_BI);

        Piece.DOWN_TO_RIGHT_RightSide.setBufferedImage(yellowCircle_BI);
        Piece.DOWN_TO_RIGHT_LeftSide.setBufferedImage(yellowCircle_BI);
        Piece.DOWN_TO_LEFT_RightSide.setBufferedImage(yellowCircle_BI);
        Piece.DOWN_TO_LEFT_LeftSide.setBufferedImage(yellowCircle_BI);

        Piece.LEFT_TO_UP_UpSide.setBufferedImage(yellowCircle_BI);
        Piece.LEFT_TO_UP_DownSide.setBufferedImage(left_to_top_downSide_BI);
        Piece.LEFT_TO_DOWN_UpSide.setBufferedImage(yellowCircle_BI);
        Piece.LEFT_TO_DOWN_DownSide.setBufferedImage(yellowCircle_BI);

        //End
        Piece.END_FROM_UP_RightSide.setBufferedImage(redCircle_BI);
        Piece.END_FROM_UP_LeftSide.setBufferedImage(redCircle_BI);
        Piece.END_FROM_LEFT_UpSide.setBufferedImage(redCircle_BI);
        Piece.END_FROM_LEFT_DownSide.setBufferedImage(leftDownEnd_BI);
        Piece.END_FROM_RIGHT_UpSide.setBufferedImage(redCircle_BI);
        Piece.END_FROM_RIGHT_DownSide.setBufferedImage(rightDownEnd_BI);
        Piece.END_FROM_DOWN_RightSide.setBufferedImage(redCircle_BI);
        Piece.END_FROM_DOWN_LeftSide.setBufferedImage(redCircle_BI);
    }

    /*public void Generate(Color[][] pixelColors) {
        int steps = Main.random.nextInt(10) + 5;
        //int steps = 2;

        int x = Main.random.nextInt(Main.renderImgWidth) - 32;
        int y = Main.random.nextInt(Main.renderImgHeight) - 32;

        //Start
        Piece[] startingPiecesSelection = {
            Piece.START_TO_UP_RightSide,
            Piece.START_TO_UP_LeftSide,
            Piece.START_TO_LEFT_UpSide,
            Piece.START_TO_LEFT_DownSide,
            Piece.START_TO_RIGHT_UpSide,
            Piece.START_TO_RIGHT_DownSide,
            Piece.START_TO_DOWN_RightSide,
            Piece.START_TO_DOWN_LeftSide,
        };

        int startingPieceIndex = Main.random.nextInt(startingPiecesSelection.length);
        Piece startingPiece = startingPiecesSelection[startingPieceIndex];

        Generator.setPixels(x, y, startingPiece, pixelColors);

        for (int i = 0; i < steps; i++) {
            x += startingPiece.moveX;
            y += startingPiece.moveY;

            int randomNextPiece = Main.random.nextInt(startingPiece.nextPieces.length);
            float straightPathChamce = Main.random.nextFloat();

            if (straightPathChamce < 0.75f) {
                randomNextPiece = 0;
            }

            startingPiece = startingPiece.nextPieces[randomNextPiece];

            Generator.setPixels(x, y, startingPiece, pixelColors);

            if (i == steps-1) {
                startingPiece = startingPiece.endingPiece;
            }
        }

        //Ending
        x += startingPiece.moveX;
        y += startingPiece.moveY;

        Generator.setPixels(x, y, startingPiece, pixelColors);
    }*/

    private static int[] loadPixels(BufferedImage bufferedImage) {
        int[] result = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                result[x + y * bufferedImage.getWidth()] = bufferedImage.getRGB(x, y);
            }
        }

        return result;
    }
}
