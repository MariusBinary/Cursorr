package mariusbinary.cursorr.network;

import mariusbinary.cursorr.network.models.ServerInfo;

public class NetworkManager {
    public static ServerInfo sServerInfo;
    public static boolean sConnectionAlive = false;
    public static boolean sBroadcastAlive = false;
    public static ClientIOThread mClientIOThread;
    public static BroadcastReceiverThread mBroadcastReceiverThread;
}