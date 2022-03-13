package eu.yozozchomutova.dialogwindow;

import eu.yozozchomutova.Main;
import eu.yozozchomutova.ui.WindowBar;

import javax.swing.*;
import java.awt.*;

public abstract class DialogWindow extends JDialog {

    public WindowBar windowBar;

    private JLabel background;

    public DialogWindow(JFrame owner, int width, int height, boolean closeAble, String titleText) {
        super(owner, ModalityType.MODELESS);

        setBounds((int) (Main.frame.getWidth()/2f-width/2f), (int) (Main.frame.getHeight()/2f-height/2f), width, height);
        setLayout(null);
        setUndecorated(true);

        //Setup window bar
        windowBar = new WindowBar(this, this, null, width, true, closeAble, titleText);

        //Other UIs
        setupUI();

        //Background
        background = new JLabel(Main.dialogBCG);
        background.setBounds(0, 0, width, height);
        add(background);
    }

    public abstract void setupUI();
}
