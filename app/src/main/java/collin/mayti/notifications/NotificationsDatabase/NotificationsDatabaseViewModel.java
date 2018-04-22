package collin.mayti.notifications.NotificationsDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotificationsDatabaseViewModel extends AndroidViewModel {

    private NotificationsDatabase notificationsDatabase;

    private static List<Notification> notificationsList = new ArrayList<>();

    public NotificationsDatabaseViewModel(@NonNull Application application) {
        super(application);

        notificationsDatabase = NotificationsDatabase.getDatabase(this.getApplication());
    }

    public List<Notification> getAllNotifications() throws ExecutionException, InterruptedException {
        new NotificationsDatabaseViewModel.getAllNotificationsAsyncTask(notificationsDatabase).execute().get();
        return notificationsList;
    }

    private static class getAllNotificationsAsyncTask extends AsyncTask<String, Void, Void> {

        private NotificationsDatabase db;

        getAllNotificationsAsyncTask(NotificationsDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(String... watchlistName) {
            notificationsList = db.notificationsDbDao().getAll();
            return null;
        }
    }
}
