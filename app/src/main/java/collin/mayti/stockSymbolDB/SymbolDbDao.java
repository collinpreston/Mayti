package collin.mayti.stockSymbolDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by chpreston on 3/7/18.
 */

@Dao
public interface SymbolDbDao {

    @Query("SELECT symbol FROM symbols")
    List<String> getAll();

    @Query("SELECT * FROM symbols WHERE symbol LIKE :symbol LIMIT 1")
    Symbol findBySymbol(String symbol);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Symbol> symbols);

}
