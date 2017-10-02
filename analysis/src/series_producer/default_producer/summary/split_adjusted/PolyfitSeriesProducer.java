package series_producer.default_producer.summary.split_adjusted;

import data.stock_history.trade_history.TradeHistory;
import immutable.ImmutableList;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limits;
import series_producer.Series;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class PolyfitSeriesProducer extends SplitAdjustedSeriesProducer {

    public PolyfitSeriesProducer(StockRepository repository) {
        super(repository);
    }

    public PolyfitSeriesProducer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    protected ImmutableList<Series> produceSeries(TradeHistory tradeHistory, Number... arguments) {
        List<Series> seriesList = super.produceSeries(tradeHistory, arguments);
        LinkedList<Series> fittedSeriesList = new LinkedList<>();
        for (Series series : seriesList) {
            Series fittedSeries = fitSeries(series, arguments);
            fittedSeriesList.add(fittedSeries);
        }
        return new ImmutableList<>(fittedSeriesList);
    }

    private Series fitSeries(Series series, Number[] arguments) {
        WeightedObservedPoints points = getValuesAsPoints(series);
        double[] coefficients = getCoefficients(points, arguments);
        List<Double> fittedValues = mapValues(series, coefficients);
        return mapSeries(series, fittedValues);
    }

    private WeightedObservedPoints getValuesAsPoints(Series series) {
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (LocalDate date : series.keySet()) {
            Double amount = series.get(date);
            long epochDay = date.toEpochDay();
            points.add(epochDay, amount);
        }
        return points;
    }

    private double[] getCoefficients(WeightedObservedPoints points, Number[] arguments) {
        int coefficientCount = getCoefficientCount(arguments);
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(coefficientCount);
        return fitter.fit(points.toList());
    }

    private List<Double> mapValues(Series series, double[] coefficients) {
        PolynomialFunction function = new PolynomialFunction(coefficients);
        List<Double> mappedValues = new LinkedList<>();
        for (LocalDate localDate : series.keySet()) {
            Double mappedValue = function.value(localDate.toEpochDay());
            mappedValues.add(mappedValue);
        }
        return mappedValues;
    }

    private Series mapSeries(Series series, List<Double> fittedValues) {
        Series fittedSeries = new Series(series.ticker, series.name, series.yAxisUnits);
        int currentIndex = 0;
        for (LocalDate date :series.keySet()) {
            fittedSeries.put(date, fittedValues.get(currentIndex));
            currentIndex++;
        }
        return fittedSeries;
    }

    protected int getCoefficientCount(Number... arguments) {
        return arguments[0].intValue();
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limits limits = new Limits(0, 25);
        ExpectedArgument coefficients = new ExpectedArgument("Number of Coefficients", limits, 1);
        return new ImmutableList<>(coefficients);
    }
}
