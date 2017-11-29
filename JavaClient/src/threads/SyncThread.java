package threads;

import components.ImageGridView;
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
            ArrayList<Map.Entry<Integer,ImageModel>> nextImages = cameraMonitor.getImage();
            long previousTimeStamp = 0;
            int counter = 0;
            boolean first = true;

            if(cameraMonitor.isSync()){

                for(Map.Entry<Integer,ImageModel> entry : nextImages){
                    ImageModel imageModel = entry.getValue();
                    if(first){
                        imageGridView.updateImage(imageModel.getImage(),entry.getKey());
                        first = false;
                    }
                    else{
                        try {

                            long diff = (imageModel.getTimeStamp() - previousTimeStamp);
                            Thread.sleep(diff);
                            //System.out.println(diff);
                            imageGridView.updateImage(imageModel.getImage(),entry.getKey());

                        } catch (InterruptedException e) {

                        }
                    }
                    counter ++;
                    previousTimeStamp = imageModel.getTimeStamp();

                }
            }else {
                for (Map.Entry<Integer, ImageModel> entry : nextImages) {
                    ImageModel imageModel = entry.getValue();
                    imageGridView.updateImage(imageModel.getImage(), entry.getKey());

                }
            }
        }

    }
}