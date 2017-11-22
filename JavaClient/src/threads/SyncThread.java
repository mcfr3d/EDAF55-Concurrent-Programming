package threads;

import components.ImageGridView;
import models.CameraModel;
import models.CameraMonitor;
import models.ImageModel;

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
            Map.Entry<Integer,ImageModel> nextImage = cameraMonitor.getImage();
            imageGridView.updateImage(nextImage.getValue().getImage(),nextImage.getKey());
            /*cameraMonitor.waitForImage();
            HashMap<Integer , ImageModel> imageMap = cameraMonitor.getImages();
            imageMap.forEach((key, imageModel) -> {
                if(imageModel != null){
                    imageGridView.updateImage(imageModel.getImage(),key);
                }
            });*/

        }
    }
}