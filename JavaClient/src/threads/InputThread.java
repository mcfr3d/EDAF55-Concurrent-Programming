package threads;

import constants.Constants;
import javafx.scene.image.Image;
import models.CameraMonitor;
import models.ImageModel;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class InputThread extends Thread {

    private Socket socket;
    private CameraMonitor cameraMonitor;

    public InputThread(Socket socket, CameraMonitor cameraMonitor) {
        this.socket = socket;
        this.cameraMonitor = cameraMonitor;
    }

    @Override
    public void run() {
        DataInputStream inputStream = null;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            if(Constants.Flags.DEBUG) System.out.println("DataInputStream in InputThread initialized successfully.");
            while (cameraMonitor.isAlive() && !isInterrupted()) {
                ImageModel imageModel = fetchImage(inputStream);
                cameraMonitor.addImage(this.hashCode(), imageModel);
            }
            if(Constants.Flags.DEBUG) System.out.println("Terminating InputThread.");

        } catch (IOException e) {
            if(Constants.Flags.DEBUG) System.out.println("DataInputStream in InputThread caused IOException.");
        } finally {
            try {
                if(inputStream != null) inputStream.close();
            } catch(IOException e) {
                if(Constants.Flags.DEBUG) System.out.println("DataInputStream in InputThread already closed.");
            }
        }
    }

    private ImageModel fetchImage(DataInputStream inputStream) throws IOException {
        int total = 0;

        // Fetching size of image
        byte[] sizeAr = new byte[4];
        while (total < 4) {
            total += inputStream.read(sizeAr, total, 4 - total);
        }
        total = 0;

        // Fetching timestamp of image
        byte[] timeStampArr = new byte[8];
        while (total < 8) {
            total += inputStream.read(timeStampArr, total, 8 - total);
        }
        total = 0;

        // Fetching image bytes
        long timeStamp = ByteBuffer.wrap(timeStampArr).asLongBuffer().get();
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
        byte[] imgAr = new byte[size];
        while (total < size) {
            total += inputStream.read(imgAr, total, size - total);
        }

        Image img = new Image(new ByteArrayInputStream(imgAr));
        return new ImageModel(img, timeStamp);
    }
}
