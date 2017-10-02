package series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics;

import descriptive_statistics.ListDescriptiveStatistics;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limit;

import java.time.LocalDate;
import java.util.List;

public class MinsQuadraticMeansSeriesProducer extends MinsSeriesProducer {
    public MinsQuadraticMeansSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public MinsQuadraticMeansSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<Double> deriveStatistics(List<Double> originalValues, Number... arguments) {
        ImmutableList<Double> mins = super.deriveStatistics(originalValues, arguments);
        int windowSize = getMeansWindowSize(arguments);
        ListDescriptiveStatistics listDescriptiveStatistics = new ListDescriptiveStatistics(mins, windowSize);
        return new ImmutableList<>(listDescriptiveStatistics.getQuadraticMeans());
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limit limit = new Limit(1, 1000);
        ExpectedArgument meansWindowSize = new ExpectedArgument("Mins Window Size", limit, 356);
        ExpectedArgument slopeWindowSize = new ExpectedArgument("Means Window Size", limit, 20);
        return new ImmutableList<>(meansWindowSize, slopeWindowSize);
    }

    protected int getMeansWindowSize(Number... arguments) {
        Number windowSize = arguments[1];
        return windowSize.intValue();
    }
}
