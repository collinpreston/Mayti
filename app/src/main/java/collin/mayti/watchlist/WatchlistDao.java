package collin.mayti.watchlist;

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
    @Query("SELECT * FROM stock")
    List<Stock> getAll();

    @Query("SELECT * FROM stock WHERE symbol LIKE :symbol LIMIT 1")
    Stock findBySymbol(String symbol);

    @Insert
    void insertAll(Stock... stocks);

    @Delete
    void delete(Stock stock);
}
