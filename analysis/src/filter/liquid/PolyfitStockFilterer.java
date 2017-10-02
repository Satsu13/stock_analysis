package filter.liquid;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import repository.stock_repository.StockRepository;
import series_producer.Series;
import series_producer.default_producer.summary.split_adjusted.SplitAdjustedSeriesProducer;

import java.time.LocalDate;

public class PolyfitStockFilterer extends LiquidStockFilter {
    public PolyfitStockFilterer(StockRepository repository) {
        super(repository);
    }

    @Override
    protected int getTimeoutInMinutes() {
        return 10;
    }

    @Override
    public boolean shouldAccept(String ticker) {
        Series splitAdjustedTradeHistory = getSplitAdjustedTradeHistory(ticker);
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (LocalDate date : splitAdjustedTradeHistory.keySet()) {
            Double amount = splitAdjustedTradeHistory.get(date);
            points.add(date.toEpochDay(), amount);
        }
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
        double[] coefficents = fitter.fit(points.toList());
        return coefficents[0] > 0.0;

    }

    private Series getSplitAdjustedTradeHistory(String ticker) {
        SplitAdjustedSeriesProducer splitAdjustedSeriesProducer = new SplitAdjustedSeriesProducer(repository);
        return splitAdjustedSeriesProducer.produceSeries(ticker).get(0);
    }
}
