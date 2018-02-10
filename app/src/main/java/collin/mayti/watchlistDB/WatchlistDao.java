package collin.mayti.watchlistDB;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Collin on 1/15/2018.
 */

@Dao
public interface WatchlistDao {
    @Query("SELECT * FROM watchlist")
    LiveData<List<Stock>> getAll();

    @Query("SELECT * FROM watchlist WHERE symbol LIKE :symbol LIMIT 1")
    Stock findBySymbol(String symbol);

    @Insert
    void insertAll(Stock... stocks);

    @Insert
    void insertStock(Stock stock);

    @Delete
    void delete(Stock stock);

    @Query("UPDATE watchlist SET price = :price, volume = :volume WHERE symbol = :symbol")
    public void updateBySymbol(String price, String volume, String symbol);
}
