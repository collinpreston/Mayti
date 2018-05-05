package collin.mayti.alerts;

import android.app.Application;

import collin.mayti.alerts.alertSubscriptionDatabase.Alert;
import collin.mayti.stockNewsDB.StockNewsDatabase;
import collin.mayti.watchlistDB.AppDatabase;

public class AlertsUtil {
    private Application application;

    public AlertsUtil(Application app) {
        this.application = app;
    }

    public boolean priceCheck(Alert alert) {
        if (alert.getAlertType().equals("PRICE_CHANGE_PERCENT")) {
            AppDatabase stockDB = AppDatabase.getDatabase(this.application);
            String changePercent = stockDB.watchlistDao().findStockItemBySymbol(alert.getSymbol()).getChangePercent();
            double changePercentCurrent = Double.parseDouble(changePercent) * 100;
            double changePercentAlert = Double.parseDouble(alert.getAlertTriggerValue());
            if (changePercentAlert < 0) {
                if (changePercentAlert > changePercentCurrent) {
                    return true;
                } else {
                    return false;
                }
            }
            if (changePercentAlert > 0) {
                if (changePercentAlert < changePercentCurrent) {
                    return true;
                } else {
                    return false;
                }
            }
            if (changePercentAlert == changePercentCurrent) {
                return true;
            }
        }
        if (alert.getAlertType().equals("PRICE_CHANGE_PRICE")) {
            AppDatabase stockDB = AppDatabase.getDatabase(this.application);
            String changePrice = stockDB.watchlistDao().findStockItemBySymbol(alert.getSymbol()).getChange();
            double changePriceCurrent = Double.parseDouble(changePrice);
            double changePriceAlert = Double.parseDouble(alert.getAlertTriggerValue());
            if (changePriceAlert < 0) {
                if (changePriceAlert > changePriceCurrent) {
                    return true;
                } else {
                    return false;
                }
            }
            if (changePriceAlert > 0) {
                if (changePriceAlert < changePriceCurrent) {
                    return true;
                } else {
                    return false;
                }
            }
            if (changePriceAlert == changePriceCurrent) {
                return true;
            }
        }
        // TODO: Need to clear out these price target alerts after each day.  Implement this functionality
        // the the splash screen cleaner.
        if (alert.getAlertType().equals("PRICE_TARGET")) {
            AppDatabase stockDB = AppDatabase.getDatabase(this.application);
            String currentPrice = stockDB.watchlistDao().findStockItemBySymbol(alert.getSymbol()).getPrice();
            double currentPriceValue = Double.parseDouble(currentPrice);
            double alertPriceTarget = Double.parseDouble(alert.getAlertTriggerValue());
            String priceChangeAmount = stockDB.watchlistDao().findStockItemBySymbol(alert.getSymbol()).getChange();
            double priceChange = Double.parseDouble(priceChangeAmount);
            double openPrice = currentPriceValue - priceChange;
            if (alertPriceTarget > openPrice && currentPriceValue > alertPriceTarget) {
                return true;
            } else {
                if (alertPriceTarget < openPrice && currentPriceValue < alertPriceTarget) {
                    return true;
                } else {
                    if (alertPriceTarget == currentPriceValue) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean volumeCheck(Alert alert) {
        AppDatabase stockDB = AppDatabase.getDatabase(this.application);
        String currentVolumeString = stockDB.watchlistDao().findStockItemBySymbol(alert.getSymbol()).getVolume();
        int currentVolumeValue = Integer.parseInt(currentVolumeString);
        int alertTriggerValue = (int) Math.floor(Double.parseDouble(alert.getAlertTriggerValue()));
        if (currentVolumeValue >= alertTriggerValue) {
            return true;
        }

        return false;
    }

    public boolean stockNewsCheck(Alert alert) {
        StockNewsDatabase stockNewsDatabase = StockNewsDatabase.getDatabase(this.application);
        int totalArticlesPublishedToday = stockNewsDatabase.stockNewsDbDao().findAllArticlesBySymbol(alert.getSymbol()).size();
        int alertTriggerValue = Integer.parseInt(alert.getAlertTriggerValue());
        if (totalArticlesPublishedToday >= alertTriggerValue) {
            return true;
        }
        return false;
    }
}
