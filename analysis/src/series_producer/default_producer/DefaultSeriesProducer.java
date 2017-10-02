package series_producer.default_producer;

import data.stock_history.trade_history.TradeHistory;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Series;
import series_producer.SeriesProducer;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class DefaultSeriesProducer extends SeriesProducer {
    private LocalDate startDate;
    private LocalDate endDate;

    public DefaultSeriesProducer(StockRepository repository) {
        this(repository, LocalDate.MIN, LocalDate.MAX);
    }

    public DefaultSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public List<Series> produceSeries(String ticker, Number... arguments) {
        TradeHistory tradeHistory = produceTradeHistory(ticker);
        return produceSeries(tradeHistory, arguments);
    }

    protected TradeHistory produceTradeHistory(String ticker) {
        return repository.getTradeHistoryDump().read(ticker);
    }

    protected ImmutableList<Series> produceSeries(TradeHistory tradeHistory, Number... arguments) {
        LinkedList<Series> series = new LinkedList<>();
        series.add(buildSeries(tradeHistory, tradeHistory.getTicker() + " - High", tradeHistory::getHigh));
        series.add(buildSeries(tradeHistory, tradeHistory.getTicker() + " - Open", tradeHistory::getOpen));
        series.add(buildSeries(tradeHistory, tradeHistory.getTicker() + " - Close", tradeHistory::getClose));
        series.add(buildSeries(tradeHistory, tradeHistory.getTicker() + " - Low", tradeHistory::getLow));
        return new ImmutableList<>(series);
    }

    protected Series buildSeries(TradeHistory tradeHistory, String name, Function<LocalDate, Number> populator) {
        Series series = new Series(tradeHistory.getTicker(), name, getYAxisUnits());
        tradeHistory.getDates().stream()
                .filter(date -> date.isAfter(startDate) && date.isBefore(endDate))
                .forEach(date -> {
            Double value = populator.apply(date).doubleValue();
            series.put(date, value);
        });
        return series;
    }

    protected String getYAxisUnits() {
        return "USD";
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        return new ImmutableList<>();
    }
}
