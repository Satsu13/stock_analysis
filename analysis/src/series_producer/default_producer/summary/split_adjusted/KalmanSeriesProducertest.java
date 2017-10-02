package series_producer.default_producer.summary.split_adjusted;

import ezkalman.EZProcessModel;
import ezkalman.InitialProcessEstimate;
import ezkalman.ProcessEquation;
import immutable.ImmutableList;
import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import repository.stock_repository.StockRepository;
import series_producer.ExpectedArgument;
import series_producer.Limits;
import series_producer.Series;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KalmanSeriesProducertest extends SplitAdjustedSeriesProducer {
    public KalmanSeriesProducertest(StockRepository repository) {
        super(repository);
    }

    public KalmanSeriesProducertest(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public ImmutableList<ExpectedArgument> getExpectedArguments() {
        Limits limits = new Limits(1, 1000000);
//        Limits limits = new Limits(1, 700);
//        Limits limits = new Limits(0, 1000000000);
        ExpectedArgument measurementNoise = new ExpectedArgument("Measurement Noise", limits, 1);
        limits = new Limits(0, 700);
//        limits = new Limits(0, 100000);
        ExpectedArgument velocityInclusionDenominator = new ExpectedArgument("Velocity Inclusion Denominator", limits, 1);
        ExpectedArgument noise1 = new ExpectedArgument("noise1", limits, 1);
        ExpectedArgument noise2 = new ExpectedArgument("noise2", limits, 1);
        ExpectedArgument noise3 = new ExpectedArgument("noise3", limits, 1);
        ExpectedArgument noise4 = new ExpectedArgument("noise4", limits, 1);
        return new ImmutableList<>(measurementNoise, velocityInclusionDenominator, noise1, noise2, noise3, noise4);
    }

    @Override
    public List<Series> produceSeries(String ticker, Number... arguments) {
        Series splitAdjustedSeries = super.produceSeries(ticker).get(0);
        Double firstValue = new ArrayList<>(splitAdjustedSeries.values()).get(0);
        Double measurementNoise = getMeasurementNoise(arguments);
        Double velocityInclusion = getVelocityInclusion(arguments);
        Double[] noise = getNoise(arguments);
        KalmanFilter filter = buildKalmanFilter(firstValue, velocityInclusion, measurementNoise, noise);
        return applyKalmanFilter(splitAdjustedSeries, filter);
    }

    private Double getMeasurementNoise(Number[] arguments) {
        return arguments[0].doubleValue();
    }

    private Double getVelocityInclusion(Number[] arguments) {
        return 1.0 / arguments[1].doubleValue();
    }

    private Double[] getNoise(Number[] arguments) {
        Double[] noise = new Double[4];
        for (int i = 0; i < 4; i++) {
            noise[i] = 1.0 / arguments[i + 2].doubleValue();
        }
        return noise;
    }


    private KalmanFilter buildKalmanFilter(Double firstValue, Double velocityInclusion, Double measurementNoise, Double[] noise) {
        ProcessModel processModel = buildProcessModel(firstValue, velocityInclusion, noise);
        MeasurementModel measurementModel = buildMeasurementModel(measurementNoise);
        return new KalmanFilter(processModel, measurementModel);
    }

    private ProcessModel buildProcessModel(Double firstValue, Double velocityInclusion, Double[] noise) {
        ProcessEquation processEquation = buildProcessEquation(velocityInclusion, noise);
        InitialProcessEstimate initialProcessEstimate = buildInitialProcessEstimate(firstValue);
        return new EZProcessModel(processEquation, initialProcessEstimate);
    }

    private InitialProcessEstimate buildInitialProcessEstimate(Double firstValue) {
        double[] initialState = new double[] { firstValue, 0 };
        double[][] initialErrorCovariance = new double[][] { { 1, 1 }, { 1, 1 } };
        return new InitialProcessEstimate(initialState, initialErrorCovariance);
    }

    private ProcessEquation buildProcessEquation(Double velocityInclusion, Double[] noise) {
        RealMatrix stateTransitionMatrix = new Array2DRowRealMatrix(new double[][] {
                {1, velocityInclusion },
                {0, 1}
        });
        RealMatrix controlMatrix = new Array2DRowRealMatrix(new double[][] {
                { 0 },
                { 0 }
        });
//        RealMatrix processNoiseMatrix = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });
        RealMatrix processNoiseMatrix = new Array2DRowRealMatrix(new double[][] {
                { noise[0], noise[1] },
                { noise[2], noise[3] }
        });
        return new ProcessEquation(stateTransitionMatrix, controlMatrix, processNoiseMatrix);
    }

    private MeasurementModel buildMeasurementModel(Double measurementNoise) {
        RealMatrix measurementMatrix = new Array2DRowRealMatrix(new double[][] { { 1, 0 } });
        RealMatrix measurementNoiseMatrix = new Array2DRowRealMatrix(new double[] { measurementNoise });
        return new DefaultMeasurementModel(measurementMatrix, measurementNoiseMatrix);
    }

    private List<Series> applyKalmanFilter(Series superSeries, KalmanFilter filter) {
        Series filteredSeries = new Series(superSeries.name, superSeries.ticker, superSeries.yAxisUnits);
        for (LocalDate date : superSeries.keySet()) {
            Double measuredValue = superSeries.get(date);
            Double estimate = estimateNextValue(filter, measuredValue);
            filteredSeries.put(date, estimate);
        }
        return new ImmutableList<>(filteredSeries);
    }

    private Double estimateNextValue(KalmanFilter filter, Double measuredValue) {
        filter.predict();
        filter.correct(new double[] { measuredValue });
        return filter.getStateEstimation()[0];
    }
}
