package main.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class InputSocket extends Thread {
    public static final String LOCAL_HOST = "127.0.0.1";
    public static final int DEF_PORT = 5000;
    private String serverHostName;
    private int port;
    private Monitor mon;
    private int disconnectCounter = 0;

    public InputSocket() {
        this(LOCAL_HOST, DEF_PORT);
    }

    public InputSocket(String serverHostName) {
        this(serverHostName, DEF_PORT);
    }

    public InputSocket(int port) {
        this(LOCAL_HOST, port);
    }

    public InputSocket(String serverHostName, int port) {
        this.serverHostName = serverHostName;
        this.port = port;
        this.mon = null;
    }

    public InputSocket(Monitor mon) {
        this(LOCAL_HOST, DEF_PORT);
        this.mon = mon;
    }

    @Override
    public void run() {
        while(!isInterrupted() && disconnectCounter < 500) {
            try {
                System.out.println("Attemping to connect to host " + serverHostName + " on port " + port + ".");
                Socket socket = null;
                InputStream in = null;
                try {
                    socket = new Socket(serverHostName, port);
                    disconnectCounter = 0;
                    in = socket.getInputStream();
                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host: " + serverHostName);
                    //System.exit(1);
                }

                byte[] sizeOfImageArray = new byte[4];
                int readBytes = in.read(sizeOfImageArray);
                if(readBytes < sizeOfImageArray.length) {
                    System.err.println("Error: Only able to read " +readBytes + " bytes of the image size.");
                }

                for(byte b = 0; b < 2; b++) {
                    byte temp = sizeOfImageArray[b];
                    int lastIndex = sizeOfImageArray.length-1-b;
                    sizeOfImageArray[b] = sizeOfImageArray[lastIndex];
                    sizeOfImageArray[lastIndex] = temp;
                }

                int sizeOfImage = ByteBuffer.wrap(sizeOfImageArray).asIntBuffer().get();
                System.out.println("Size of image: " + sizeOfImage);
                byte[] imageArray = new byte[sizeOfImage];

                in.read(imageArray);

                if(mon != null) {
                    mon.parseImageBytes(imageArray);
                }

                socket.close();

            } catch (IOException e) {
                System.err.println("Couldn't get I/O");
                disconnectCounter++;
            }
        }
        if(disconnectCounter > 10) System.out.println("Disconnecting...");
    }
}