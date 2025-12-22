package mariusbinary.cursorr.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/*
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
 */

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.activities.base.BaseAppActivity;
import mariusbinary.cursorr.fragments.ConnectionFragment;
import mariusbinary.cursorr.fragments.MouseFragment;
import mariusbinary.cursorr.interfaces.IClientIOChanged;
import mariusbinary.cursorr.mouse.orientation.OrientationProvider;
import mariusbinary.cursorr.network.NetworkManager;
import mariusbinary.cursorr.network.NetworkService;
import mariusbinary.cursorr.utils.PreferenceUtil;

public class MainActivity extends BaseAppActivity implements IClientIOChanged {

    private static final int REQUEST_INTERNET_ACCESS = 1001;

    private ConnectionFragment sConnectionFragment;
    private MouseFragment sMouseFragment;
    private String mLastFragmentTag;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore the old session if activity was recreated.
        if (savedInstanceState != null) {

            if (NetworkManager.sConnectionAlive && NetworkManager.mClientIOThread != null) {
                sMouseFragment = (MouseFragment)
                        getSupportFragmentManager().findFragmentByTag("MouseFragment");

                NetworkManager.mClientIOThread.setClientIOListener(this);
            }

            sConnectionFragment = (ConnectionFragment)
                    getSupportFragmentManager().findFragmentByTag("ConnectionFragment");

        } else if (sConnectionFragment == null) {
            sConnectionFragment = new ConnectionFragment();
        }

        // Load the content view.
        setContentView(R.layout.activity_main);

        // Check if user bought the premium version. If so don't load ads.
        /*
        if (!PreferenceUtil.getInstance(this).isUserPremium()) {
            MobileAds.initialize(this, initializationStatus ->
                    Log.e("DEBUG_", "MobileAds.initialize"));

            // Load bottom ad banner.
            RelativeLayout adLayout = findViewById(R.id.ad_container);
            FrameLayout frameLayout = findViewById(R.id.content_frame);
            int frameAdPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
                    getResources().getDisplayMetrics());
            frameLayout.setPadding(0,0,0,frameAdPadding);

            AdView adView = new AdView(this);
            adView.setAdSize(getBannerAdSize());
            adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));

            RelativeLayout.LayoutParams adLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            adLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            adLayout.setVisibility(View.VISIBLE);
            adLayout.addView(adView, adLayoutParams);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        */

        // Initialize the app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        } else {
            initApplication();
        }
    }

    /*
    private AdSize getBannerAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.i("DEBUG_", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        Log.d("DEBUG_", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        Log.d("DEBUG_", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Log.d("DEBUG_", "The ad was shown.");
                                    }
                                });

                        interstitialAd.show(MainActivity.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("DEBUG_", loadAdError.getMessage());
                    }
                });
    }
     */

    @Override
    public void onDeviceDisconnected() {
        runOnUiThread(() -> {
            setConnectionFragment();
        });
    }

    @Override
    public void onDeviceConnected() {
        sConnectionFragment.stopLocalDevicesDiscovery();
        PreferenceUtil.getInstance(this).addDeviceToFavourites(NetworkManager.sServerInfo);
        sConnectionFragment.reloadNetworkList(false);

        runOnUiThread(() -> {
            if (!PreferenceUtil.getInstance(this).isUserPremium()) {
                //loadInterstitialAd();
            }
            showMouseFragment();
        });
    }

    @Override
    public void onDeviceRefused() {
        runOnUiThread(() -> sConnectionFragment.showDeviceDeniedConnectionDialog());
    }

    // After there was called an activity recreation, all the previous fragments
    // needs to be recreated and added back to stack, with their previous state.
    public void restoreFragments() {
        if (NetworkManager.sConnectionAlive) {
            displayFragment(sMouseFragment, "MouseFragment");
        } else {
            displayFragment(sConnectionFragment, "ConnectionFragment");
        }
    }

    public void showMouseFragment() {
        if(OrientationProvider.checkGyro(this)) {
            sMouseFragment = new MouseFragment();
            displayFragment(sMouseFragment, "MouseFragment");
        } else {
            Toast.makeText(this, getString(R.string.toast_no_gyroscope), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_INTERNET_ACCESS) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.toast_permission_denied), Toast.LENGTH_LONG).show();
            } else {
                initApplication();
            }
        }
    }

    private void getInternetPermission() {
        boolean hasPermission1 = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission2 = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission3 = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission1 || !hasPermission2 || !hasPermission3) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_INTERNET_ACCESS);
        } else {
            initApplication();
        }
    }

    private void initApplication() {
        restoreFragments();
    }

    public void setConnectionFragment() {
        if (isDestroyed()) return;
        displayFragment(sConnectionFragment, "ConnectionFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;

        if (mLastFragmentTag != null) {
            if (mLastFragmentTag.equals("MouseFragment")) {
                displayFragment(sMouseFragment, "MouseFragment");
            } else {
                displayFragment(sConnectionFragment, "ConnectionFragment");
            }
        }

        if (PreferenceUtil.getInstance(this).isFirstPremium()) {
            PreferenceUtil.getInstance(this).setUserFirstPremium();
            Log.e("DEBUG_", "Recreate");
            recreate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            NetworkManager.mBroadcastReceiverThread.stopBroadcast();
            if (NetworkManager.sConnectionAlive) {
                this.stopService(new Intent(this, NetworkService.class));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (NetworkManager.sConnectionAlive && NetworkManager.mClientIOThread != null) {
            if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
                return sMouseFragment.handleHardwareButtons("volume_down");
            } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
                return sMouseFragment.handleHardwareButtons("volume_up");
            } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_MUTE)) {
                return sMouseFragment.handleHardwareButtons("volume_mute");
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    public void displayFragment(Fragment fragment, String tag) {
        if (isPaused) {
            mLastFragmentTag = tag;
            return;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_animation, R.anim.exit_animation)
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(tag)
                    .commit();

            mLastFragmentTag = null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (NetworkManager.sConnectionAlive) {
            // Alert the user that the connection to the device was denied.
            builder.setMessage(getString(R.string.alert_close_connection_summary))
                    .setPositiveButton(getString(R.string.alert_close_connection_positive), (dialog, id) -> {
                        stopService(new Intent(this, NetworkService.class));
                    })
                    .setNegativeButton(getString(R.string.alert_close_connection_negative), (dialog, id) -> { });
        } else {
            // Alert the user that the connection to the device was denied.
            builder.setMessage(getString(R.string.alert_exit_summary))
                    .setPositiveButton(getString(R.string.alert_exit_positive), (dialog, id) -> { finishAndRemoveTask(); })
                    .setNegativeButton(getString(R.string.alert_exit_negative), (dialog, id) -> { });
        }

        builder.create();
        builder.show();
    }
}