package mariusbinary.cursorr.controls.seekbar;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

public class SeekBarPreference extends DialogPreference implements Slider.OnChangeListener, OnClickListener
{
    private static final String androidns="http://schemas.android.com/apk/res/android";

    private Slider mSeekBar;
    private TextView mSplashText, mValueText;
    private Context mContext;
    private String mDialogMessage, mSuffix;
    private float mDefault, mMin = 1.0f, mMax, mValue = 0.0f;

    public SeekBarPreference(Context context, AttributeSet attrs) {

        super(context,attrs); 
        mContext = context;

        // Get string value for dialogMessage :
        int mDialogMessageId = attrs.getAttributeResourceValue(androidns, "dialogMessage", 0);
        if(mDialogMessageId == 0) mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
        else mDialogMessage = mContext.getString(mDialogMessageId);

        // Get string value for suffix (text attribute in xml file) :
        int mSuffixId = attrs.getAttributeResourceValue(androidns, "text", 0);
        if(mSuffixId == 0) mSuffix = attrs.getAttributeValue(androidns, "text");
        else mSuffix = mContext.getString(mSuffixId);

        // Get default and max seekbar values :
        mDefault = attrs.getAttributeFloatValue(androidns, "defaultValue", 5.0f);
        mMax = attrs.getAttributeFloatValue(androidns, "max", 10.0f);
    }

    @Override 
    protected View onCreateDialogView() {

        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        mSplashText = new TextView(mContext);
        mSplashText.setPadding(30, 10, 30, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        mSeekBar = new Slider(mContext);
        mSeekBar.addOnChangeListener(this);
        mSeekBar.setContentDescription(getTitle());
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist())
            mValue = getPersistedFloat(mDefault);

        mSeekBar.setValueFrom(mMin);
        mSeekBar.setValueTo(mMax);
        mSeekBar.setValue(mValue);
        mSeekBar.setStepSize(0.5f);

        return layout;
    }

    @Override 
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setValueFrom(mMin);
        mSeekBar.setValueTo(mMax);
        mSeekBar.setValue(mValue);
        mSeekBar.setStepSize(0.5f);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)  
    {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            mValue = shouldPersist() ? getPersistedFloat(mDefault) : 0.0f;
        else
            mValue = (Float) defaultValue;
    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        String t = String.valueOf(value);
        mValueText.setText(mSuffix == null ? t : t.concat(" " + mSuffix));
    }

    @Override
    public void showDialog(Bundle state) {
        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (shouldPersist()) {

            mValue = mSeekBar.getValue();
            persistFloat(mSeekBar.getValue());
            callChangeListener(mSeekBar.getValue());
        }

        ((AlertDialog) getDialog()).dismiss();
    }
}