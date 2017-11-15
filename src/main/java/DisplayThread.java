package main.java;

import javax.swing.*;
import java.awt.*;

public class DisplayThread extends Thread {

    private JLabel label;
    private Monitor monitor;

    public DisplayThread(JLabel label, Monitor monitor) {
        this.label = label;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            Image image = null;
            try {
                image = monitor.getNextImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
                interrupt();
            }
            if(image != null) {
                label.setIcon(new ImageIcon(image));
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                interrupt();
            }
        }
    }
}
