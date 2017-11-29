package constants;
public class Constants {

    public static class Flags{
        public static final boolean DEBUG = false;
    }

    public static class MotionMode {
        public static final int AUTO = 0;
        public static final int MOVIE = 1;
        public static final int IDLE = 2;

    }

    public static class SyncMode {
        public static final int SYNC = 0;
        public static final int ASYNC = 1;
    }

    public static class MotionCode {
        public static final byte IDLE = 0x00;
        public static final byte MOVIE = (byte) 0xFF;
    }

    public static class ActionType{
        public static final int CHANGE_SYNC   = 0;
        public static final int CHANGE_MOTION = 1;
        public static final int CONNECT       = 2;

    }
}