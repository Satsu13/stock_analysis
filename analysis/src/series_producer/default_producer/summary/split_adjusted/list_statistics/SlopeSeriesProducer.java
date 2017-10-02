package series_producer.default_producer.summary.split_adjusted.list_statistics;

import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limits;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class SlopeSeriesProducer extends ListStatisticsSeriesProducer {
    public SlopeSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public SlopeSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<Double> deriveStatistics(List<Double> originalValues, Number... arguments) {
        int slopeWindowSize = getWindowSize(arguments);
        return deriveSlopes(originalValues, slopeWindowSize);
    }

    private ImmutableList<Double> deriveSlopes(List<Double> quadraticMeans, int slopeWindowSize) {
        LinkedList<Double> slopes = new LinkedList<>();
        for (int i = slopeWindowSize; i < quadraticMeans.size(); i++) {
            Double slope = deriveSlope(quadraticMeans, slopeWindowSize, i);
            slopes.add(slope);
        }
        return new ImmutableList<>(slopes);
    }

    private Double deriveSlope(List<Double> quadraticMeans, int slopeWindowSize, int i) {
        Double current = quadraticMeans.get(i);
        Double previous = quadraticMeans.get(i - slopeWindowSize);
        return (current - previous) / slopeWindowSize;
    }

    protected int getWindowSize(Number... arguments) {
        return arguments[0].intValue();
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limits limits = new Limits(1, 1000);
        ExpectedArgument windowSize = new ExpectedArgument("Window Size", limits, 1);
        return new ImmutableList<>(windowSize);
    }
}
