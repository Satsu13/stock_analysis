package series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics;

import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limit;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class QuadraticMeansSlopeSeriesProducer extends QuadraticMeansSeriesProducer {
    public QuadraticMeansSlopeSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public QuadraticMeansSlopeSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<Double> deriveStatistics(List<Double> originalValues, Number... arguments) {
        List<Double> loggedSeries = new LinkedList<>();
        for (Double number : originalValues) {
            loggedSeries.add(Math.log(number));
        }
        List<Double> quadraticMeans = super.deriveStatistics(loggedSeries, arguments);
        int slopeWindowSize = getSlopeWindowSize(arguments);
        return deriveSlopes(quadraticMeans, slopeWindowSize);
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

    protected int getSlopeWindowSize(Number... arguments) {
        Number slopeWindowSize = arguments[1];
        return slopeWindowSize.intValue();
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limit limit = new Limit(1, 1000);
        ExpectedArgument meansWindowSize = new ExpectedArgument("Means Window Size", limit, 20);
        limit = new Limit(1, 1000);
        ExpectedArgument slopeWindowSize = new ExpectedArgument("Slope Window Size", limit, 100);
        return new ImmutableList<>(meansWindowSize, slopeWindowSize);
    }


}
