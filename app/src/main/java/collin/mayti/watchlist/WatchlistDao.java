package collin.mayti.watchlist;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.HashMap;
import java.util.List;

import collin.mayti.stock.StockContent;

/**
 * Created by Collin on 1/15/2018.
 */

@Dao
public interface WatchlistDao {
    @Query("SELECT * FROM watchlist")
    List<Stock> getAll();

    @Query("SELECT * FROM watchlist WHERE symbol LIKE :symbol LIMIT 1")
    Stock findBySymbol(String symbol);

    @Insert
    void insertAll(Stock... stocks);

    @Delete
    void delete(Stock stock);

    @Query("UPDATE watchlist SET price = :price, volume = :volume WHERE symbol = :symbol")
    public void updateBySymbol(String price, String volume,
                                                                            String symbol);
}
