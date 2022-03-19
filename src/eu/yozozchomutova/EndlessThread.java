package eu.yozozchomutova;

import eu.yozozchomutova.ui.WindowBar;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class EndlessThread extends Thread{

    @Override
    public void run() {
        //Get data
        LinkedList<WindowBar> windowBars = WindowBar.windowBars;

        while (true) {
            try { Thread.sleep(10); } catch (Exception e) {}
            PointerInfo mouseInfo = MouseInfo.getPointerInfo();

            int mouseX = 0, mouseY = 0;

            if (mouseInfo != null) {
                mouseX = mouseInfo.getLocation().x;
                mouseY = mouseInfo.getLocation().y;
            }

            //Camera movement
            if (Main.RMB) {
                int newCamX = Camera.x + mouseX - Main.lastMouseX;
                int newCamY = Camera.y + mouseY - Main.lastMouseY;

                newCamX = Math.max(0, newCamX);
                newCamY = Math.max(0, newCamY);

                Camera.x = Math.min(newCamX, Level.renderImgWidth - Main.frame.getWidth());
                Camera.y = Math.min(newCamY, Level.renderImgHeight - Main.frame.getHeight());
            }

            //Move window bars
            for (WindowBar wb : windowBars) {
                if (wb.windowIsMoving) {
                    Point windowPoint = wb.window.getLocation();

                    int newX = windowPoint.x + (mouseX - Main.lastMouseX);
                    int newY = windowPoint.y + (mouseY - Main.lastMouseY);

                    wb.window.setLocation(newX, newY);

                    if (wb.window instanceof JFrame) {
                        JFrame frame = (JFrame) wb.window;

                        if (frame.getExtendedState() != 0)
                            frame.setExtendedState(0);
                    }

                    break;
                }
            }

            Main.lastMouseX = mouseX;
            Main.lastMouseY = mouseY;

            //Update surface jlabel
            Main.surfaceRenderer.setBounds(-Camera.x, -Camera.y + WindowBar.BAR_HEIGHT, Level.renderImgWidth, Level.renderImgHeight);

            //Update #DEBUG# memory profiler
            Main.memoryProfiler.setText("<html>Memory Total: " + (int)(Runtime.getRuntime().maxMemory()/1024f) + "<br/>Free memory: " + (int)(Runtime.getRuntime().freeMemory()/1024f) + "<br/>Usage: " + (int)((float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (float)Runtime.getRuntime().maxMemory() * 100f) + "%</html>");
        }
    }
}
