package collin.mayti.watchlistDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.sql.Date;

/**
 * Created by Collin on 1/14/2018.
 */


@Entity(tableName = "watchlist", indices = {@Index(value = "symbol")}, primaryKeys = {"symbol", "watchlist"})
public class Stock {
    public int getPositionID() {
        return positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(String watchlist) {
        this.watchlist = watchlist;
    }

    public Date getDateToRemove() {
        return dateToRemove;
    }

    public void setDateToRemove(Date dateToRemove) {
        this.dateToRemove = dateToRemove;
    }

    public String getRecordHigh() {
        return recordHigh;
    }

    public void setRecordHigh(String recordHigh) {
        this.recordHigh = recordHigh;
    }

    public String getRecordLow() {
        return recordLow;
    }

    public void setRecordLow(String recordLow) {
        this.recordLow = recordLow;
    }

    public String getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(String averageVolume) {
        this.averageVolume = averageVolume;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(String latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    @NonNull
    private String symbol;

    @ColumnInfo(name = "positionID")
    private int positionID;

    @ColumnInfo(name = "price")
    private String price;

    @ColumnInfo(name = "volume")
    private String volume;

    @ColumnInfo(name = "change")
    private String change;

    @ColumnInfo(name = "changePercent")
    private String changePercent;

    @NonNull
    @ColumnInfo(name = "watchlist")
    private String watchlist;

    @ColumnInfo
    @TypeConverters(DateConverter.class)
    private Date dateToRemove;

    @ColumnInfo(name = "recordHigh")
    private String recordHigh;

    @ColumnInfo(name = "recordLow")
    private String recordLow;

    @ColumnInfo(name = "averageVolume")
    private String averageVolume;

    @ColumnInfo(name = "latestUpdate")
    private String latestUpdate;

}


