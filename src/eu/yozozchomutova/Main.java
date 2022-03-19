package eu.yozozchomutova;

import eu.yozozchomutova.dialogwindow.*;
import eu.yozozchomutova.toolpanel.EditObjectToolPanel;
import eu.yozozchomutova.ui.ImageUI;
import eu.yozozchomutova.ui.WindowBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class Main extends JPanel implements MouseListener, KeyListener {

    public static final String VERSION = "4.0";

    public static Random random = new Random();

    public static JFrame frame;

    public static final String[] levelListStr = {
            "M01", "M02", "M03", "M04", "M05", "M06", "M07", "M08",
            "M09", "M10", "M11", "M12", "M13", "M14", "M15", "M16",
            "M17", "M18", "M19", "M20", "M21", "M22"
    };

    public static final int[] levelIndexes = {
            1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22
    };

    public static final String[] biomesStr = {
            "Grass", "Mud", "Desert", "Snow"
    };

    public static final String[] gameType = {
            "Classic+Warmonger"
    };

    //Top bar
    private static ImageUI settings, newLevel, importLevel, exportLevel, exportPng; // First sequence - File managing
    private static ImageUI surfacePaint, addEdges, addMapObjects; // Second sequence - Surface editing
    private static ImageUI levelProperties, addBuilding, addUnit; // Third sequence - Object editing
    private static ImageUI generateLevel, generateObjects; // Fourth sequence - Generating

    //Dialogs
    public static LevelPropertiesDLG levelPropertiesDLG;
    public static NewProjectDLG newProjectDLG;
    public static GenerateDLG generateDLG;
    public static ConfirmDLG confirmDLG;
    public static DoingTaskDLG doingTaskDLG;
    public static SettingsDLG settingsDLG;

    //ToolPanes
    public static EditObjectToolPanel editObjectTP;

    //Backgrounds
    public static ImageIcon mainBCG = new ImageIcon("src/ui/bcg.jpg");
    public static ImageIcon dialogBCG = new ImageIcon("src/ui/dialog_bcg.jpg");
    public static BufferedImage levelPropertiesBCG;

    //Map terrain
    public static BufferedImage grass, mud, snow, desert, water;
    public static int[] grassRA, mudRA, snowRA, desertRA, waterRA;

    public static EdgeGenerator snowEdges, stoneEdges;

    //Rendering
    public static JLabel surfaceRenderer;
    public static JLabel playerPropertiesRenderer;

    public final static int MIN_IMG_WIDTH = 62;
    public final static int MIN_IMG_HEIGHT = 34;
    public final static int MAX_IMG_WIDTH = 128;
    public final static int MAX_IMG_HEIGHT = 128;

    //BuildingPacks
    public static BuildingPack blueBPack, greenBPack, whiteBPack;

    //Mouse
    public static int lastMouseX;
    public static int lastMouseY;

    public static boolean LMB;
    public static boolean MMB;
    public static boolean RMB;

    //#DEBUG#
    public static JLabel memoryProfiler;

    Main() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setSize(1600, 800);
        frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
        frame.setIconImage(new ImageIcon("src/icons/icon.png").getImage());
        frame.add(this);
        frame.setVisible(true);

        setBackground(Color.BLACK);
        setLayout(null);

        //Init dialogs
        levelPropertiesDLG = new LevelPropertiesDLG(frame);
        newProjectDLG = new NewProjectDLG(frame);
        generateDLG = new GenerateDLG(frame);
        confirmDLG = new ConfirmDLG(frame);
        doingTaskDLG = new DoingTaskDLG(frame);
        settingsDLG = new SettingsDLG(frame);

        //Window bar
        WindowBar mainWindowBar = new WindowBar(frame, this, this, frame.getWidth(), false, true, "State of War Classic/Warmonger - Level generator/editor " + VERSION);
        mainWindowBar.setIcon("src/icons/icon.png");

        playerPropertiesRenderer = new JLabel();
        playerPropertiesRenderer.setVisible(false);
        playerPropertiesRenderer.setVerticalAlignment(SwingConstants.TOP);
        add(playerPropertiesRenderer);

        //DEBUG memory profiler
        memoryProfiler = new JLabel();
        memoryProfiler.setVerticalAlignment(SwingConstants.TOP);
        memoryProfiler.setForeground(Color.white);
        memoryProfiler.setBounds(0, frame.getHeight()-100, 300, 100);
        add(memoryProfiler);

        //Load map pieces
        try {
            grass = ImageIO.read(new File("src/mapPieces/grass.jpg"));
            mud = ImageIO.read(new File("src/mapPieces/mud.jpg"));
            snow = ImageIO.read(new File("src/mapPieces/snow.jpg"));
            desert = ImageIO.read(new File("src/mapPieces/desert.jpg"));

            levelPropertiesBCG = ImageIO.read(new File("src/ui/propsBcg.jpg"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        grassRA = Main.grass.getRGB(0, 0, Main.grass.getWidth(), Main.grass.getHeight(), null, 0, Main.grass.getWidth());
        mudRA = Main.mud.getRGB(0, 0, Main.mud.getWidth(), Main.mud.getHeight(), null, 0, Main.mud.getWidth());
        snowRA = Main.snow.getRGB(0, 0, Main.snow.getWidth(), Main.snow.getHeight(), null, 0, Main.snow.getWidth());
        desertRA = Main.desert.getRGB(0, 0, Main.desert.getWidth(), Main.desert.getHeight(), null, 0, Main.desert.getWidth());

        snowEdges = new EdgeGenerator("snow/");

        //Init tool panels
        editObjectTP = new EditObjectToolPanel(frame);

        setupUI();

        //Building pack
        blueBPack = new BuildingPack("blue/");
        greenBPack = new BuildingPack("green/");
        whiteBPack = new BuildingPack("white/");

        //Endless thread
        EndlessThread endlessThread = new EndlessThread();
        endlessThread.start();

        //Visible
        frame.setFocusable(true);
        frame.addMouseListener(this);
        frame.addKeyListener(this);

        frame.repaint();
        repaint();

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                mainWindowBar.updateUI(frame.getWidth());
            }
        });
    }

    private void setupUI() {
        // 1. Seuquence - File/Project managing
        settings = new ImageUI(this, this, 0, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_settings", true);
        newLevel = new ImageUI(this, this, 64, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_newProject", true);
        importLevel = new ImageUI(this, this, 128, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_iLvl", true);
        exportLevel = new ImageUI(this, this, 192, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_eLvl", false);
        exportPng = new ImageUI(this, this, 256, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_ePng", false);

        // 2. Seuquence - Surface editing
        surfacePaint = new ImageUI(this, this, 384, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_surfacePaint", false);
        addEdges = new ImageUI(this, this, 448, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_aEdges", false);
        addMapObjects = new ImageUI(this, this, 512, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_aObjects", false);

        // 3. Seuquence - Object editing
        levelProperties = new ImageUI(this, this, 640, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_properties", false);
        addBuilding = new ImageUI(this, this, 704, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_aBuilding", false);
        addUnit = new ImageUI(this, this, 768, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_aUnit", false);

        // 4. Seuquence - Generating
        generateLevel = new ImageUI(this, this, 896, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_gLevel", false);
        generateObjects = new ImageUI(this, this, 960, WindowBar.BAR_HEIGHT, 64, 64, "src/ui/btn_gObjects", false);

        //Action listeners
        settings.addActionListener(e -> {
            //
            settingsDLG.setVisible(true);
        });

        newLevel.addActionListener(e -> {
            //
            newProjectDLG.setVisible(true);
        });

        //Levels loading path
        importLevel.addActionListener(e -> {
            File file = openFileChooser(false, "SOW level files", "edt", "srf", "map");

            if (file != null) {
                String path = file.getAbsolutePath();
                path = path.substring(0, path.length()-4);

                File edtFile = new File( path + ".edt");
                File mapFile = new File( path + ".map");
                File srfFile = new File( path + ".srf");
                File tilFile = new File( path + ".til");
                File tmiFile = new File( path + ".tmi");

                //Check their existence
                /*if (!edtFile.exists() || !mapFile.exists() || !srfFile.exists() || !tilFile.exists() || !tmiFile.exists()) {
                    JOptionPane.showMessageDialog(Main.this, "Files of level are not complete!", "Missing files!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }*/

                //Import level
                try {
                    ImportManager.importAll(edtFile, mapFile, srfFile);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        });

        exportPng.addActionListener(e -> {
            try {
                File selectedFolder = openFileChooser(true, "Output folder");
                if (selectedFolder == null)
                    return;

                File file = new File(selectedFolder.getAbsolutePath() + "/mapScheme.jpg");
                file.createNewFile();

                ImageIO.write(Rasterizer.generateSchemeJpg(Level.mapSchemeImage, Level.levelPropertiesImage), "jpg", file);

                JOptionPane.showMessageDialog(Main.this, "Export was successfull!");
            } catch (IOException io) {
                io.printStackTrace();
                JOptionPane.showMessageDialog(Main.this, "Export failed!");
            }
        });

        exportLevel.addActionListener(e -> {
            File file = openFileChooser(false, "SOW .edt files", "edt");

            if (file != null) {
                String path = file.getAbsolutePath();
                String filename = path.substring(0, path.length()-4);

                try {
                    File edtFile = new File(filename + ".edt");
                    File mapFile = new File(filename + ".map");
                    File srfFile = new File(filename + ".srf");
                    File tilFile = new File(filename + ".til");
                    File tmiFile = new File(filename + ".tmi");

                    ExportManager exportManager = new ExportManager();
                    exportManager.exportAll(edtFile, mapFile, Level.srfImage);

                    Files.write(edtFile.toPath(), exportManager.edtBytes);
                    Files.write(mapFile.toPath(), exportManager.mapBytes);
                    Files.write(srfFile.toPath(), exportManager.srfBytes);
                    Files.write(tilFile.toPath(), exportManager.tilBytes);
                    Files.write(tmiFile.toPath(), exportManager.tmiBytes);

                    JOptionPane.showMessageDialog(Main.this, "Export was successfull!");
                } catch (IOException io) {
                    io.printStackTrace();
                    JOptionPane.showMessageDialog(Main.this, "Export failed!");
                }
            }
        });

        levelProperties.addActionListener(e -> {
            //
            levelPropertiesDLG.reasignValues();
            levelPropertiesDLG.setVisible(true);
        });

        //2nd row
        generateLevel.addActionListener(e -> {
            generateDLG.setGenerateSettings(true, true);
            generateDLG.setVisible(true);
        });

        generateObjects.addActionListener(e -> {
            generateDLG.setGenerateSettings(false, true);
            generateDLG.setVisible(true);
        });

        //Surface renderer
        surfaceRenderer = new JLabel();
        add(surfaceRenderer);
    }

    private File openFileChooser(boolean directoriesOnly, String desc, String... extensions) {
        JFileChooser fileChooser = new JFileChooser();

        if (directoriesOnly)
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        else
            fileChooser.setFileFilter(new FileNameExtensionFilter(desc, extensions));

        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    public static void main(String[] args) {
        new Main();
    }

    public static void setUIVisible(boolean visible) {
        settings.setVisible(visible);
        newLevel.setVisible(visible);
        importLevel.setVisible(visible);
        exportLevel.setVisible(visible);
        exportPng.setVisible(visible);

//        surfacePaint.setVisible(visible);
//        addEdges.setVisible(visible);
//        addMapObjects.setVisible(visible);

        levelProperties.setVisible(visible);
        addBuilding.setVisible(visible);
        addUnit.setVisible(visible);

        generateLevel.setVisible(visible);
        generateObjects.setVisible(visible);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        mainBCG.paintIcon(this, g, 0, 0);

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int btn = e.getButton();

        lastMouseX = e.getX();
        lastMouseY = e.getY();

        if (btn == 1) {
            LMB = true;
        } else if (btn == 2) {
            MMB = true;
        } else if (btn == 3) {
            RMB = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int btn = e.getButton();

        if (btn == 1) {
            LMB = false;
        } else if (btn == 2) {
            MMB = false;
        } else if (btn == 3) {
            RMB = false;
        }
    }

    @Override public void mouseClicked(MouseEvent e) {
        int mouseHitX = e.getX() + Camera.x;
        int mouseHitY = e.getY() - WindowBar.BAR_HEIGHT + Camera.y;

        //Check for building selection
        for (Building b : Level.buildings) {
            int leftX = b.xTilePos*32;
            int topY = b.yTilePos*32;
            int rightX = leftX + b.buildingType.width;
            int bottomY = topY + b.buildingType.height;

            if (mouseHitX > leftX && mouseHitX < rightX && mouseHitY > topY && mouseHitY < bottomY) {
                editObjectTP.selectBuilding(b);
                editObjectTP.setVisibility(true);
                return;
            }
        }

        //Check for unit selection
        for (Unit u : Level.units) {
            int leftX = u.x;
            int topY = u.y;
            int rightX = leftX + u.unitType.width;
            int bottomY = topY + u.unitType.height;

            if (mouseHitX > leftX && mouseHitX < rightX && mouseHitY > topY && mouseHitY < bottomY) {
                editObjectTP.selectUnit(u);
                //editObjectTP.setVisibility(true);
                return;
            }
        }
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_H && newLevel.isVisible() == addBuilding.isVisible()) { //Toggle UI
            setUIVisible(!settings.isVisible());
        } else if (keyCode == KeyEvent.VK_S && Level.mapSchemeImage != null) { // Show how map/scheme will/should really look
            surfaceRenderer.setIcon(new ImageIcon(Level.mapSchemeImage));
        } else if (keyCode == KeyEvent.VK_D && Level.srfImage != null) { // Show how surface will really look
            surfaceRenderer.setIcon(new ImageIcon(Level.srfImage));
        } else if (keyCode == KeyEvent.VK_G) { //Toggle player properties
            playerPropertiesRenderer.setVisible(!playerPropertiesRenderer.isVisible());
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) { }
}
