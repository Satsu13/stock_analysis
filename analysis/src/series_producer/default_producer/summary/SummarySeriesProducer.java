package series_producer.default_producer.summary;

import data.stock_history.trade_history.TradeHistory;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Series;
import series_producer.default_producer.DefaultSeriesProducer;

import java.time.LocalDate;
import java.util.List;

public class SummarySeriesProducer extends DefaultSeriesProducer {
    public SummarySeriesProducer(StockRepository repository) {
        super(repository);
    }

    public SummarySeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    protected ImmutableList<Series> produceSeries(TradeHistory tradeHistory, Number... arguments) {
        Series series = new Series(tradeHistory.getTicker(), getName() + " - " + tradeHistory.getTicker(), getYAxisUnits());
        List<Series> defaultSeries = super.produceSeries(tradeHistory, arguments);
        for (LocalDate date : defaultSeries.get(0).keySet()) {
            double summarizedValue = summarizeValue(defaultSeries, date);
            series.put(date,summarizedValue);
        }
        return new ImmutableList<>(series);
    }

    protected double summarizeValue(List<Series> defaultValues, LocalDate date) {
        double high = defaultValues.get(0).get(date);
        double low = defaultValues.get(3).get(date);
        return (high + low) / 2.0;
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        return new ImmutableList<>();
    }
}
