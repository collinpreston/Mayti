package collin.mayti.stockNewsDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StockNewsDbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Article> articles);

    @Query("SELECT * FROM news")
    List<Article> getAll();

    @Query("SELECT * FROM news WHERE symbol LIKE :symbol")
    List<Article> findAllArticlesBySymbol(String symbol);

    @Query("SELECT COUNT(*) FROM news WHERE symbol LIKE :symbol")
    int getTotalArticlesBySymbol(String symbol);
}
