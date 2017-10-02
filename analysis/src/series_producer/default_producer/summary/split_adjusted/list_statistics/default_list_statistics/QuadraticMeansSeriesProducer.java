package series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics;

import descriptive_statistics.ListDescriptiveStatistics;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;

import java.time.LocalDate;

public class QuadraticMeansSeriesProducer extends DefaultListStatisticsSeriesProducer {
    public QuadraticMeansSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public QuadraticMeansSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    protected ImmutableList<Double> deriveStatistics(ListDescriptiveStatistics listDescriptiveStatistics) {
        return new ImmutableList<>(listDescriptiveStatistics.getQuadraticMeans());
    }
}
