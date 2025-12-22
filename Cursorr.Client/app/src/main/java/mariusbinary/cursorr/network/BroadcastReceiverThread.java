package mariusbinary.cursorr.network;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.util.Log;

import mariusbinary.cursorr.activities.MainActivity;
import mariusbinary.cursorr.fragments.ConnectionFragment;
import mariusbinary.cursorr.network.models.ServerInfo;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class BroadcastReceiverThread implements Runnable {

    private Activity mActivity;
    private ConnectionFragment mConnectionFragment;
    private WifiManager.MulticastLock mLock;
    private DatagramSocket mSocket;
    private InetAddress mBroadcastAddress;
    private ArrayList<ServerInfo> mDiscoveredServers;
    private long lastBroadcastRequest = 0;

    private static final int BROADCAST_TIMEOUT = 2500;
    private static final int BROADCAST_LISTEN_PORT = 1235;
    private static final int BROADCAST_SEND_PORT = 1236;

    private String TAG = "DEBUG_BROADCAST";

    public BroadcastReceiverThread(Activity activity, ConnectionFragment fragment, WifiManager.MulticastLock lock) {
        mActivity = activity;
        mLock = lock;
        mSocket = null;
        mDiscoveredServers = new ArrayList<>();
        mConnectionFragment = fragment;
    }

    public void setContext(Activity activity, ConnectionFragment fragment) {
        mActivity = activity;
        mConnectionFragment = fragment;
    }

    public void stopBroadcast() {
        NetworkManager.sBroadcastAlive = false;
    }

    @Override
    public void run() {
        try {
            mLock.acquire();
            if (mSocket == null) {
                mSocket = new DatagramSocket(BROADCAST_LISTEN_PORT);
                mSocket.setSoTimeout(BROADCAST_TIMEOUT);
                mSocket.setBroadcast(true);
                mBroadcastAddress = InetAddress.getByName("255.255.255.255");
                NetworkManager.sBroadcastAlive = true;
            }
        } catch (IOException e) { e.printStackTrace(); }

        Log.v(TAG, "Broadcast STARTED (" + NetworkManager.sBroadcastAlive + ")");
        while(mSocket != null && NetworkManager.sBroadcastAlive) {
            try {

                // Send an alert in broadcast to ask to all the listening servers
                // in the network to respond if available.
                if (System.currentTimeMillis() - lastBroadcastRequest >= BROADCAST_TIMEOUT) {
                    if (mDiscoveredServers.size() > 0) {
                        updateDetectedServers();
                    }
                    String gsonStr = "Cursorr:BroadcastRequest";
                    DatagramPacket broadcastPacket = new DatagramPacket(
                            gsonStr.getBytes(), gsonStr.getBytes().length, mBroadcastAddress, BROADCAST_SEND_PORT);
                    mSocket.send(broadcastPacket);

                    Log.v(TAG, "Broadcast request after: " + (System.currentTimeMillis() - lastBroadcastRequest) + "ms");
                    lastBroadcastRequest = System.currentTimeMillis();
                }

                // After the alert was send, wait for a response from the server.
                if (!mLock.isHeld()) mLock.acquire();
                DatagramPacket datagramPacket = new DatagramPacket(
                        new byte[ServerInfo.SERVER_INFO_LENGTH], ServerInfo.SERVER_INFO_LENGTH);

                mSocket.receive(datagramPacket);
                mLock.release();

                String gsonString = new String(datagramPacket.getData());
                gsonString = gsonString.substring(0, gsonString.indexOf("}") + 1);

                ServerInfo serverInfo = new Gson().fromJson(gsonString, ServerInfo.class);
                String serverAddress = datagramPacket.getAddress().toString().substring(1);
                serverInfo.setServerAddress(serverAddress);
                serverInfo.setAlive(true);

                Log.v(TAG, "Received (from: " + serverAddress + "): " + gsonString);
                mDiscoveredServers.add(serverInfo);

            } catch (SocketTimeoutException ex) {
                Log.v(TAG, "Broadcast request TIMEOUT");

            } catch (IOException e) {
                Log.v(TAG, "Broadcast socket exception: " + e);
            }
        }

        if (mSocket != null) mSocket.close();
        Log.v(TAG, "Broadcast STOPPED");
    }

    public void updateDetectedServers() {
        mActivity.runOnUiThread(() -> {
            mConnectionFragment.updateLocalDevices(mDiscoveredServers, true);
            mDiscoveredServers.clear();
        });
    }
}