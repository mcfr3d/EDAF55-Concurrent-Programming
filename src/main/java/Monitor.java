package main.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

public class Monitor {
    LinkedList<Image> buffer;

    public Monitor() {
        buffer = new LinkedList<>();
    }

    public synchronized void parseImageBytes(byte[] bytes) {
        long startTime = System.currentTimeMillis();
        System.out.println("Reading: " + startTime);
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            System.err.println("Image was not reassembled.");
        }

        if(image != null) {
            long endTime = System.currentTimeMillis();
            System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + endTime);
            System.out.println("It took " + (endTime - startTime) + " ms to process the picture.");
            buffer.add(image);
            notifyAll();
        } else {
            System.err.println("Image was corrupt.");
        }
    }

    public synchronized Image getNextImage() throws InterruptedException {
        while(buffer.isEmpty()) wait();
        return buffer.removeFirst();
    }
}
