package series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics;

import descriptive_statistics.ListDescriptiveStatistics;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limits;
import series_producer.default_producer.summary.split_adjusted.list_statistics.ListStatisticsSeriesProducer;

import java.time.LocalDate;
import java.util.List;

public abstract class DefaultListStatisticsSeriesProducer extends ListStatisticsSeriesProducer {
    public DefaultListStatisticsSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public DefaultListStatisticsSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limits limits = new Limits(1, 1000);
        ExpectedArgument windowSize = new ExpectedArgument("Window Size", limits, 100);
        return new ImmutableList<>(windowSize);
    }

    protected int getWindowSize(Number... arguments) {
        Number windowSize = arguments[0];
        return windowSize.intValue();
    }

    @Override
    public ImmutableList<Double> deriveStatistics(List<Double> originalValues, Number... arguments) {
        int windowSize = getWindowSize(arguments);
        ListDescriptiveStatistics listDescriptiveStatistics = new ListDescriptiveStatistics(originalValues, windowSize);
        return deriveStatistics(listDescriptiveStatistics);
    }

    protected abstract ImmutableList<Double> deriveStatistics(ListDescriptiveStatistics listDescriptiveStatistics);
}
