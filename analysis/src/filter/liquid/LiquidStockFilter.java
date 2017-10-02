package filter.liquid;

import data.stock_history.trade_history.TradeHistory;
import descriptive_statistics.ListDescriptiveStatistics;
import filter.DefaultStockFilterer;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.default_producer.VolumeSeriesProducer;

import java.time.Period;
import java.util.Hashtable;
import java.util.List;

public class LiquidStockFilter extends DefaultStockFilterer {
    public static final Period MINIMUM_HISTORY_LENGTH = Period.ofYears(10);
    public static final int MOVING_AVERAGE_WINDOW_SIZE = 500;
    public static final int LIQUIDITY_THRESHOLD = 60000;

    private static final Hashtable<StockRepository, Hashtable<String, Boolean>> acceptanceCache = new Hashtable<>();

    public LiquidStockFilter(StockRepository repository) {
        super(repository);
        cacheRepository(repository);
    }

    private void cacheRepository(StockRepository repository) {
        if (!acceptanceCache.containsKey(repository)) {
            acceptanceCache.put(repository, new Hashtable<>());
        }
    }

    public LiquidStockFilter(StockRepository repository, ImmutableList<String> tickersToFilter) {
        super(repository, tickersToFilter);
        cacheRepository(repository);
    }

    @Override
    public boolean shouldAccept(String ticker) {
        if (!acceptanceIsCached(ticker)) {
            cacheAcceptance(ticker);
        }
        return acceptanceCache.get(repository).get(ticker);
    }

    private boolean acceptanceIsCached(String ticker) {
        return acceptanceCache.get(repository).containsKey(ticker);
    }

    private void cacheAcceptance(String ticker) {
        boolean liquid = isLongEnough(ticker) && isLiquid(ticker);
        acceptanceCache.get(repository).put(ticker, liquid);
    }

    private boolean isLongEnough(String ticker) {
        TradeHistory tradeHistory = repository.getTradeHistoryDump().read(ticker);
        return getTradeHistoryLength(tradeHistory).getDays() > MINIMUM_HISTORY_LENGTH.getDays();
    }

    private Period getTradeHistoryLength(TradeHistory tradeHistory) {
        return tradeHistory.getDates().first().until(tradeHistory.getDates().last());
    }

    protected boolean isLiquid(String ticker) {
        List<Double> volumeMovingAverage = calculateVolumeMovingAverage(ticker);
        for (Double volumeAverage : volumeMovingAverage) {
            if (volumeAverage < LIQUIDITY_THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    private List<Double> calculateVolumeMovingAverage(String ticker) {
        VolumeSeriesProducer volumeSeriesProducer = new VolumeSeriesProducer(repository);
        List<Double> volumes = new ImmutableList<>(volumeSeriesProducer.produceSeries(ticker).get(0).values());
        ListDescriptiveStatistics calculator = new ListDescriptiveStatistics(volumes, MOVING_AVERAGE_WINDOW_SIZE);
        return calculator.getMeans();
    }
}
