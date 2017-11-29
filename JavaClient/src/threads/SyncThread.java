package threads;

import components.ImageGridView;
import javafx.util.Pair;
import models.CameraModel;
import models.CameraMonitor;
import models.ImageModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SyncThread extends Thread {
    private CameraMonitor cameraMonitor;
    private ImageGridView imageGridView;

    public SyncThread(CameraMonitor cameraMonitor, ImageGridView imageGridView) {
        this.cameraMonitor = cameraMonitor;
        this.imageGridView = imageGridView;
    }

    @Override
    public void run() {
        while (cameraMonitor.isAlive()) {
            ArrayList<Pair<Integer,ImageModel>> nextImages = cameraMonitor.getImage();
            long previousTimeStamp = 0;
            int counter = 0;
            boolean first = true;

            if(cameraMonitor.isSync()){

                for(Pair<Integer,ImageModel> entry : nextImages){
                    ImageModel imageModel = entry.getValue();
                    if(first){
                        imageGridView.updateImage(imageModel.image,entry.getKey());
                        first = false;
                    }
                    else{
                        try {

                            long diff = (imageModel.timeStamp - previousTimeStamp);
                            Thread.sleep(diff);
                            imageGridView.updateImage(imageModel.image,entry.getKey());

                        } catch (InterruptedException e) {

                        }
                    }
                    counter ++;
                    previousTimeStamp = imageModel.timeStamp;

                }
            }else {
                for (Pair<Integer, ImageModel> entry : nextImages) {
                    ImageModel imageModel = entry.getValue();
                    imageGridView.updateImage(imageModel.image, entry.getKey());

                }
            }
        }

    }
}