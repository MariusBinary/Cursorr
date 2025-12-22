package mariusbinary.cursorr.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NetworkService extends Service {

    private static String sAddress;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sAddress = intent.getStringExtra("Server");
        Toast.makeText(this, "Connected to " + sAddress,
                Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!NetworkManager.sConnectionAlive) return;

        new Thread(() -> {
            try {
                NetworkManager.mClientIOThread.setSocketState(ClientIOState.DISCONNECTED_BY_USER);
                NetworkManager.mClientIOThread.stopCommunication();
            } catch (Exception ignored) {}
        }).start();

        Toast.makeText(this, sAddress + " disconnected successfully", Toast.LENGTH_SHORT).show();
    }
}