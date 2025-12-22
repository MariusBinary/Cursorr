package mariusbinary.cursorr.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import mariusbinary.cursorr.activities.SettingsActivity;
import mariusbinary.cursorr.activities.WelcomeActivity;
import mariusbinary.cursorr.adapters.DeviceAdapter;

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.network.BroadcastReceiverThread;
import mariusbinary.cursorr.network.ClientIOState;
import mariusbinary.cursorr.network.ClientIOThread;
import mariusbinary.cursorr.network.NetworkManager;
import mariusbinary.cursorr.network.models.ServerInfo;
import mariusbinary.cursorr.utils.PreferenceUtil;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionFragment extends Fragment {

    private View mView;
    private ListView mListView;
    private DeviceAdapter mCustomAdapter;
    private Button mAddDeviceButton;
    private ImageView mSettingsButton;
    private ImageView mInfoButton;
    private AlertDialog mAlertDialog;
    private ArrayList<ServerInfo> mNetworkList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNetworkList = PreferenceUtil.getInstance(getActivity()).getFavouritesDevices();
        mCustomAdapter = new DeviceAdapter(getActivity(), this, R.layout.item_device, mNetworkList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_connect, container, false);

            mListView = mView.findViewById(R.id.listView);
            mListView.setAdapter(mCustomAdapter);
            mAddDeviceButton = mView.findViewById(R.id.btn_connect);
            mSettingsButton = mView.findViewById(R.id.button_settings);
            mInfoButton = mView.findViewById(R.id.button_info);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (!NetworkManager.sBroadcastAlive) {
            discoverLocalDevices();
        } else {
            NetworkManager.mBroadcastReceiverThread.setContext(getActivity(), this);
        }

        mListView.setOnItemClickListener((adapterView, view1, position, id) -> {
            ServerInfo serverInfo = (ServerInfo) adapterView.getAdapter().getItem(position);
            if (serverInfo != null) showDeviceConnectionDialog(serverInfo, false);
        });

        mAddDeviceButton.setOnClickListener(v -> showDeviceCreateDialog());
        mSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            getActivity().startActivity(intent);
        });

        mInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.putExtra("startedByUser", true);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    // TODO: If click is not after broadcast update...problem
    public void reloadNetworkList(boolean notify) {
        ArrayList<ServerInfo> servers = PreferenceUtil.getInstance(getActivity()).getFavouritesDevices();

        mNetworkList.clear();
        for (ServerInfo info : servers) {
            mNetworkList.add(info);
        }

        if (notify) mCustomAdapter.notifyDataSetChanged();
    }

    public ArrayList<ServerInfo> getNetworkList() {
        return mNetworkList;
    }

    public void showDeviceCreateDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_create_device, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        EditText nameInput = view.findViewById(R.id.name_input);
        EditText addressInput = view.findViewById(R.id.address_input);

        addressInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        addressInput.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart)
                        + source.subSequence(start, end)
                        + destTxt.substring(dend);
                if (!resultingTxt
                        .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (int i = 0; i < splits.length; i++) {
                        if (Integer.valueOf(splits[i]) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        };

        addressInput.setFilters(filters);

        builder.setView(view);
        builder.setTitle(getString(R.string.alert_add_device));
        builder.setPositiveButton(getString(R.string.alert_add), (d, e) -> {
            if (!TextUtils.isEmpty(nameInput.getText()) &&
                    !TextUtils.isEmpty(addressInput.getText())) {
                ServerInfo device = new ServerInfo(nameInput.getText().toString());
                device.setServerAddress(addressInput.getText().toString());
                device.setServerHasVersion(false);
                createDevice(device);
            }
        });
        builder.setNegativeButton(getString(R.string.alert_cancel), (d, e) -> { });
        showDialog(builder, false);
    }

    /**
     * Add a new device to the favourites list.
     */
    public void createDevice(ServerInfo device) {
        PreferenceUtil.getInstance(getActivity()).addDeviceToFavourites(device);
        reloadNetworkList(true);
    }

    public void showDeviceConnectionDialog(ServerInfo device, boolean ignoreServerVersion) {

        boolean isServerCompatible = device.hasVersion() ? device.getVersion() >= getResources().getInteger(R.integer.server_minimum_version) : true;

        if (isServerCompatible || ignoreServerVersion) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_connecting, null);
            TextView connectingTo = view.findViewById(R.id.connectingTo);
            connectingTo.setText(getString(R.string.alert_connecting_to, device.getServerInfo()));
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);
            showDialog(builder, true, dialog -> disconnectDevice());
            connectDevice(device);
        } else {
            // Alert the user that the connection to the device was denied.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.alert_server_not_compatible))
                    .setPositiveButton(getString(R.string.alert_connect_anyway), (dialog, id) -> {
                        showDeviceConnectionDialog(device, true);
                    })
                    .setNegativeButton(getString(R.string.alert_cancel), (dialog, id) -> { });
            showDialog(builder, true);
        }
    }

    /**
     * Start the communication with a device.
     */
    public void connectDevice(ServerInfo device) {
        NetworkManager.sServerInfo = device;
        NetworkManager.mClientIOThread = new ClientIOThread(device);
        NetworkManager.mClientIOThread.setClientIOListener(getActivity());
        new Thread(NetworkManager.mClientIOThread).start();
    }

    public void showDeviceDeniedConnectionDialog() {
        // Alert the user that the connection to the device was denied.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.alert_connection_error))
                .setPositiveButton(getString(R.string.alert_retry), (dialog, id) -> {
                    showDeviceConnectionDialog(NetworkManager.sServerInfo, false);
                })
                .setNegativeButton(getString(R.string.alert_cancel), (dialog, id) -> { });
        showDialog(builder, true);
    }

    public void showDeviceRenameDialog(ServerInfo device) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_rename_device, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        EditText nameInput = view.findViewById(R.id.name_input);
        nameInput.setText(device.getServerInfo());

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.alert_rename), (d, e) -> {
            if (!TextUtils.isEmpty(nameInput.getText())) {
                device.setServerInfo(nameInput.getText().toString());
                renameDevice(device);
            }
        });
        builder.setNegativeButton(getString(R.string.alert_cancel), (d, e) -> { });
        showDialog(builder, false);
    }

    /**
     * Update the shown name of a specific device.
     */
    public void renameDevice(ServerInfo device) {
        PreferenceUtil.getInstance(getActivity()).addDeviceToFavourites(device);
        updateLocalDevices(PreferenceUtil.getInstance(getActivity()).getFavouritesDevices(), false);
    }

    public void showDeviceRemoveDialog(ServerInfo device) {

        // Alert the user that the connection to the device was denied.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.alert_remove_device, device.getAddress()))
                .setPositiveButton(getString(R.string.alert_remove), (dialog, id) -> removeDevice(device))
                .setNegativeButton(getString(R.string.alert_cancel), (dialog, id) -> {});
        showDialog(builder, true);
    }

    /**
     * Remove the device from the favourites list.
     */
    public void removeDevice(ServerInfo device) {
        PreferenceUtil.getInstance(getActivity()).removeDeviceFromFavourites(device);
        mNetworkList = PreferenceUtil.getInstance(getActivity()).getFavouritesDevices();
        mCustomAdapter.notifyDataSetChanged();
    }

    /**
     * Stop the communication with the current connected device.
     */
    public void disconnectDevice() {
        NetworkManager.mClientIOThread.setSocketState(ClientIOState.DISCONNECTED_BY_CANCEL);
        NetworkManager.mClientIOThread.stopCommunication();
        showToast(getString(R.string.alert_connection_cancelled));
    }

    Toast mToast;
    private void showToast(String text) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Dismiss all the previous alert dialogs, and show a new one.
     */
    private void showDialog(AlertDialog.Builder builder, boolean cancellable,
                            DialogInterface.OnCancelListener onCancelListener) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = builder.create();
        mAlertDialog.setCancelable(cancellable);
        if (onCancelListener != null) {
            mAlertDialog.setOnCancelListener(onCancelListener);
        }
        mAlertDialog.show();
    }

    private void showDialog(AlertDialog.Builder builder, boolean cancellable) {
        showDialog(builder, cancellable, null);
    }

    /**
     * Start the broadcast service to discover servers on the network.
     */
    private void discoverLocalDevices() {
        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = wifi.createMulticastLock("cursorrMulticastLock");
        NetworkManager.mBroadcastReceiverThread = new BroadcastReceiverThread(getActivity(), this, lock);
        new Thread(NetworkManager.mBroadcastReceiverThread).start();
    }

    /**
     * Stop the broadcast service.
     */
    public void stopLocalDevicesDiscovery() {
        NetworkManager.mBroadcastReceiverThread.stopBroadcast();
    }

    /**
     * Update the list of available devices.
     */
    public void updateLocalDevices(ArrayList<ServerInfo> discoveredDevices, boolean updateAlive) {

        boolean isBroadcastEnabled = (PreferenceUtil.getInstance(getActivity()).isBroadcastScanEnabled());
        ArrayList<Integer> onlineDevices = new ArrayList<>();

        for (int i = 0; i < discoveredDevices.size(); i++) {

            boolean newServer = true;
            for (int j = 0; j < mNetworkList.size(); j++) {

                if (mNetworkList.get(j).getAddress().equals(discoveredDevices.get(i).getAddress())) {
                    ServerInfo present = mNetworkList.get(j);
                    ServerInfo discovered = discoveredDevices.get(i);
                    if (updateAlive) {
                        present.setAlive(discovered.isAlive());
                    }
                    present.setServerHasVersion(discovered.hasVersion());
                    present.setServerVersion(discovered.getVersion());
                    if (!present.isMemorized()) {
                        present.setServerInfo(discovered.getServerInfo());
                    }
                    newServer = false;
                    onlineDevices.add(j);
                    break;
                }
            }

            if (newServer) {
                // If the server is not already present add it.
                if (isBroadcastEnabled) {
                    mNetworkList.add(discoveredDevices.get(i));
                    onlineDevices.add(mNetworkList.size() - 1);
                }
            }
        }

        // Remove unreachable devices.
        for (int i = 0; i < mNetworkList.size(); i++) {
            if (!onlineDevices.contains(i)) {
                if (!mNetworkList.get(i).isMemorized()) {
                    mNetworkList.remove(i);
                } else {
                    mNetworkList.get(i).setAlive(false);
                }
            }
        }

        mCustomAdapter.notifyDataSetChanged();
    }
}