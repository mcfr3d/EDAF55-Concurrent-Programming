package components;

import javafx.event.Event;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Map;

public class MultipleChoiceView extends HBox {
    public MultipleChoiceView(Map.Entry<Integer,String>[] choices){
        this(choices,0);
    }
    public MultipleChoiceView(Map.Entry<Integer,String>[] choices , int active){
        setMinWidth(75*choices.length);
        setMaxWidth(75*choices.length);
        getStyleClass().add("multiple-choice");
        final Rectangle outputClip = new Rectangle(75*choices.length , 33);
        outputClip.setArcWidth(33);
        outputClip.setArcHeight(33);
        setClip(outputClip);

        ArrayList<Choice> choiceList = new ArrayList<>();

        for(int n = 0 ; n < choices.length ; n++){
            Map.Entry<Integer,String> entry = choices[n];
            Choice choice = new Choice(entry.getValue() , entry.getKey());
            choiceList.add(choice);
            if(n == active){
                choice.setActive(true);
            }
        }
        for(Choice choice : choiceList){
            choice.setOnMouseClicked(event -> {
                this.fireEvent(new MultipleChoiceEvent(MultipleChoiceEvent.MULTIPLE_CHOICE_EVENT,choice.getValue()));
                for(Choice otherChoice: choiceList){
                    if(otherChoice.equals(choice)){
                        otherChoice.setActive(true);
                    }else{
                        otherChoice.setActive(false);
                    }
                }
            });
            getChildren().add(choice);
        }

    }
}
