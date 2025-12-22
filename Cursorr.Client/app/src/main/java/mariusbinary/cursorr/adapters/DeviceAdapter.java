package mariusbinary.cursorr.adapters;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import mariusbinary.cursorr.R;
import mariusbinary.cursorr.fragments.ConnectionFragment;
import mariusbinary.cursorr.network.models.ServerInfo;

import java.util.ArrayList;

public class DeviceAdapter extends ArrayAdapter<ServerInfo> {

    private Activity mActivity;
    private ConnectionFragment mConnectionFragment;

    public DeviceAdapter(Activity activity, ConnectionFragment connectionFragment,
                         int textViewResourceId, ArrayList<ServerInfo> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
        mConnectionFragment = connectionFragment;
    }

    private class ViewHolder {
        private TextView mTextView;
        private TextView mSubtitle;
        private RelativeLayout mBadgeStatus;
        private ImageButton mMoreButton;
    }

    @Override
    public int getCount() {
        super.getCount();
        if (mConnectionFragment != null) {
            return mConnectionFragment.getNetworkList().size();
        } else {
            return  0;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            convertView = layoutInflater.inflate(R.layout.item_device, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTextView = convertView.findViewById(R.id.code);
            viewHolder.mBadgeStatus = convertView.findViewById(R.id.badge_status);
            viewHolder.mSubtitle = convertView.findViewById(R.id.subtitle);
            viewHolder.mMoreButton = convertView.findViewById(R.id.more_button);

            viewHolder.mMoreButton.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(getContext(), viewHolder.mMoreButton);
                popup.inflate(R.menu.device_menu);

                ArrayList<ServerInfo> serverList = mConnectionFragment.getNetworkList();
                ServerInfo serverInfo = serverList.get(position);
                Log.e("DEMO", serverInfo.getServerInfo());

                if (!serverInfo.isMemorized()) {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                }

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.connect:
                            mConnectionFragment.showDeviceConnectionDialog(serverInfo, false);
                            return true;
                        case R.id.rename:
                            mConnectionFragment.showDeviceRenameDialog(serverInfo);
                            return true;
                        case R.id.remove:
                            mConnectionFragment.showDeviceRemoveDialog(serverInfo);
                            return true;
                        default:
                            return false;
                    }
                });
                popup.show();
            });

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<ServerInfo> serverList = mConnectionFragment.getNetworkList();
        if(!serverList.isEmpty()) {
            ServerInfo serverInfo = serverList.get(position);
            viewHolder.mTextView.setText(serverInfo.getServerInfo());

            if (serverInfo.isAlive()) {
                viewHolder.mBadgeStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.itemDeviceOnline)));
                viewHolder.mSubtitle.setText("Online");
                viewHolder.mSubtitle.setTextColor(ContextCompat.getColor(getContext(), R.color.itemDeviceOnline));
            } else {
                viewHolder.mBadgeStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.itemDeviceOffline)));
                viewHolder.mSubtitle.setText("Offline");
                viewHolder.mSubtitle.setTextColor(ContextCompat.getColor(getContext(), R.color.itemDeviceOffline));
            }
        }

        return convertView;
    }
}