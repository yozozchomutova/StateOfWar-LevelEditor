package eu.yozozchomutova;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BuildingPack {

    public int[]    basePixels, FheavyPixels,   FlightPixels,   FmediumPixels,  FrobotPixels,         labPixels,  minePixels, radarPixels,    windPixels;
    public int      baseWidth,  FheavyWidth,    FlightWidth,    FmediumWidth,   FrobotWidth,    labWidth,   mineWidth,  radarWidth,     windWidth;
    public int      baseHeight,  FheavyHeight,    FlightHeight,    FmediumHeight,   FrobotHeight,    labHeight,   mineHeight,  radarHeight,     windHeight;

    public BuildingPack(String subFolder) {
        BufferedImage base_BI,
                Flight_BI, Fmedium_BI, Fheavy_BI, Frobot_BI,
                lab_BI, mine_BI, radar_BI, wind_BI;

        //Load
        try {
            base_BI = ImageIO.read(new File("src/buildings/" + subFolder + "base.png"));

            Flight_BI = ImageIO.read(new File("src/buildings/" + subFolder + "Flight.png"));
            Fmedium_BI = ImageIO.read(new File("src/buildings/" + subFolder + "Fmedium.png"));
            Fheavy_BI = ImageIO.read(new File("src/buildings/" + subFolder + "Fheavy.png"));
            Frobot_BI = ImageIO.read(new File("src/buildings/" + subFolder + "Frobot.png"));

            lab_BI = ImageIO.read(new File("src/buildings/" + subFolder + "lab.png"));
            mine_BI = ImageIO.read(new File("src/buildings/" + subFolder + "mine.png"));
            radar_BI = ImageIO.read(new File("src/buildings/" + subFolder + "radar.png"));
            wind_BI = ImageIO.read(new File("src/buildings/" + subFolder + "wind.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        basePixels = loadPixels(base_BI);
        baseWidth = base_BI.getWidth();
        baseHeight = base_BI.getHeight();

        FlightPixels = loadPixels(Flight_BI);
        FlightWidth = Flight_BI.getWidth();
        FlightHeight = Flight_BI.getHeight();

        FmediumPixels = loadPixels(Fmedium_BI);
        FmediumWidth = Fmedium_BI.getWidth();
        FmediumHeight = Fmedium_BI.getHeight();

        FheavyPixels = loadPixels(Fheavy_BI);
        FheavyWidth = Fheavy_BI.getWidth();
        FheavyHeight = Fheavy_BI.getHeight();

        FrobotPixels = loadPixels(Frobot_BI);
        FrobotWidth = Frobot_BI.getWidth();
        FrobotHeight = Frobot_BI.getHeight();

        labPixels = loadPixels(lab_BI);
        labWidth = lab_BI.getWidth();
        labHeight = lab_BI.getHeight();

        minePixels = loadPixels(mine_BI);
        mineWidth = mine_BI.getWidth();
        mineHeight = mine_BI.getHeight();

        radarPixels = loadPixels(radar_BI);
        radarWidth = radar_BI.getWidth();
        radarHeight = radar_BI.getHeight();

        windPixels = loadPixels(wind_BI);
        windWidth = wind_BI.getWidth();
        windHeight = wind_BI.getHeight();
    }

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
