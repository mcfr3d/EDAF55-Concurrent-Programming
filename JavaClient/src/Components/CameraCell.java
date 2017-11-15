package components;

import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CameraCell extends ListCell<CameraModel> {
    private static final String IP_TEXT_CLASS = "ip-txt";
    private static final String CELL_CLASS = "camera-cell";
    private static final String NAME_TEXT_CLASS= "camera-name-txt";

    private GridPane grid = new GridPane();
    private Text nameText;
    private Text ipText;
    private Button connectButton;
    private BorderPane mainPane;
    private VBox rows;


    public CameraCell(){
        getStyleClass().add(CELL_CLASS);
        nameText = new Text();
        ipText = new Text();
        connectButton = new Button();
        mainPane = new BorderPane();
        rows = new VBox();
        configControls();
        configPanes();
        rows.getChildren().add(nameText);
        rows.getChildren().add(ipText);
        StackPane sp = new StackPane();
        sp.getChildren().add(connectButton);
        mainPane.setLeft(rows);
        mainPane.setRight(sp);


    }

    private void configControls(){
        ipText.getStyleClass().add(IP_TEXT_CLASS);
        nameText.getStyleClass().add(NAME_TEXT_CLASS);
        nameText.setWrappingWidth(150);
    }
    private void configPanes(){
        mainPane.setMaxWidth(250);
        mainPane.setMinWidth(250);

        rows.setMaxWidth(180);

    }

    /*Text nameTxt;
    Button connectBtn;
    boolean connected;
    public CameraItem(String name, String ip,boolean connected){
        this.connected = connected;
        setMaxWidth(300);
        setMinWidth(300);
        Text nameTxt = new Text(name);
        nameTxt.setWrappingWidth(185);
        Text ipTxt = new Text(ip);
        getStyleClass().add("camera-item");


        nameTxt.getStyleClass().add("camera-txt");
        ipTxt.getStyleClass().add("ip-txt");
        connectBtn = new Button(connected ? "Disconnect":"Connect");
        VBox rows = new VBox();
        rows.setMaxWidth(180);

        rows.getChildren().add(nameTxt);
        rows.getChildren().add(ipTxt);
        setLeft(rows);
        setRight(connectBtn);



    }*/

    @Override
    protected void updateItem(CameraModel camera, boolean empty) {
        super.updateItem(camera,empty);
        if(empty){
            setText(null);
            setGraphic(null);
        }else{
            connectButton.setText(camera.isConnected() ? "Disconnect":"Connect");
            ipText.setText(camera.getIp());
            nameText.setText(camera.getName());
            setGraphic(mainPane);
        }
    }

}
