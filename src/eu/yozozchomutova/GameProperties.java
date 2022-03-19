package eu.yozozchomutova;

import java.util.Random;

public class GameProperties {
    //Blue
    public static int blueMoney;
    public static int blueResearch;

    public static int blueBombers;
    public static int blueMeteors;
    public static int blueCarrier;
    public static int blueTrojans;
    public static int blueFighters;
    public static boolean blueCannon;
    public static boolean blueAntiair;
    public static boolean bluePlasma;
    public static boolean blueRotary;
    public static boolean blueDefragmentator;

    public static boolean bluePVT;

    //Green
    public static int greenMoney;
    public static int greenResearch;

    public static int greenBombers;
    public static int greenMeteors;
    public static int greenCarrier;
    public static int greenTrojans;
    public static int greenFighters;
    public static boolean greenCannon;
    public static boolean greenAntiair;
    public static boolean greenPlasma;
    public static boolean greenRotary;
    public static boolean greenDefragmentator;

    public static boolean greenPVT;

    public static void GenerateProperties() {
        Random r = new Random();

        //Money
        blueMoney = r.nextInt(7000) + 3000;
        greenMoney = r.nextInt(7000) + 3000;

        //Research
        blueResearch = r.nextInt(250);
        greenResearch = r.nextInt(250);

        //Air forces
        blueBombers = r.nextInt(2);
        greenBombers = r.nextInt(2);
        blueMeteors = r.nextInt(1);
        greenMeteors = r.nextInt(1);
        blueCarrier = r.nextInt(1);
        greenCarrier = r.nextInt(1);
        blueTrojans = r.nextInt(2);
        greenTrojans = r.nextInt(2);
        blueFighters = r.nextInt(3);
        greenFighters = r.nextInt(3);

        //Towers
        blueCannon = true;
        greenCannon = true;
        blueAntiair = true;
        greenAntiair = true;
        bluePlasma = r.nextBoolean();
        greenPlasma = r.nextBoolean();
        blueRotary = r.nextBoolean();
        greenRotary = r.nextBoolean();
        blueDefragmentator = r.nextBoolean();
        greenDefragmentator = r.nextBoolean();

        //PVT
        bluePVT = true;
        greenPVT = true;
    }
}
