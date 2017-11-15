package components;

import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class MultipleChoiceView extends HBox {
    public MultipleChoiceView(String[] choices){
        this(choices,0);
    }
    public MultipleChoiceView(String[] choices , int active){
        setMinWidth(75*choices.length);
        setMaxWidth(75*choices.length);
        getStyleClass().add("multiple-choice");
        final Rectangle outputClip = new Rectangle(75*choices.length , 33);
        outputClip.setArcWidth(33);
        outputClip.setArcHeight(33);
        setClip(outputClip);

        ArrayList<Choice> choiceList = new ArrayList<>();

        for(int n = 0 ; n < choices.length ; n++){
            String c = choices[n];
            Choice choice = new Choice(c);
            choiceList.add(choice);
            if(n == active){
                choice.setActive(true);
            }
        }
        for(Choice choice : choiceList){
            choice.setOnMouseClicked(event -> {
                for(Choice otherChoice : choiceList){
                    if(choice != otherChoice){
                        otherChoice.setActive(false);
                    }else{
                        otherChoice.setActive(true);
                    }
                }
            });
            getChildren().add(choice);
        }

    }
}
