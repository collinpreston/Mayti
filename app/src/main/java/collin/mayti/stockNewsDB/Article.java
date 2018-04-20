package collin.mayti.stockNewsDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(tableName = "news", indices = {@Index(value = "symbol")}, primaryKeys = {"symbol", "headline"})
public class Article {

    @NonNull
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NonNull String symbol) {
        this.symbol = symbol;
    }

    public void setHeadline(@NonNull String headline) {
        this.headline = headline;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @NonNull
    private String symbol;

    @NonNull
    public String getHeadline() {
        return headline;
    }

    @NonNull
    @ColumnInfo(name = "headline")
    private String headline;

    @ColumnInfo(name = "dateTime")
    private String dateTime;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "source")
    private String source;
}
