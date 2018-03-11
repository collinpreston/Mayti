package collin.mayti.stockSymbolDB;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by chpreston on 3/10/18.
 */

public class SymbolViewModel extends AndroidViewModel {
    private static List<String> symbolList;

    SymbolDatabase symbolDatabase;

    public SymbolViewModel(@NonNull Application application) {
        super(application);
        symbolDatabase = SymbolDatabase.getDatabase(this.getApplication());
    }

    public List<String> readAllSymbols() throws ExecutionException, InterruptedException {
        new SymbolViewModel.readAllSymbolsAsyncTask(symbolDatabase).execute().get();
        return symbolList;
    }

    private static class readAllSymbolsAsyncTask extends AsyncTask<String, Void, Void> {

        private SymbolDatabase db;

        readAllSymbolsAsyncTask(SymbolDatabase database) {
            db = database;
        }

        @Override
        protected Void doInBackground(String... strings) {
            symbolList = db.symbolDbDao().getAll();
            return null;
        }
    }
}
