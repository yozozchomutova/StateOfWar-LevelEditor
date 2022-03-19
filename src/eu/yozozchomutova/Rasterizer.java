package eu.yozozchomutova;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Rasterizer {

    public static void renderToImage(BufferedImage targetBI, Color[][] pixels) {
        //Convert color pixels to bufferImage
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                targetBI.setRGB(x, y, pixels[x][y].getRGB());
            }
        }
    }

    public static void fillBackground(Color[][] targetPixels, BufferedImage backgroundSource) {
        int bcgWidth = backgroundSource.getWidth();
        int bcgHeight = backgroundSource.getHeight();
        int[] bcgPixels = backgroundSource.getRGB(0, 0, bcgWidth, bcgHeight, null, 0, bcgWidth);

        //Background
        for (int y = 0; y < targetPixels[0].length; y++) {
            for (int x = 0; x < targetPixels.length; x++) {
                targetPixels[x][y] = new Color(bcgPixels[(x % bcgWidth) + (y % bcgHeight) * bcgWidth]);
            }
        }
    }

    public static int[] colorArrayToIntArray(Color[][] pixels) {
        int[] result = new int[pixels.length * pixels[0].length];

        for (int y = 0; y < pixels[0].length; y++) {
            for (int x = 0; x < pixels.length; x++) {
                result[y * pixels.length + x] = pixels[x][y].getRGB();
            }
        }

        return result;
    }

    public static BufferedImage createBI(int[] pixels, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bi.setRGB(0, 0, width, height, pixels, 0, width);
        return bi;
    }

    public static BufferedImage generateSchemeJpg(BufferedImage srfImage, BufferedImage levelPropertiesImage) {
        BufferedImage resultBI = new BufferedImage(srfImage.getWidth() + levelPropertiesImage.getWidth(), srfImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultBI.createGraphics();

        g.drawImage(srfImage, null, 0, 0);
        g.drawImage(levelPropertiesImage, null, srfImage.getWidth(), 0);

        g.dispose();
        return resultBI;
    }

    public static void WriteTextToRightTab(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setPaint(color);
        g2d.drawString(text, x, y);
    }

    public static void DrawGamePropertyToRightTab(Graphics2D g2d, int x, int y, boolean condition, String imagePath) {
        ImageIcon img = new ImageIcon(imagePath + (condition ? "On" : "Off") + ".png");
        g2d.drawImage(img.getImage(), x, y, img.getIconWidth(), img.getIconHeight(), null);
    }

    public static void DrawGamePropertyToRightTab(Graphics2D g2d, int x, int y, String imagePath) {
        ImageIcon img = new ImageIcon(imagePath + ".png");
        g2d.drawImage(img.getImage(), x, y, img.getIconWidth(), img.getIconHeight(), null);
    }

    public static void setPixels(int x, int y, EdgeGenerator.Piece piece, Color[][] destination) {
        setPixels(x, y, piece.width, piece.height, piece.pixels, destination);
    }

    public static void setPixels(int x, int y, BufferedImage bi, Color[][] destination) {
        int width = bi.getWidth();
        int height = bi.getHeight();

        int[] pixels = bi.getRGB(0, 0, width, height, null, 0, width);

        setPixels(x, y, width, height, pixels, 1f, destination);
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

    public static void copyPixels(Color[][] source, Color[][] destination) {
        for (int y = 0; y < source[0].length; y++) {
            for (int x = 0; x < source.length; x++) {
                destination[x][y] = source[x][y];
            }
        }
    }

    public static void applyBlackWhiteFilter(BufferedImage source, BufferedImage destination, int lighting) {
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                Color color = new Color(source.getRGB(x, y), true);

                int grayColor = (int)((color.getRed() + color.getGreen() + color.getBlue()) / 3f);
                grayColor = Math.min(grayColor + lighting, 255);
                int newColor = new Color(grayColor, grayColor, grayColor, color.getAlpha()).getRGB();

                destination.setRGB(x, y, newColor);
            }
        }
    }
}
