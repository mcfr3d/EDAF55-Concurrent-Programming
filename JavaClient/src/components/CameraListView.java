package components;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class CameraListView extends ListView<CameraModel> {
    public CameraListView(ObservableList<CameraModel> cameraList){
        setMinWidth(290);
        setMaxWidth(290);
        setItems(cameraList);
        setCellFactory(param -> new CameraCell());
        getStyleClass().add("camera-list");
    }
}
