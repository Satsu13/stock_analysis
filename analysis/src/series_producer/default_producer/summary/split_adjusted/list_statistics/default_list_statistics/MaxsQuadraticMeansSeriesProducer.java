package series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics;

import descriptive_statistics.ListDescriptiveStatistics;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limits;

import java.time.LocalDate;
import java.util.List;

public class MaxsQuadraticMeansSeriesProducer extends MaxsSeriesProducer {
    public MaxsQuadraticMeansSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public MaxsQuadraticMeansSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<Double> deriveStatistics(List<Double> originalValues, Number... arguments) {
        ImmutableList<Double> maxs = super.deriveStatistics(originalValues, arguments);
        int windowSize = getMeansWindowSize(arguments);
        ListDescriptiveStatistics listDescriptiveStatistics = new ListDescriptiveStatistics(maxs, windowSize);
        return new ImmutableList<>(listDescriptiveStatistics.getQuadraticMeans());
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limits limits = new Limits(1, 1000);
        ExpectedArgument meansWindowSize = new ExpectedArgument("Maxs Window Size", limits, 356);
        ExpectedArgument slopeWindowSize = new ExpectedArgument("Means Window Size", limits, 20);
        return new ImmutableList<>(meansWindowSize, slopeWindowSize);
    }

    protected int getMeansWindowSize(Number... arguments) {
        Number windowSize = arguments[1];
        return windowSize.intValue();
    }
}
