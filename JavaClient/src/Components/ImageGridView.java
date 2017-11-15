package components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ImageGridView extends AnchorPane {

    public ImageGridView (){
        ImageView im1 = new ImageView();
        ImageView im2 = new ImageView();
        im1.setImage(new Image("file:/Users/axeldelbom/Development/edaf55test/video/output_0001.jpeg"));
        im2.setImage(new Image("file:/Users/axeldelbom/Development/edaf55test/video/output_0051.jpeg"));
        getChildren().addAll(im1,im2);
        im1.setFitHeight(500);
        im1.setFitWidth(430);
        im2.setFitHeight(500);
        im2.setFitWidth(430);
        im1.setPreserveRatio(true);
        im2.setPreserveRatio(true);

        setImageGrid(im1,im2);


    }

    public void setImageGrid(ImageView imageView1, ImageView imageView2){

        setTopAnchor(imageView1, 125.0);
        setLeftAnchor(imageView1, 30.0);
        setTopAnchor(imageView2, 125.0);
        setLeftAnchor(imageView2, 490.0);
    }

}