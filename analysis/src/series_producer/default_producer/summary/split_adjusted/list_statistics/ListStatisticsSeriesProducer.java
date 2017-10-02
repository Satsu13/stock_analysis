package series_producer.default_producer.summary.split_adjusted.list_statistics;

import data.stock_history.trade_history.TradeHistory;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.Series;
import series_producer.default_producer.summary.split_adjusted.SplitAdjustedSeriesProducer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class ListStatisticsSeriesProducer extends SplitAdjustedSeriesProducer {
    public ListStatisticsSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public ListStatisticsSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<Series> produceSeries(TradeHistory tradeHistory, Number... arguments) {
        ImmutableList<Series> originalSeriesList = super.produceSeries(tradeHistory, arguments);
        ArrayList<Series> derivedSeriesList = new ArrayList<>(originalSeriesList.size());
        for (Series series : originalSeriesList) {
            Series derivedSeries = deriveSeries(series, arguments);
            derivedSeriesList.add(derivedSeries);
        }
        return new ImmutableList<>(derivedSeriesList);
    }

    private Series deriveSeries(Series series, Number... arguments) {
        List<Double> values = new ImmutableList<>(series.values());
        List<Double> derivedStatistics = deriveStatistics(values, arguments);
        return buildDerivedSeries(series, new ImmutableList<>(series.keySet()), derivedStatistics);
    }

    public abstract ImmutableList<Double> deriveStatistics(List<Double> originalValues, Number... arguments);

    private Series buildDerivedSeries(Series originalSeries, List<LocalDate> dates, List<Double> derivedStatistics) {
        Series newSeries = new Series(originalSeries.ticker, originalSeries.name, getYAxisUnits());
        int offset = dates.size() - derivedStatistics.size();
        for (int i = offset; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            Double value = derivedStatistics.get(i - offset);
            newSeries.put(date, value);
        }
        return newSeries;
    }
}
