package series_producer.default_producer;

import data.stock_history.trade_history.TradeHistory;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.Series;

import java.time.LocalDate;

public class VolumeSeriesProducer extends DefaultSeriesProducer {
    public VolumeSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public VolumeSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    protected ImmutableList<Series> produceSeries(TradeHistory tradeHistory, Number... arguments) {
        Series series = buildSeries(tradeHistory, "", tradeHistory::getVolume);
        return new ImmutableList<>(series);
    }
}
