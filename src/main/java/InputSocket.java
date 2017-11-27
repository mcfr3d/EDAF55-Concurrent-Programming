package main.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class InputSocket extends Thread {
    public static final String LOCAL_HOST = "127.0.0.1";
    public static final int DEF_PORT = 5000;
    private String serverHostName;
    private int port;
    private Monitor mon;
    private int disconnectCounter = 0;

    public InputSocket(String serverHostName, int port, Monitor mon) {
        this.serverHostName = serverHostName;
        this.port = port;
        this.mon = mon;
    }

    public InputSocket(Monitor mon) {
        this(LOCAL_HOST, DEF_PORT, mon);
    }

    @Override
    public void run() {
        while(!isInterrupted() && disconnectCounter < 500) {
            System.out.println("Attemping to connect to host " + serverHostName + " on port " + port + ".");
            Socket socket = null;
            BufferedInputStream bis = null;
            try {
                socket = new Socket(serverHostName, port);
		System.out.println("socket created");
		
                bis = new BufferedInputStream(socket.getInputStream());
                disconnectCounter = 0;
                System.out.println("Fetching image...");
                fetchImage(bis);

                bis.close();
                socket.close();
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host: " + serverHostName);
                //System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't connect.");
                disconnectCounter++;
            }

//            try {
//                sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }
        if(disconnectCounter > 10) System.out.println("Disconnecting...");
    }

    private void fetchImage(InputStream is) {
        try {

            byte[] sizeOfImageArray = new byte[4];
            int readBytes = is.read(sizeOfImageArray);
            if(readBytes < sizeOfImageArray.length) {
                System.err.println("Error: Only able to read " +readBytes + " bytes of the image size.");
                return;
            }

            for(byte b = 0; b < 2; b++) {
                byte temp = sizeOfImageArray[b];
                int lastIndex = sizeOfImageArray.length-1-b;
                sizeOfImageArray[b] = sizeOfImageArray[lastIndex];
                sizeOfImageArray[lastIndex] = temp;
            }

            int sizeOfImage = ByteBuffer.wrap(sizeOfImageArray).asIntBuffer().get();
            System.out.println("Size of image: " + sizeOfImage);
            if(sizeOfImage <= 0) {
                disconnectCounter++;
                return;
            }
	    int read = 0;
            //byte[] imageArray = new byte[sizeOfImage];
	    LinkedList<byte[]> list = new LinkedList<>();
	    while(read < sizeOfImage) {
//            if(is.skip(4) == 4) {
	        byte[] imageArray = new byte[1];
                read += is.read(imageArray);
	        list.addLast(imageArray);
	//	is.skip(49996);
//            } else {
//                System.out.println("Not able to skip 4 bytes.");
//            }
	    }
	    byte[] imageArray = new byte[sizeOfImage];
	    int counter = 0;
	    for(byte[] arr: list) {
		for(int i = 0; i<arr.length && counter+i < sizeOfImage; i++) {
		    imageArray[counter+i] = arr[i];
		}
		counter += arr.length;
	    }

                if(mon != null) {
                    mon.parseImageBytes(imageArray);
                }
	    is.skip(50000-4-sizeOfImage);


        } catch (IOException e) {
            System.err.println("Couldn't get I/O");
            disconnectCounter++;
        }
    }
}
