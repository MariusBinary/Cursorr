package mariusbinary.cursorr.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.List;

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.utils.PreferenceUtil;

public class SettingsFragment extends PreferenceFragment implements PurchasesUpdatedListener {

    private Toast mToastAlert;
    private AlertDialog mAlertDialog;
    private BillingClient mBillingClient;
    private boolean mIsBillingConnected;
    private boolean mIsBillingBusy;

    private String APP_PREMIUM_ID = "premium_upgrade";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceCategory prefCategoryPremium = (PreferenceCategory) findPreference("group_premium");
        Preference prefRestorePremium = findPreference("restore_premium");
        Preference prefBuyPremium = findPreference("buy_premium");
        Preference prefAppLicense = findPreference("app_license");

        prefRestorePremium.setOnPreferenceClickListener(preference -> {
            restorePremium();
            return true;
        });

        prefBuyPremium.setOnPreferenceClickListener(preference -> {
            showPremiumOfferDialog();
            return true;
        });

        EditTextPreference listPreference = (EditTextPreference) findPreference("device_name");
        if(listPreference.getText() == null) {
            String deviceName = PreferenceUtil.getInstance(getActivity()).getDeviceName();
            listPreference.setText(deviceName);
        }

        findPreference("app_support").setOnPreferenceClickListener(preference -> {
            try {
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:" + getString(R.string.app_support)));
                startActivity(email);
            } catch (Exception ex) { }
            return true;
        });

        findPreference("app_website").setOnPreferenceClickListener(preference -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cursorrapp.com"));
                startActivity(intent);
            } catch (Exception ex) { }
            return true;
        });

        SwitchPreference sw = (SwitchPreference)findPreference("volume_buttons");
        sw.setOnPreferenceClickListener(preference -> {
            if (!PreferenceUtil.getInstance(getActivity()).isUserPremium()) {
                showPremiumOfferDialog();
                sw.setChecked(false);
            }
            return true;
        });

        if (PreferenceUtil.getInstance(getActivity()).isUserPremium()) {
            getPreferenceScreen().removePreference(prefCategoryPremium);
            prefAppLicense.setSummary(getString(R.string.pref_app_license_premium));
        } else {
            prefAppLicense.setSummary(getString(R.string.pref_app_license_free));
        }
    }

    private void showPremiumOfferDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_go_premium, null);
        Button buyButton = view.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(v -> purchasePremium());
        showDialog(new AlertDialog.Builder(getActivity()).setView(view), true, null);
    }

    private void showPremiumSuccessDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ok_premium, null);
        showDialog(new AlertDialog.Builder(getActivity()).setView(view), true, (v) -> {
            getActivity().recreate();
        });
    }

    private void showPremiumErrorDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_error_premium, null);
        showDialog(new AlertDialog.Builder(getActivity()).setView(view), true, null);
    }


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

    private void showDialog(String message) {
        if (mToastAlert != null) {
            mToastAlert.cancel();
        }

        mToastAlert = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        mToastAlert.show();
    }

    private void restorePremium() {
        if (!mIsBillingBusy)
            setupBilling(1);
        else
            showDialog("A purchase or recovery of the premium version is already in progress.");
    }

    private void purchasePremium() {
        if (!mIsBillingBusy)
            setupBilling(0);
        else
            showDialog("A purchase or recovery of the premium version is already in progress.");
    }

    private void handlePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add(APP_PREMIUM_ID);

        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        mBillingClient.querySkuDetailsAsync(params, (billingResult1, list) -> {
            if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {

                SkuDetails premiumSku = null;

                for (SkuDetails skuDetails : list) {
                    if (skuDetails.getSku().equals(APP_PREMIUM_ID)) {
                        premiumSku = skuDetails;
                        break;
                    }
                }

                if (premiumSku != null) {
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(premiumSku)
                            .build();
                    mBillingClient.launchBillingFlow(getActivity(), flowParams);
                } else {
                    onPremiumFailedPurchased("no_products");
                }

            } else {
                onPremiumFailedPurchased("no_products");
            }
        });
    }

    private void handleRestore() {
        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, list) -> {
            boolean hasBoughtPremium = false;
            for (Purchase purchase : list) {
                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    for (String sku : purchase.getSkus()) {
                        if (sku.equals(APP_PREMIUM_ID)) {
                            hasBoughtPremium = true;
                        }
                    }
                }
            }

            if (hasBoughtPremium) {
                onPremiumSuccessRestored();
            } else {
                onPremiumFailedRestored();
            }
        });
    }

    private void setupBilling(int type) {

        mIsBillingBusy = true;

        if (mBillingClient == null) {
            mBillingClient = BillingClient.newBuilder(getActivity())
                    .setListener(this)
                    .enablePendingPurchases()
                    .build();
        }

        if (!mIsBillingConnected) {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    mIsBillingConnected = true;

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (type == 0) {
                            handlePurchase();
                        } else {
                            handleRestore();
                        }
                    } else {
                        onBillingServiceFailed("invalid_payment_system");
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    mIsBillingConnected = false;
                    onBillingServiceFailed("no_connection");
                }
            });
        } else {
            if (type == 0) {
                handlePurchase();
            } else {
                handleRestore();
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            for (Purchase purchase : list) {
                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                    if(!purchase.isAcknowledged()){
                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.getPurchaseToken())
                                        .build();
                        mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult1 -> {
                            if(billingResult1.getResponseCode()== BillingClient.BillingResponseCode.OK) {
                                onPremiumSuccessPurchased();
                            } else {
                                onPremiumFailedPurchased("error");
                            }
                        });
                    }

                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            onPremiumFailedPurchased("cancelled");
        } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            onPremiumSuccessRestored();
        }else{
            onPremiumFailedPurchased("error");
        }
    }

    public void onPremiumSuccessPurchased() {
        mIsBillingBusy = false;
        getActivity().runOnUiThread(() -> {
            PreferenceUtil.getInstance(getActivity()).setUserPremium(true);
            showPremiumSuccessDialog();
        });
    }

    public void onPremiumFailedPurchased(@NonNull String errorCode) {
        mIsBillingBusy = false;
        getActivity().runOnUiThread(() -> showPremiumErrorDialog());

        // errorCode: ["cancelled"] "Cancelled"
        // errorCode: ["no_products"] "In-app purchase for the premium version is not currently available, please try again."
        // errorCode: ["error"] "Error"
    }

    public void onPremiumSuccessRestored() {
        mIsBillingBusy = false;
        getActivity().runOnUiThread(() -> {
            PreferenceUtil.getInstance(getActivity()).setUserPremium(true);
            showPremiumSuccessDialog();
        });
    }

    public void onPremiumFailedRestored() {
        mIsBillingBusy = false;
        getActivity().runOnUiThread(() -> {
            showDialog("No previous purchase was found for the premium version of the application.");
        });
    }

    public void onBillingServiceFailed(String errorCode) {
        mIsBillingBusy = false;
        mIsBillingConnected = false;
        getActivity().runOnUiThread(() -> showPremiumErrorDialog());

        // errorCode: ["invalid_payment_system"] "Check google play account for payment system"
        // errorCode: ["no_connection"] "Payment system is not valid at the moment"
    }

    public boolean isBillingBusy() {
        return mIsBillingBusy;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBillingClient != null) {
            if (mBillingClient.isReady()) {
                mBillingClient.endConnection();
                mIsBillingConnected = false;
            }
        }
    }
}