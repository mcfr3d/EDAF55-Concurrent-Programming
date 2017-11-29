package actions;

import models.CameraMonitor;

public abstract class Action{
    public void execute(CameraMonitor monitor) {
        op(monitor);
    }

    abstract void op(CameraMonitor monitor);
}