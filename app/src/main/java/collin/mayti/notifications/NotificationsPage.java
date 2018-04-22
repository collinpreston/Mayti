package collin.mayti.notifications;

import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import collin.mayti.R;

public class NotificationsPage extends Fragment {

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_all_notifications, container, false);

        ListView notificationsListView = rootView.findViewById(R.id.notificationsList);
        NotificationsListViewAdapter notificationsListViewAdapter = new NotificationsListViewAdapter(this.getActivity(), this.getContext());
        notificationsListView.setAdapter(notificationsListViewAdapter);

        return rootView;
    }
}
