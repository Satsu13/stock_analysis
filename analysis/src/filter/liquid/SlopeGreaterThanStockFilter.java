package filter.liquid;

import repository.stock_repository.StockRepository;
import series_producer.Series;
import series_producer.SeriesProducer;
import series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics.QuadraticMeansSlopeSeriesProducer;

import java.time.LocalDate;
import java.util.Collection;

public class SlopeGreaterThanStockFilter extends LiquidStockFilter {

    private LocalDate startDate;
    private LocalDate endDate;
    private double minimumAverage;
    private double quadraticMeansWindowSize;
    private double slopeWindowSize;


    public SlopeGreaterThanStockFilter(StockRepository repository,
                                       double minimumAverage,
                                       double quadraticMeansWindowSize,
                                       double slopeWindowSize) {
        super(repository);
        this.minimumAverage = minimumAverage;
        this.quadraticMeansWindowSize = quadraticMeansWindowSize;
        this.slopeWindowSize = slopeWindowSize;
        startDate = LocalDate.MIN;
        endDate = LocalDate.MAX;
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
    public boolean shouldAccept(String ticker) {
        if (isLiquid(ticker)) {
            return averageSlopeIsGreater(ticker);
        } else {
            return false;
        }
    }

    public boolean averageSlopeIsGreater(String ticker) {
        SeriesProducer seriesProducer = new QuadraticMeansSlopeSeriesProducer(repository, startDate, endDate);
        Series slopes = seriesProducer.produceSeries(ticker, quadraticMeansWindowSize, slopeWindowSize).get(0);
        return averageSlopeIsGreater(slopes);
    }


    public boolean averageSlopeIsGreater(Series slopes) {
        return averageSlopeIsGreaterThan(slopes, minimumAverage);
    }

    public static boolean averageSlopeIsGreaterThan(Series slopes, double minimumAverage) {
        double average = calculateAverage(slopes.values());
        return average > minimumAverage;
    }

    private static Double calculateAverage(Collection<Double> slopes) {
        double total = calculateTotal(slopes);
        return total / slopes.size();
    }

    private static Double calculateTotal(Collection<Double> slopes) {
        double total = 0.0;
        for (Double slope : slopes) {
            total += slope;
        }
        return total;
    }
}
