package actions;

import constants.Constants;

public class ConnectAction extends Action {
    private String address;
    public ConnectAction(String address){
        super(Constants.ActionType.CONNECT);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}