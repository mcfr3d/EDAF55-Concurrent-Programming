package threads;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                ImageModel imageModel = fetchImage(new DataInputStream(socket.getInputStream()));
                cameraMonitor.addImage(this.hashCode(), imageModel);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageModel fetchImage(DataInputStream inputStream) throws IOException {
        //while (cameraMonitor.isAlive()) {
            byte[] sizeAr = new byte[4];
            int total = 0;

            while (total < 4) {
                total += inputStream.read(sizeAr, total, 4 - total);
            }
            total = 0;
            byte[] timeStampArr = new byte[8];

            while (total < 8) {
                total += inputStream.read(timeStampArr, total, 8 - total);
            }
            long timeStamp = ByteBuffer.wrap(timeStampArr).asLongBuffer().get();
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
            byte[] imgAr = new byte[size];

            total = 0;
            while (total < size) {
                total += inputStream.read(imgAr, total, size - total);
            }
            Image img = new Image(new ByteArrayInputStream(imgAr));
            return new ImageModel(img ,timeStamp);

        //}

    }
}
