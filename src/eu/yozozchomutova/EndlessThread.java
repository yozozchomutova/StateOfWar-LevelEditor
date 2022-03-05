package eu.yozozchomutova;

import java.awt.*;

public class EndlessThread extends Thread{

    @Override
    public void run() {
        //Get data

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

                Camera.x = Math.min(newCamX, Main.renderImgWidth - Main.frame.getWidth());
                Camera.y = Math.min(newCamY, Main.renderImgHeight - Main.frame.getHeight() + 25);
            }

            Main.lastMouseX = mouseX;
            Main.lastMouseY = mouseY;

            //Update surface jlabel
            Main.surfaceRenderer.setBounds(-Camera.x, -Camera.y, Main.renderImgWidth, Main.renderImgHeight);
        }
    }
}
