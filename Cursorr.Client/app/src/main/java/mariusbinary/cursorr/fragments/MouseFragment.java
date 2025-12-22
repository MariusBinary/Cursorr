package mariusbinary.cursorr.fragments;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.activities.SettingsActivity;
import mariusbinary.cursorr.controls.Touchpad;
import mariusbinary.cursorr.mouse.orientation.KalmanFilterProvider;
import mariusbinary.cursorr.mouse.orientation.OrientationProvider;
import mariusbinary.cursorr.network.NetworkManager;
import mariusbinary.cursorr.utils.PreferenceUtil;

public class MouseFragment extends Fragment implements View.OnClickListener, View.OnTouchListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Touchpad mTouchpad;
    private OrientationProvider mOrientationProvider;
    private Vibrator mVibrator;
    private Toast mToast;

    private long mTouchTime;
    private boolean mFirstTouch;
    private int mMouseMode = 0;
    private boolean mVolumeButtonsEnabled = false;
    private boolean mVibrationEnabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mouse, container, false);

        Button left = view.findViewById(R.id.button_touch_left);
        Button right = view.findViewById(R.id.button_touch_right);
        Button middle = view.findViewById(R.id.button_touch_middle);
        ImageButton buttonSettings = view.findViewById(R.id.button_settings);
        ImageButton buttonBack = view.findViewById(R.id.button_back);
        ImageButton buttonMode = view.findViewById(R.id.button_mode);

        mTouchpad = view.findViewById(R.id.touchpad);

        if (savedInstanceState != null) {
            mMouseMode = savedInstanceState.getInt("mMouseMode", 0);
        } else {
            mMouseMode = PreferenceUtil.getInstance(getActivity()).getDefaultMouseMode();
        }
        switchMode(mMouseMode, buttonMode, false);

        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        middle.setOnTouchListener(this);
        buttonSettings.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonMode.setOnClickListener(this);

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load user preferences.
        PreferenceUtil pref = PreferenceUtil.getInstance(getActivity());
        mVolumeButtonsEnabled = pref.isVolumeButtonsEnabled();
        mVibrationEnabled = pref.isVibrationEnabled();
        mTouchpad.setGestureRecognition(pref.isGestureRecognitionEnabled());
        mTouchpad.setTouchpadTrail(pref.isTouchpadTrailEnabled());
        pref.registerOnSharedPreferenceChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("mMouseMode", mMouseMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
        super.onDestroyView();
        stopGyroscopicMouse();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        switch (key) {
            case PreferenceUtil.MOUSE_2D_SENSITIVITY:
                float sensitivity_2d = PreferenceUtil.getInstance(getActivity()).get2dMouseSensitivity();
                NetworkManager.mClientIOThread.setMouseSensitivity(0, sensitivity_2d);
                break;
            case PreferenceUtil.MOUSE_3D_SENSITIVITY:
                float sensitivity_3d = PreferenceUtil.getInstance(getActivity()).get3dMouseSensitivity();
                NetworkManager.mClientIOThread.setMouseSensitivity(1, sensitivity_3d);
                break;
            case PreferenceUtil.SCROLL_SENSITIVITY:
                float sensitivity_scroll = PreferenceUtil.getInstance(getActivity()).getScrollSensitivity();
                NetworkManager.mClientIOThread.setScrollSensitivity(sensitivity_scroll);
                break;
            case PreferenceUtil.VOLUME_BUTTONS:
                mVolumeButtonsEnabled = PreferenceUtil.getInstance(getActivity()).isVolumeButtonsEnabled();
                break;
            case PreferenceUtil.VIBRATE_ON_TOUCH:
                mVibrationEnabled = PreferenceUtil.getInstance(getActivity()).isVibrationEnabled();
                break;
            case PreferenceUtil.GESTURE_RECOGNITION:
                mTouchpad.setGestureRecognition(PreferenceUtil.getInstance(getActivity()).isGestureRecognitionEnabled());
                break;
            case PreferenceUtil.TOUCHPAD_TRAIL:
                mTouchpad.setTouchpadTrail(PreferenceUtil.getInstance(getActivity()).isTouchpadTrailEnabled());
                break;
        }
    }

    private void sendMouseButtonData(final String data) {
        new Thread(() -> {
            if (NetworkManager.sConnectionAlive) {
                NetworkManager.mClientIOThread.sendData("Mouse_Button", data);
            }
        }).start();
    }

    public boolean handleHardwareButtons(String action) {
        if (mVolumeButtonsEnabled) {
            new Thread(() -> {
                if (NetworkManager.sConnectionAlive) {
                    NetworkManager.mClientIOThread.sendData("Hardware_Button", action);
                }
            }).start();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (view.getId() == R.id.button_touch_right) {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (NetworkManager.sConnectionAlive) {
                    sendMouseButtonData("right");
                }
                vibrate();
            }
            return false;
        } else if (view.getId() == R.id.button_touch_middle) {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                vibrate();
                if (NetworkManager.sConnectionAlive) {
                    sendMouseButtonData("middle");
                }
            }
            return false;
        } else if (view.getId() == R.id.button_touch_left) {
            if (NetworkManager.sConnectionAlive && motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (mFirstTouch && (System.currentTimeMillis() - mTouchTime) <= 300) {
                    mFirstTouch = false;
                } else {
                    mFirstTouch = true;
                    mTouchTime = System.currentTimeMillis();
                }
                sendMouseButtonData("left");
                vibrate();
            }
            return false;
        }

        return true;
    }

    void vibrate() {
        if (!mVibrationEnabled) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            mVibrator.vibrate(80);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_touch_right:
                sendMouseButtonData("right");
                vibrate();
                break;
            case R.id.button_touch_middle:
                sendMouseButtonData("middle");
                vibrate();
                break;
            case R.id.button_back:
                getActivity().onBackPressed();
                break;
            case R.id.button_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.button_mode:
                int mouseMode = mMouseMode == 0 ? 1 : 0;
                switchMode(mouseMode, (ImageButton) view, true);
                break;
        }
    }

    private void switchMode(int mode, ImageButton view, boolean alert) {
        if (mode == 0) {
            stopGyroscopicMouse();
            if (alert) animateRotation(view);
            view.setImageResource(R.drawable.ic_mouse_2d);
            if (alert) showToast(getString(R.string.toast_2d_mode));
            mTouchpad.setTouchpadEnabled(true);
            mMouseMode = 0;
        } else {
            startGyroscopicMouse();
            if (alert) animateRotation(view);
            view.setImageResource(R.drawable.ic_mouse_3d);
            if (alert) showToast(getString(R.string.toast_3d_mode));
            mTouchpad.setTouchpadEnabled(false);
            mMouseMode = 1;
        }

        PreferenceUtil.getInstance(getActivity()).setLastMouseMode(mode);
    }

    private void showToast(String text) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void animateRotation(View view) {
        view.setRotation(0);
        view.animate().setDuration(500).rotation(360F)
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void startGyroscopicMouse() {
        new Thread(() -> {
            mOrientationProvider = new KalmanFilterProvider((SensorManager)
                    getActivity().getSystemService(Activity.SENSOR_SERVICE));
        }).start();
    }

    private void stopGyroscopicMouse() {
        if (mOrientationProvider != null) {
            mOrientationProvider.sensorStop();
        }
    }
}