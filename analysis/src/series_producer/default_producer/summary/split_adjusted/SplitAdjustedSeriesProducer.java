package series_producer.default_producer.summary.split_adjusted;

import split.SplitAdjuster;
import data.stock_history.split_history.SplitHistory;
import data.stock_history.trade_history.TradeHistory;
import repository.stock_repository.StockRepository;
import series_producer.default_producer.summary.SummarySeriesProducer;

import java.time.LocalDate;
import java.util.Hashtable;

public class SplitAdjustedSeriesProducer extends SummarySeriesProducer {
    private Hashtable<String, TradeHistory> splitAdjustedTradeHistoryCache;

    public SplitAdjustedSeriesProducer(StockRepository repository) {
        super(repository);
        splitAdjustedTradeHistoryCache = new Hashtable<>();
    }

    public SplitAdjustedSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
        splitAdjustedTradeHistoryCache = new Hashtable<>();
    }

    @Override
    protected TradeHistory produceTradeHistory(String ticker) {
        if (!splitAdjustedTradeHistoryCache.containsKey(ticker)) {
            cacheNewSplitAdjustedTradeHistory(ticker);
        }
        return splitAdjustedTradeHistoryCache.get(ticker);
    }

    //TODO move split adjustment caluclations here
    private void cacheNewSplitAdjustedTradeHistory(String ticker) {
        TradeHistory tradeHistory = super.produceTradeHistory(ticker);
        SplitHistory splitHistory = repository.getSplitHistoryDump().read(ticker);
        TradeHistory adjustedTradeHistory = SplitAdjuster.buildAdjustedTradeHistory(tradeHistory, splitHistory);
        splitAdjustedTradeHistoryCache.put(ticker, adjustedTradeHistory);
    }
}
