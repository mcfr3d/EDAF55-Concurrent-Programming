package actions;

import actions.Action;
import constants.Constants;

public class SyncAction extends Action {
    private int code;
    public SyncAction(int code){
        super(Constants.ActionType.CHANGE_SYNC);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}