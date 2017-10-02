package split;

import data.stock_history.split_history.SplitHistory;
import data.stock_history.trade_history.TradeHistory;
import data.stock_history.trade_history.TradeHistoryBean;

import java.time.LocalDate;
import java.util.LinkedList;

public class SplitAdjuster {
    public static TradeHistory buildAdjustedTradeHistory(TradeHistory tradeHistory, SplitHistory splitHistory) {
        SplitAdjuster splitAdjuster = new SplitAdjuster(tradeHistory, splitHistory);
        return splitAdjuster.adjustedTradeHistory;
    }

    private TradeHistory tradeHistory;
    private SplitHistory splitHistory;
    private TradeHistoryBean adjustedTradeHistoryBean;
    private LinkedList<LocalDate> splitDates;
    private TradeHistory adjustedTradeHistory;
    private double currentMultiplier;

    private SplitAdjuster(TradeHistory tradeHistory, SplitHistory splitHistory) {
        this.tradeHistory = tradeHistory;
        this.splitHistory = splitHistory;
        initAdjustedStockHistoryBean();
        adjustedTradeHistory = new TradeHistory(adjustedTradeHistoryBean);
    }

    private void initAdjustedStockHistoryBean() {
        adjustedTradeHistoryBean = new TradeHistoryBean(tradeHistory.getTicker());
        splitDates = new LinkedList<>(splitHistory.getDates());
        populateAdjustedBean();
    }

    private void populateAdjustedBean() {
        currentMultiplier = 1.0;
        for (LocalDate stockDate : tradeHistory.getDates()) {
            adjustForSplits(stockDate);
            populateAdjustedBean(stockDate, currentMultiplier);
        }
    }

    private void adjustForSplits(LocalDate stockDate) {
        while (anyPreviousSplitDates(stockDate)) {
            currentMultiplier *= calculateNextMultiplier();
        }
    }

    private boolean anyPreviousSplitDates(LocalDate stockDate) {
        if (splitDates.size() == 0) {
            return false;
        } else {
            LocalDate currentDate = splitDates.peek();
            return currentDate.isBefore(stockDate) || currentDate.isEqual(stockDate);
        }
    }

    private double calculateNextMultiplier() {
        LocalDate splitHistoryDate = splitDates.pop();
        return splitHistory.calculateMultiplier(splitHistoryDate);
    }

    private void populateAdjustedBean(LocalDate date, double multiplier) {
        adjustedTradeHistoryBean.dates.add(date);
        adjustedTradeHistoryBean.opens.put(date, tradeHistory.getOpen(date) * multiplier);
        adjustedTradeHistoryBean.highs.put(date, tradeHistory.getHigh(date) * multiplier);
        adjustedTradeHistoryBean.lows.put(date, tradeHistory.getLow(date) * multiplier);
        adjustedTradeHistoryBean.closes.put(date, tradeHistory.getClose(date) * multiplier);
        adjustedTradeHistoryBean.volumes.put(date, tradeHistory.getVolume(date));
    }
}
