package collin.mayti.notifications;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.R;
import collin.mayti.notifications.NotificationsDatabase.Notification;
import collin.mayti.notifications.NotificationsDatabase.NotificationsDatabaseViewModel;

public class NotificationsListViewAdapter extends BaseAdapter{

    private List<Notification> notifications = new ArrayList<>();
    private Activity mActivity;
    private Context mContext;

    public NotificationsListViewAdapter(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;

        try {
            notifications = getNotifications();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.notification_list_item, parent, false);

        TextView notificationTitle = row.findViewById(R.id.notificationTitleTxt);
        notificationTitle.setText(notifications.get(position).getNotificationTitle());


        TextView notificationText = row.findViewById(R.id.notificationTextTxt);
        notificationText.setText(notifications.get(position).getNotificationText());


        TextView notificationDate = row.findViewById(R.id.notificationDate);
        notificationDate.setText(notifications.get(position).getDateTriggered().toString());

        return row;
    }

    private List<Notification> getNotifications() throws ExecutionException, InterruptedException {
        NotificationsDatabaseViewModel notificationsDatabaseViewModel = ViewModelProviders.of((FragmentActivity) mActivity).get(NotificationsDatabaseViewModel.class);
        List<Notification> notificationsList = notificationsDatabaseViewModel.getAllNotifications();
        return notificationsList;
    }
}
