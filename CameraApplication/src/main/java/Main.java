package main.java;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        String ipAddr = "";
	int port = 5000;
        if(args.length >= 1) {
            ipAddr = args[0];
        }
        if(args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }
        JFrame frame = new JFrame("Input Socket.");
        frame.setPreferredSize(new Dimension(1000, 600));
        JLabel label = new JLabel();
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);

        Monitor mon = new Monitor();
        InputSocket inSocket = ipAddr.isEmpty() ? new InputSocket(mon) : new InputSocket(ipAddr, port, mon);
        inSocket.start();

        DisplayThread displayThread = new DisplayThread(label, mon);
        displayThread.start();
    }
}
