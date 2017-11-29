package actions;

import actions.Action;
import constants.Constants;

public class MotionAction extends Action {
    private int code;
    public MotionAction(int code){
        super(Constants.ActionType.CHANGE_MOTION);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}