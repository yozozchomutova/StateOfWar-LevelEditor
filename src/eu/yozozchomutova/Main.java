package eu.yozozchomutova;

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

import static eu.yozozchomutova.Generator.GenerateSchemeJpg;
import static eu.yozozchomutova.Generator.resultImage;

public class Main extends JPanel implements MouseListener, KeyListener {

    public static final String VERSION = "2.0";

    public static Random random = new Random();

    public static JFrame frame;

    private static final String[] levelListStr = {
            "M01", "M02", "M03", "M04", "M05", "M06", "M07", "M08",
            "M09", "M10", "M11", "M12", "M13", "M14", "M15", "M16",
            "M17", "M18", "M19", "M20", "M21", "M22"
    };

    public static final int[] levelIndexes = {
            1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22
    };

    /*private static final String[] biomesStr = {
            "Grass", "Mud", "Grass + Mud", "Snow", "Desert"
    };*/

    private static final String[] biomesStr = {
            "Snow"
    };

    private JTextField sowLevelsPath;
    private Button applySowLPaths;
    private JComboBox levelListCombo;
    private Button load;
    private Button exportPng;
    private Button exportMap;

    private Button generateLevelBtn;
    private JDialog generateLevelDialog;

    private static ImageIcon bcg = new ImageIcon("src/ui/bcg.jpg");
    public static int[] propsBcg;

    //DIALOG UI
    public static JTextField mapWidthTF, mapHeightTF;
    public static JComboBox biomesCB;
    public static JTextField noiseSize;
    public static JTextField additionalGreenHeadquartersCount;
    public static JTextField whiteBuildingsCount;

    //Map terrain
    public static BufferedImage grass, mud, snow, desert, water;
    public static int[] grassRA, mudRA, snowRA, desertRA, waterRA;

    public static EdgeGenerator snowEdges;

    //Rendering
    public static JLabel surfaceRenderer;
    public static JLabel playerPropertiesRenderer;

    public final static int MIN_IMG_WIDTH = 62;
    public final static int MIN_IMG_HEIGHT = 34;
    public final static int MAX_IMG_WIDTH = 128;
    public final static int MAX_IMG_HEIGHT = 128;

    public static int renderImgWidth;
    public static int renderImgHeight;
    public static int renderImgWidth32;
    public static int renderImgHeight32;

    //BuildingPacks
    public static BuildingPack blueBPack, greenBPack, whiteBPack;

    //Mouse
    public static int lastMouseX;
    public static int lastMouseY;

    public static boolean LMB;
    public static boolean MMB;
    public static boolean RMB;

    Main() {
        frame = new JFrame("State of War Classic/Warmonger - Level generator " + VERSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        setBackground(Color.BLACK);
        setLayout(null);

        playerPropertiesRenderer = new JLabel();
        playerPropertiesRenderer.setVisible(false);
        playerPropertiesRenderer.setVerticalAlignment(SwingConstants.TOP);
        add(playerPropertiesRenderer);

        setupUI();

        //Load map pieces
        try {
            //grass = ImageIO.read(new File("src/mapPieces/grass.jpg"));
            //mud = ImageIO.read(new File("src/mapPieces/mud.jpg"));
            snow = ImageIO.read(new File("src/mapPieces/snow.jpg"));
            //water = ImageIO.read(new File("src/mapPieces/sea2.jpg"));

            BufferedImage propsBcgBI = ImageIO.read(new File("src/ui/propsBcg.jpg"));
            propsBcg = propsBcgBI.getRGB(0, 0, propsBcgBI.getWidth(), propsBcgBI.getHeight(), null, 0, propsBcgBI.getWidth());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //grassRA = Main.grass.getRGB(0, 0, Main.grass.getWidth(), Main.grass.getHeight(), null, 0, Main.grass.getWidth());
        //mudRA = Main.mud.getRGB(0, 0, Main.mud.getWidth(), Main.mud.getHeight(), null, 0, Main.mud.getWidth());
        snowRA = Main.snow.getRGB(0, 0, Main.snow.getWidth(), Main.snow.getHeight(), null, 0, Main.snow.getWidth());
        //waterRA = Main.water.getRGB(0, 0, Main.water.getWidth(), Main.water.getHeight(), null, 0, Main.water.getWidth());

        snowEdges = new EdgeGenerator("snow/");

        //Building pack
        blueBPack = new BuildingPack("blue/");
        greenBPack = new BuildingPack("green/");
        whiteBPack = new BuildingPack("white/");

        //Endless thread
        EndlessThread endlessThread = new EndlessThread();
        endlessThread.start();

        //Visible
        frame.setVisible(true);
        frame.setSize(1600, 800);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        frame.repaint();
        frame.setFocusable(true);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        repaint();

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
    }

    private void setupUI() {
        //Levels loading path
        sowLevelsPath = new JTextField();
        sowLevelsPath.setBounds(10, 10, 400, 20);
        add(sowLevelsPath);

        File sowPathPreferenceFile = new File("src/sowPath.pref");

        if (sowPathPreferenceFile.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(sowPathPreferenceFile.toPath());
                sowLevelsPath.setText(new String(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        applySowLPaths = new Button(this, 420, 10, 100, 20, "Apply");
        applySowLPaths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Save
                    if (!sowPathPreferenceFile.exists()) {
                        sowPathPreferenceFile.createNewFile();
                    }

                    Files.write(sowPathPreferenceFile.toPath(), sowLevelsPath.getText().getBytes());
                } catch (IOException ioe) {}
            }
        });

        load = new Button(this, 530, 10, 100, 20, "Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = openFileChooser(true);

                if (file != null) {
                    for (int i = 0; i < levelListStr.length; i++) {
                        File edtFile = new File( file.getAbsolutePath() + "/" + levelListStr[i] + ".edt");
                        File mapFile = new File( file.getAbsolutePath() + "/" + levelListStr[i] + ".map");
                        File srfFile = new File( file.getAbsolutePath() + "/" + levelListStr[i] + ".srf");
                        File tilFile = new File( file.getAbsolutePath() + "/" + levelListStr[i] + ".til");
                        File tmiFile = new File( file.getAbsolutePath() + "/" + levelListStr[i] + ".tmi");

                        //Check their existence
                        if (!edtFile.exists() || !mapFile.exists() || !srfFile.exists() || !tilFile.exists() || !tmiFile.exists()) {
                            JOptionPane.showMessageDialog(Main.this, "Files of level " + levelListStr[i] + " are not complete!", "Missing files!", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(Main.this, "All files were found!", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    sowLevelsPath.setText(file.getAbsolutePath());
                }
            }
        });

        levelListCombo = new JComboBox(levelListStr);
        levelListCombo.setBounds(640, 10, 100, 20);
        levelListCombo.addKeyListener(this);
        levelListCombo.addMouseListener(this);
        add(levelListCombo);

        exportPng = new Button(this, 750, 10, 100, 20, "Export PNG");
        exportPng.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = new File("src/outputMap.jpg");

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    ImageIO.write(GenerateSchemeJpg(), "jpg", file);

                    JOptionPane.showMessageDialog(Main.this, "Export was successfull!");
                } catch (IOException io) {
                    io.printStackTrace();
                    JOptionPane.showMessageDialog(Main.this, "Export failed!");
                }
            }
        });

        exportMap = new Button(this, 860, 10, 100, 20, "Export MAP");
        exportMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File edtFile = new File( sowLevelsPath.getText() + "/" + levelListStr[levelListCombo.getSelectedIndex()] + ".edt");
                    File mapFile = new File( sowLevelsPath.getText() + "/" + levelListStr[levelListCombo.getSelectedIndex()] + ".map");
                    File srfFile = new File( sowLevelsPath.getText() + "/" + levelListStr[levelListCombo.getSelectedIndex()] + ".srf");
                    File tilFile = new File( sowLevelsPath.getText() + "/" + levelListStr[levelListCombo.getSelectedIndex()] + ".til");
                    File tmiFile = new File( sowLevelsPath.getText() + "/" + levelListStr[levelListCombo.getSelectedIndex()] + ".tmi");

                    ExportManager exportManager = new ExportManager();
                    exportManager.exportAll(edtFile, mapFile, Generator.srfImage);

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

        //2nd row
        generateLevelBtn = new Button(this, 10, 40, 100, 20,"Generate");
        generateLevelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateLevelDialog.setVisible(true);
            }
        });

        //Dialog
        generateLevelDialog = new JDialog(frame, "Generate new level", true);
        generateLevelDialog.setBounds(200, 200, 500, 520);
        generateLevelDialog.setResizable(false);
        generateLevelDialog.setLayout(null);
        setupDialogUI();

        //Surface renderer
        surfaceRenderer = new JLabel();
        add(surfaceRenderer);
    }

    private void setupDialogUI() {
        /*JCheckBox experimentalMode = new JCheckBox("Experimental mode");
        experimentalMode.setBounds(5, 5, 200, 20);
        generateLevelDialog.add(experimentalMode);*/

        mapWidthTF = new JTextField("64");
        mapWidthTF.setBounds(5,5, 50, 20);
        mapWidthTF.setToolTipText("Map width (value * 32 = map width in pixels)");
        generateLevelDialog.add(mapWidthTF);

        mapHeightTF = new JTextField("64");
        mapHeightTF.setBounds(60,5, 50, 20);
        mapHeightTF.setToolTipText("Map height (value * 32 = map height in pixels)");
        generateLevelDialog.add(mapHeightTF);

        biomesCB = new JComboBox(biomesStr);
        biomesCB.setBounds(5, 30, 80, 20);
        generateLevelDialog.add(biomesCB);

        additionalGreenHeadquartersCount = new JTextField("0");
        additionalGreenHeadquartersCount.setToolTipText("Green headquarters count");
        additionalGreenHeadquartersCount.setBounds(5, 60, 50, 20);
        generateLevelDialog.add(additionalGreenHeadquartersCount);

        noiseSize = new JTextField("500");
        noiseSize.setToolTipText("Noise zoom");
        noiseSize.setBounds(5, 90, 50, 20);
        generateLevelDialog.add(noiseSize);

        whiteBuildingsCount = new JTextField("15");
        whiteBuildingsCount.setToolTipText("White buildings count");
        whiteBuildingsCount.setBounds(5, 120, 50, 20);
        generateLevelDialog.add(whiteBuildingsCount);

        Button generate = new Button(generateLevelDialog, 5, 445, 100, 30, "Generate");
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateLevelDialog.setVisible(false);

                try {
                    renderImgWidth32 = Integer.parseInt(mapWidthTF.getText());
                    renderImgHeight32 = Integer.parseInt(mapHeightTF.getText());

                    //Bounds
                    renderImgWidth32 = Math.min(MAX_IMG_WIDTH, renderImgWidth32);
                    renderImgHeight32 = Math.min(MAX_IMG_HEIGHT, renderImgHeight32);
                    renderImgWidth32 = Math.max(MIN_IMG_WIDTH, renderImgWidth32);
                    renderImgHeight32 = Math.max(MIN_IMG_HEIGHT, renderImgHeight32);

                    renderImgWidth = renderImgWidth32 * 32;
                    renderImgHeight = renderImgHeight32 * 32;
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(Main.this, "Invalid map size", "Map size error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Generator.Generate(Double.parseDouble(noiseSize.getText()), Integer.parseInt(additionalGreenHeadquartersCount.getText()), Integer.parseInt(whiteBuildingsCount.getText()), biomesCB.getSelectedIndex());
            }
        });
    }

    private File openFileChooser(boolean directoriesOnly) {
        JFileChooser fileChooser = new JFileChooser();

        if (directoriesOnly)
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        else
            fileChooser.setFileFilter(new FileNameExtensionFilter("SOW Map files (.srf)", "srf"));

        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    public static void main(String[] args) {
        new Main();
    }

    private void setUIVisible(boolean visible) {
        sowLevelsPath.setVisible(visible);
        applySowLPaths.setVisible(visible);
        levelListCombo.setVisible(visible);
        load.setVisible(visible);
        exportPng.setVisible(visible);
        exportMap.setVisible(visible);
        generateLevelBtn.setVisible(visible);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        bcg.paintIcon(this, g, 0, 0);

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

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_H) { //Toggle UI
            setUIVisible(!load.isVisible());
        } else if (keyCode == KeyEvent.VK_S) { // Show how map/scheme will/should really look
            surfaceRenderer.setIcon(new ImageIcon(Generator.resultImage));
        } else if (keyCode == KeyEvent.VK_D) { // Show how surface will really look
            surfaceRenderer.setIcon(new ImageIcon(Generator.srfImage));
        } else if (keyCode == KeyEvent.VK_G) { //Toggle player properties
            playerPropertiesRenderer.setVisible(!playerPropertiesRenderer.isVisible());
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) { }
}
