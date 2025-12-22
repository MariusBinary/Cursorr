package mariusbinary.cursorr.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mariusbinary.cursorr.network.models.ServerInfo;

public final class PreferenceUtil {

    public static final String USER_PREMIUM = "is_premium";
    public static final String USER_FIRST_PREMIUM = "first_premium";
    public static final String DEVICE_NAME = "device_name";
    public static final String SERVER_LIST = "favourite_devices";
    public static final String APP_THEME = "app_theme";
    public static final String FIRST_TIME = "is_first_time";
    public static final String MOUSE_3D_SENSITIVITY = "mouse_3d_sensitivity";
    public static final String MOUSE_2D_SENSITIVITY = "mouse_2d_sensitivity";
    public static final String SCROLL_SENSITIVITY = "scroll_sensitivity";
    public static final String DEFAULT_MOUSE_MODE = "startup_mouse_type";
    public static final String LAST_MOUSE_MODE = "last_mouse_type";
    public static final String BROADCAST_DISCOVERY = "broadcast_scan";
    public static final String VIBRATE_ON_TOUCH = "vibrate_on_press";
    public static final String VOLUME_BUTTONS = "volume_buttons";
    public static final String GESTURE_RECOGNITION = "recognize_gestures";
    public static final String TOUCHPAD_TRAIL = "touchpad_trail";

    private static PreferenceUtil sInstance;

    private final SharedPreferences mPreferences;

    private PreferenceUtil(@NonNull final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public void registerOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public final boolean isUserPremium() {
        return mPreferences.getBoolean(USER_PREMIUM, false);
    }

    public final boolean isFirstTime() {
        return mPreferences.getBoolean(FIRST_TIME, true);
    }

    public final void setFirstTime(boolean isFirstTime) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public final void setUserPremium(boolean isPremium) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(USER_PREMIUM, isPremium);
        editor.putBoolean(USER_FIRST_PREMIUM, isPremium);
        editor.commit();
    }

    public final boolean isFirstPremium() {
        return mPreferences.getBoolean(USER_FIRST_PREMIUM, false);
    }

    public final void setUserFirstPremium() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(USER_FIRST_PREMIUM, false);
        editor.commit();
    }

    /**
     * Return the device name that will be shown on the server.
     */
    public final String getDeviceName() {

        String savedDeviceName = mPreferences.getString(DEVICE_NAME, null);
        if (savedDeviceName != null) {
            return savedDeviceName;
        }

        try {
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            String deviceName = myDevice.getName();
            return deviceName;
        }catch (Exception ex) {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return (model.substring(0, 1).toUpperCase() +
                        model.substring(1).toLowerCase());
            }
            return (manufacturer.substring(0, 1).toUpperCase() +
                    manufacturer.substring(1).toLowerCase()) + " " + model;
        }
    }

    /**
     * Set the device name.
     */
    public final void setDeviceName(String name) {

        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(DEVICE_NAME, name);
        editor.commit();
    }

    /**
     * Add the device to the favourites or update it's data if already present.
     */
    public final void addDeviceToFavourites(ServerInfo serverInfo) {

        SharedPreferences.Editor editor = mPreferences.edit();
        String mDevices = mPreferences.getString(SERVER_LIST, null);
        HashMap<String, String> hDevices = new HashMap<>();

        if (mDevices != null) {
            Type typeOfHashMap = new TypeToken<HashMap<String, String>>() { }.getType();
            hDevices = new Gson().fromJson(mDevices, typeOfHashMap);
        }

        hDevices.put(serverInfo.getAddress(), serverInfo.getServerInfo());

        String json = new Gson().toJson(hDevices);
        editor.putString(SERVER_LIST, json);
        editor.commit();
    }

    /**
     * Return a list of all the favourite user devices.
     */
    public final ArrayList<ServerInfo> getFavouritesDevices() {
        ArrayList<ServerInfo> devices = new ArrayList<>();
        String mDevices = mPreferences.getString(SERVER_LIST, null);

        if (mDevices != null) {
            HashMap<String, String> hDevices = new Gson().fromJson(mDevices, new TypeToken<HashMap<String, String>>(){}.getType());
            for(Map.Entry<String, String> entry : hDevices.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                ServerInfo device = new ServerInfo(value);
                device.setServerAddress(key);
                device.setMemorized(true);
                devices.add(device);
            }
        }

        return devices;
    }

    /**
     * Remove the device from the favourites list.
     */
    public final boolean removeDeviceFromFavourites(ServerInfo device) {
        SharedPreferences.Editor editor = mPreferences.edit();
        String mDevices = mPreferences.getString(SERVER_LIST, null);
        HashMap<String, String> hDevices = new HashMap<>();

        if (mDevices != null) {
            Type typeOfHashMap = new TypeToken<HashMap<String, String>>() { }.getType();
            hDevices = new Gson().fromJson(mDevices, typeOfHashMap);
            hDevices.remove(device.getAddress());

            String json = new Gson().toJson(hDevices);
            editor.putString(SERVER_LIST, json);
            editor.commit();
        }

        return true;
    }

    public final String getAppTheme()
    {
        return mPreferences.getString(APP_THEME, "system");
    }

    public final float get3dMouseSensitivity() {
        return mPreferences.getFloat(MOUSE_3D_SENSITIVITY, 5000.0f);
    }

    public final float get2dMouseSensitivity() {
        return mPreferences.getFloat(MOUSE_2D_SENSITIVITY, 3.5f);
    }

    public final void setMouse2dSensitivity(float sensitivity) {

        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(MOUSE_2D_SENSITIVITY, sensitivity);
        editor.commit();
    }

    public final void setMouse3dSensitivity(float sensitivity) {

        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(MOUSE_3D_SENSITIVITY, sensitivity);
        editor.commit();
    }

    public final float getScrollSensitivity() {
        return mPreferences.getFloat(SCROLL_SENSITIVITY, 4.0f);
    }

    public final void setScrollSensitivity(float sensitivity) {

        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(SCROLL_SENSITIVITY, sensitivity);
        editor.commit();
    }

    public final boolean isVibrationEnabled() {
        return mPreferences.getBoolean(VIBRATE_ON_TOUCH, false);
    }

    public final boolean isVolumeButtonsEnabled() {
        return mPreferences.getBoolean(VOLUME_BUTTONS, false);
    }

    public final void setAppTheme(String theme) {

        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(APP_THEME, theme);
        editor.commit();
    }

    public final int getDefaultMouseMode() {
        String mouseMode = mPreferences.getString(DEFAULT_MOUSE_MODE, "2d_mouse");

        switch (mouseMode) {
            case "2d_mouse":
                return 0;
            case "3d_mouse":
                return 1;
            case "last_used":
                return getLastMouseMode();
        }

        return 0;
    }

    public final int getLastMouseMode() {
        return mPreferences.getInt(LAST_MOUSE_MODE, 0);
    }

    public final boolean isBroadcastScanEnabled() {
        return mPreferences.getBoolean(BROADCAST_DISCOVERY, true);
    }

    public final boolean isGestureRecognitionEnabled() {
        return mPreferences.getBoolean(GESTURE_RECOGNITION, true);
    }

    public final boolean isTouchpadTrailEnabled() {
        return mPreferences.getBoolean(TOUCHPAD_TRAIL, true);
    }

    public final void setLastMouseMode(int mode) {

        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_MOUSE_MODE, mode);
        editor.commit();
    }
}
