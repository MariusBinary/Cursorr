package mariusbinary.cursorr.network;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import mariusbinary.cursorr.activities.MainActivity;
import mariusbinary.cursorr.interfaces.IClientIOChanged;
import mariusbinary.cursorr.mouse.representation.Quaternion;
import mariusbinary.cursorr.network.models.ClientInfo;
import mariusbinary.cursorr.network.models.DataWrapper;
import mariusbinary.cursorr.network.models.ServerInfo;
import mariusbinary.cursorr.utils.PreferenceUtil;

public class ClientIOThread implements Runnable {

    private Activity mActivity;
    private IClientIOChanged mListener;

    private DataWrapper mDataWrapper;
    private ServerInfo mServerInfo;
    private ClientInfo mClientInfo;
    private ClientIOState mSocketState;
    private InetAddress mServerEndpoint;
    private DatagramSocket mSocket;
    private int mSocketPort = 5394;
    private int mSocketTimeout = 10000;
    private long mAliveTimeout = 20000;
    private long mLastAliveSignal = 0;
    private boolean mStopFlag;

    private boolean mListenerInit;
    private float mScrollSensitivity;
    private float mMouseSensitivity2d;
    private float mMouseSensitivity3d;
    private boolean mMouse3dInit;

    public ClientIOThread(final ServerInfo serverInfo) {
        mServerInfo = serverInfo;
        mSocketState = ClientIOState.UNDEFINED;
        mListenerInit = false;
    }

    public void setClientIOListener(Activity activity) {
        mActivity = activity;
        mListener = (IClientIOChanged) activity;

        if (!mListenerInit) {
            PreferenceUtil pref = PreferenceUtil.getInstance(activity);
            mClientInfo = new ClientInfo(pref.getDeviceName());
            mMouseSensitivity2d = pref.get2dMouseSensitivity();
            mMouseSensitivity3d = pref.get3dMouseSensitivity();
            mScrollSensitivity = pref.getScrollSensitivity();
            mMouse3dInit = true;
            mListenerInit = true;
        }
    }

    public void setMouseSensitivity(int mouse, float sensitivity) {
        if (mouse == 0) {
            mMouseSensitivity2d = sensitivity;
        } else {
            mMouseSensitivity3d = sensitivity;
            mMouse3dInit = true;
        }
    }

    public void setScrollSensitivity(float sensitivity) {
        mScrollSensitivity = sensitivity;
    }

    public void sendToClient(String data) {
        try {
            mSocket.send(new DatagramPacket(data.getBytes(),
                    data.getBytes().length, mServerEndpoint, mSocketPort));
        } catch (IOException ex) { }
    }

    public void sendData(String operationType, String data) {
        mDataWrapper = new DataWrapper(operationType, data);
        data = DataWrapper.getGsonString(mDataWrapper);
        sendToClient(data);
    }

    public void sendScrollData(int x, int y) {
        mDataWrapper = new DataWrapper(x, y, mScrollSensitivity, true);
        String data = new Gson().toJson(mDataWrapper);
        sendToClient(data);
    }

    public void send2dMouseData(int x, int y) {
        mDataWrapper = new DataWrapper(x, y, mMouseSensitivity2d);
        String data = new Gson().toJson(mDataWrapper);
        sendToClient(data);
    }

    public void send3dMouseData(Quaternion quaternion) {
        mDataWrapper = new DataWrapper(quaternion, mMouse3dInit, mMouseSensitivity3d);
        String data = new Gson().toJson(mDataWrapper);
        sendToClient(data);

        if (mMouse3dInit) mMouse3dInit = false;
    }

    public void sendStopSignal() {
        sendData("Stop","");
    }

    public void close() throws IOException {
        if(mSocket != null) mSocket.close();
    }

    public void run() {

        try {
            mServerEndpoint = InetAddress.getByName(mServerInfo.getAddress());
            mSocket = new DatagramSocket(mSocketPort);
            mSocket.setSoTimeout(mSocketTimeout);

            if (mServerInfo.getAddress().equals("1.2.3.4")) {
                mLastAliveSignal = System.currentTimeMillis();
                startCommunication();
            } else {
                askConnectionPermission();
            }

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        while (!mStopFlag) {
            try {
                receiveData();
            } catch (SocketTimeoutException ex) {
                long timeMillis = System.currentTimeMillis();
                if (timeMillis - mLastAliveSignal > mAliveTimeout) {
                    if (mSocketState.equals(ClientIOState.WAITING)) {
                        mSocketState = ClientIOState.DISCONNECTED_BY_WAITING;
                    } else {
                        mSocketState = ClientIOState.DISCONNECTED_BY_TIMEOUT;
                    }
                    stopCommunication();
                }
            } catch (IOException ignored) { }
        }
    }

    private void receiveData() throws IOException {

        byte[] rawData = new byte[512];
        DatagramPacket packet = new DatagramPacket(rawData, rawData.length);
        if (mSocket != null) mSocket.receive(packet);

        byte[] sizedData = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), sizedData, 0, packet.getLength());
        String receivedData = new String(sizedData);
        if(receivedData == null) return;

        switch (receivedData) {
            case "Stop":
                mSocketState = ClientIOState.DISCONNECTED_BY_SERVER;
                stopCommunication();
                break;
            case "Allowed":
                startCommunication();
                break;
            case "Denied":
                mSocketState = ClientIOState.DISCONNECTED_BY_REFUSE;
                stopCommunication();
                break;
            case "Alive":
                mLastAliveSignal = System.currentTimeMillis();
                break;
        }
    }

    public void askConnectionPermission() {
        new Thread(() -> {
            mSocketState = ClientIOState.WAITING;
            NetworkManager.sConnectionAlive = false;

            String client = new Gson().toJson(mClientInfo);
            mDataWrapper = new DataWrapper("Permission", client);
            String data = DataWrapper.getGsonString(mDataWrapper);
            sendToClient(data);

            mLastAliveSignal = System.currentTimeMillis();
        }).start();
    }

    public void setSocketState(ClientIOState state) {
        mSocketState = state;
    }

    private void startCommunication() {
        // Start the network service in background.
        Intent intent = new Intent(mActivity, NetworkService.class);
        intent.putExtra("Server", mServerInfo.getServerInfo());
        mActivity.startService(intent);

        // Alert that the communication with the server started.
        mSocketState = ClientIOState.CONNECTED;
        NetworkManager.sConnectionAlive = true;
        mListener.onDeviceConnected();
    }

    public void stopCommunication()  {
        try {
            Log.e("DEBUG_", "STATE: " + mSocketState.toString());

            // If the user ended the connection, alert the server.
            if (mSocketState.equals(ClientIOState.DISCONNECTED_BY_USER)) {
                sendStopSignal();
            }

            mStopFlag = true;
            NetworkManager.sConnectionAlive = false;
            close();

            // Alert that the communication with the server ended.
            switch (mSocketState) {
                case DISCONNECTED_BY_SERVER:
                case DISCONNECTED_BY_USER:
                case DISCONNECTED_BY_TIMEOUT:
                    mListener.onDeviceDisconnected();
                    break;
                case DISCONNECTED_BY_REFUSE:
                case DISCONNECTED_BY_WAITING:
                    mListener.onDeviceRefused();
                    break;
                case DISCONNECTED_BY_CANCEL:
                    break;
            }

        } catch (IOException ex) { }
    }
}