package collin.mayti.splashScreen;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import collin.mayti.MainActivity;
import collin.mayti.applicationSettingsDB.SettingDatabase;
import collin.mayti.applicationSettingsDB.SettingObject;
import collin.mayti.applicationSettingsDB.SettingViewModel;
import collin.mayti.datacapture.DailyUpdateDataRetriever;
import collin.mayti.stockSymbolDB.SymbolDatabase;
import collin.mayti.watchlist.WatchlistViewModel;
import collin.mayti.watchlistDB.AppDatabase;
import collin.mayti.watchlistDB.Stock;

/**
 * Created by chpreston on 3/10/18.
 */

/**
 * This class will display a splash screen while checking if various updates need to be performed
 * which require database updates and downloading updated data from servers.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final String DAILY_WATCHLIST_NAME = "daily_watchlist";
    private static final String WEEKLY_WATCHLIST_NAME = "weekly_watchlist";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeAllDatabases();

        try {
            // Need to set a timeout amount for this.
            checkSymbolDatabaseLastUpdate();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * This method checks to the settingdatabase to see if the symboldatabase has been updated today.
     * If the symbol database has not been updated today, this method will update the database.
     */
    private void checkSymbolDatabaseLastUpdate() throws ExecutionException, InterruptedException, ParseException {

        SettingViewModel viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);

        // Today's date
        String currentDateString = DateFormat.getDateInstance().format(new java.util.Date());


        int totalNumberOfRowsInSettings = viewModel.getTotalNumberOfSettings();

        // Check to see if the settingsdatabase is empty.  If it's empty, then this is the first
        // time the application has been opened.  We need to populate all of the settings and update
        // the symbol database and corresponding setting.
        if (!(totalNumberOfRowsInSettings ==  0)) {
            SettingObject symbolDatabaseLastUpdate =
                    viewModel.readSetting("SYMBOL_DATABASE_LAST_UPDATE");
            if (symbolDatabaseLastUpdate.getSettingValue().equals("")) {
                updateSymbolDatabaseAndSetting(currentDateString, viewModel);
            } else {
                Date settingValue = DateFormat.getDateInstance().parse(symbolDatabaseLastUpdate.getSettingValue());
                Date currentDate = DateFormat.getDateInstance().parse(currentDateString);
                if (settingValue.before(currentDate)) {
                    updateSymbolDatabaseAndSetting(currentDateString, viewModel);

                }
            }
        }
        else {
            populateSettingDatabase(viewModel);
            updateSymbolDatabaseAndSetting(currentDateString, viewModel);
        }
    }

    private void updateSymbolDatabaseAndSetting(String currentDateString, SettingViewModel viewModel)
            throws ExecutionException, InterruptedException {
        DailyUpdateDataRetriever dailyUpdateDataRetriever = new DailyUpdateDataRetriever(this.getBaseContext());
        dailyUpdateDataRetriever.execute().get();

        // Update the setting value to today's date.
        SettingObject settingObject = new SettingObject();
        settingObject.setSettingID("SYMBOL_DATABASE_LAST_UPDATE");
        settingObject.setSettingValue(currentDateString);
        viewModel.updateSetting(settingObject);
    }

    /**
     * This method populates the settingdatabase with all of the settingID values needed.  This is only
     * called if the app is being opened for the first time.
     * @param viewModel
     */
    private void populateSettingDatabase(SettingViewModel viewModel) {
        addSettingToSettingDatabase(viewModel, "SYMBOL_DATABASE_LAST_UPDATE");
    }

    private void addSettingToSettingDatabase(SettingViewModel viewModel, String settingID) {
        SettingObject settingObject = new SettingObject();
        settingObject.setSettingID(settingID);
        settingObject.setSettingValue("");
        viewModel.addItem(settingObject);
    }

    /**
     * This method will check to see if the watchlist database has been cleaned for expired watchlist
     * items today.  If the watchlist database has not already been cleaned today, it will clean the
     * watchlist database of expired watchlist items.
     */
    private void cleanWatchlistDatabase() throws ExecutionException, InterruptedException {
        WatchlistViewModel viewModel = ViewModelProviders.of(this).get(WatchlistViewModel.class);

        // First check the daily stocks.
        List<Stock> stockList = viewModel.getAllStocksForWatchlist("DAILY_WATCHLIST_NAME");

        // TODO

    }

    private void initializeAllDatabases() {
        // Initialize database
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        // Initialize symbol database
        SymbolDatabase symbolDb = SymbolDatabase.getDatabase(getApplicationContext());

        // Initialize setting database
        SettingDatabase settingDatabase = SettingDatabase.getDatabase(getApplicationContext());
    }


}
