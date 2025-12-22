package mariusbinary.cursorr;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import mariusbinary.cursorr.utils.PreferenceUtil;

public class App extends Application {

    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        applyAppTheme();
    }

    public void applyAppTheme() {
        switch (PreferenceUtil.getInstance(this).getAppTheme()) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public static App getInstance() {
        return app;
    }
}
