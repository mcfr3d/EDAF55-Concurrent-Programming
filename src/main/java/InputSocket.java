package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class InputSocket extends Thread {
    public static final String LOCAL_HOST = "127.0.0.1";
    public static final int DEF_PORT = 5000;
    private String serverHostName;
    private int port;

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
    }

    @Override
    public void run() {
        try {
            System.out.println("Attemping to connect to host " + serverHostName + " on port " + port + ".");
            Socket socket = null;
            BufferedReader in = null;
            try {
                socket = new Socket(serverHostName, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host: " + serverHostName);
                System.exit(1);
            }
            String receivedMessage = in.readLine();
            while(receivedMessage != null) {
                System.out.println(receivedMessage);
                receivedMessage = in.readLine();
            }
            socket.close();

        } catch (IOException e) {
            System.err.println("Couldn't get I/O");
            System.exit(1);
        }
    }
}