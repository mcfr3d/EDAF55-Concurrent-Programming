package threads;

import components.MainPane;
import constants.Constants;
import javafx.util.Pair;
import models.CameraMonitor;
import models.ImageModel;

import java.awt.*;
import java.util.ArrayList;

public class SyncThread extends Thread {
    private CameraMonitor cameraMonitor;
    private MainPane mainPane;

    public SyncThread(CameraMonitor cameraMonitor, MainPane mainPane) {
        this.cameraMonitor = cameraMonitor;
        this.mainPane = mainPane;
    }

    @Override
    public void run() {
        while (cameraMonitor.isAlive() && !isInterrupted()) {
            Pair<Integer,ImageModel> image = cameraMonitor.getImage();
            mainPane.updateImage(image.getValue().image , image.getKey());
        }
        if(Constants.Flags.DEBUG) System.out.println("Terminating SyncThread.");
    }
}