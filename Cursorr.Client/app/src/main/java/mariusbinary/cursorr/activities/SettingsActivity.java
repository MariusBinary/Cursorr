package mariusbinary.cursorr.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.activities.base.BaseAppActivity;
import mariusbinary.cursorr.fragments.SettingsFragment;
import mariusbinary.cursorr.utils.PreferenceUtil;

public class SettingsActivity extends BaseAppActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();
        } else {
            mSettingsFragment = (SettingsFragment)
                    getFragmentManager().findFragmentByTag("settings_fragment");
            if (mSettingsFragment == null) {
                mSettingsFragment = new SettingsFragment();
            }
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame_settings, mSettingsFragment, "settings_fragment")
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_settings));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case "sensitivity_3D":
                float prefMouseSensitivity3d = sharedPreferences.getFloat(key, 5.0f) * 1000f;
                PreferenceUtil.getInstance(this).setMouse3dSensitivity(prefMouseSensitivity3d);
                break;
            case "sensitivity_2D":
                float prefMouseSensitivity2d = sharedPreferences.getFloat(key, 3.5f);
                PreferenceUtil.getInstance(this).setMouse2dSensitivity(prefMouseSensitivity2d);
                break;
            case "sensitivity_scroll":
                float prefScrollSensitivity = sharedPreferences.getFloat(key, 4.0f);
                PreferenceUtil.getInstance(this).setScrollSensitivity(prefScrollSensitivity);
                break;
            case "app_theme":
                applyAppTheme();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {

        if (!mSettingsFragment.isBillingBusy()) {
            super.onBackPressed();
        } else {
            Toast.makeText(SettingsActivity.this,
                    "Finish the purchase process before leaving.", Toast.LENGTH_SHORT).show();
        }
    }
}
