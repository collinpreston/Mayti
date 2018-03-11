package collin.mayti.applicationSettingsDB;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;



/**
 * Created by chpreston on 3/10/18.
 */

public class SettingViewModel extends AndroidViewModel {

    private static SettingObject settingObject;
    private static List<SettingObject> settingObjectList;

    private static int totalNumberOfRowsForSettings;

    private SettingDatabase settingDatabase;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        settingDatabase = SettingDatabase.getDatabase(this.getApplication());

    }


    public void addItem(SettingObject setting) {
        new SettingViewModel.addAsyncTask(settingDatabase).execute(setting);
    }

    private static class addAsyncTask extends AsyncTask <SettingObject, Void, Void> {

        private SettingDatabase db;

        addAsyncTask(SettingDatabase settingDatabase) {
            db = settingDatabase;
        }

        @Override
        protected Void doInBackground(final SettingObject... params) {
            db.settingDbDao().insert(params[0]);
            return null;
        }

    }

    public void updateSetting(SettingObject setting) {
        new updateAsyncTask(settingDatabase).execute(setting);
    }

    private static class updateAsyncTask extends AsyncTask<SettingObject, Void, Void> {

        private SettingDatabase db;

        updateAsyncTask(SettingDatabase settingDatabase) {
            db = settingDatabase;
        }

        @Override
        protected Void doInBackground(SettingObject... settingObjects) {
            db.settingDbDao().updateSetting(settingObjects);
            return null;
        }
    }

    public SettingObject readSetting(String settingID) throws ExecutionException, InterruptedException {
        new readSettingAsyncTask(settingDatabase, settingID).execute(settingID).get();
        return settingObject;
    }

    private static class readSettingAsyncTask extends AsyncTask<String, Void, Void> {

        private SettingDatabase db;
        private String mSettingID;

        readSettingAsyncTask(SettingDatabase settingDatabase, String settingID) {
            db = settingDatabase;
            mSettingID = settingID;
        }

        @Override
        protected Void doInBackground(String... settingID) {
            settingObject = db.settingDbDao().findBySettingID(mSettingID);
            return null;
        }

    }

    public List<SettingObject> readAllSettings() {
        new readAllSettingsAsyncTask(settingDatabase).execute();
        return settingObjectList;
    }

    private static class readAllSettingsAsyncTask extends AsyncTask<String, Void, Void> {

        private SettingDatabase db;

        readAllSettingsAsyncTask(SettingDatabase settingDatabase) {
            db = settingDatabase;
        }

        @Override
        protected Void doInBackground(String... strings) {
            settingObjectList = db.settingDbDao().getAll();
            return null;
        }
    }

    public int getTotalNumberOfSettings() throws ExecutionException, InterruptedException {
        new SettingViewModel.getTotalNumberOfSettingsAsyncTask(settingDatabase).execute().get();
        return totalNumberOfRowsForSettings;
    }
    private static class getTotalNumberOfSettingsAsyncTask extends AsyncTask<String, Void, Void> {

        private SettingDatabase db;

        getTotalNumberOfSettingsAsyncTask(SettingDatabase settingDatabase) {
            db = settingDatabase;
        }

        @Override
        protected Void doInBackground(String... setting) {
            try {
                totalNumberOfRowsForSettings = db.settingDbDao().getTotalNumberOfRows();
            } catch (Exception e) {
                totalNumberOfRowsForSettings = 0;
            }
            return null;
        }
    }
}
