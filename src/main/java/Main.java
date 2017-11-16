package main.java;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Input Socket.");
        frame.setPreferredSize(new Dimension(1000, 600));
        JLabel label = new JLabel();
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);

        Monitor mon = new Monitor();

        InputSocket inSocket = new InputSocket(mon);
        inSocket.start();

        DisplayThread displayThread = new DisplayThread(label, mon);
        displayThread.start();
    }
}
