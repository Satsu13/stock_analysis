package investment_simulator;

import filter.StockFilterer;
import filter.liquid.LiquidStockFilter;
import immutable.ImmutableList;
import investment_simulator.report.SimulationReport;
import javafx.beans.property.SimpleDoubleProperty;
import repository.stock_repository.StockRepository;
import series_producer.Series;
import series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics.QuadraticMeansSlopeSeriesProducer;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;

public class SlopeInvestmentSimulator extends InvestmentSimulator {
    private int meansWindowSize;
    private int slopeWindowSize;
    private int slopeMeansWindowSizeInDays;
    private SimpleDoubleProperty buySlopeThreshold;
    private SimpleDoubleProperty sellSlopeThreshold;

    public SlopeInvestmentSimulator(StockRepository repository, int meansWindowSize, int slopeWindowSize) {
        super(repository);
        this.meansWindowSize = meansWindowSize;
        this.slopeWindowSize = slopeWindowSize;
        initDefaults();
    }

    private void initDefaults() {
        buySlopeThreshold = new SimpleDoubleProperty(0.001);
        sellSlopeThreshold = new SimpleDoubleProperty(-0.001);
        slopeMeansWindowSizeInDays = 365 * 10;
    }

    public double getBuySlopeThreshold() {
        return buySlopeThreshold.get();
    }

    public void setBuySlopeThreshold(double buySlopeThreshold) {
        this.buySlopeThreshold.set(buySlopeThreshold);
    }

    public double getSellSlopeThreshold() {
        return sellSlopeThreshold.get();
    }

    public void setSellSlopeThreshold(double sellSlopeThreshold) {
        this.sellSlopeThreshold.set(sellSlopeThreshold);
    }

    public int getSlopeMeansWindowSizeInDays() {
        return slopeMeansWindowSizeInDays;
    }

    public void setSlopeMeansWindowSizeInDays(int slopeMeansWindowSizeInDays) {
        this.slopeMeansWindowSizeInDays = slopeMeansWindowSizeInDays;
    }

    @Override
    protected StockFilterer getStockFilterer() {
        return new LiquidStockFilter(repository);
    }

    @Override
    protected List<Series> preloadSeries(StockRepository repository, String ticker) {
        QuadraticMeansSlopeSeriesProducer producer = new QuadraticMeansSlopeSeriesProducer(repository);
        List<Series> quadraticMeansSlopeSeries = producer.produceSeries(ticker, meansWindowSize, slopeWindowSize);
        Series series = renameSeries(quadraticMeansSlopeSeries, "Slopes");
        return new ImmutableList<>(series);
    }

    private Series renameSeries(List<Series> seriesCollection, String newName) {
        Series series = seriesCollection.get(0);
        return new Series(series, series.ticker, newName, series.yAxisUnits);
    }

    @Override
    protected boolean shouldBuy(List<Series> preLoadedSeries, LocalDate currentDate, String ticker) {
        try {
            return tryDecidingToBuy(preLoadedSeries, currentDate);
        } catch (IndexOutOfBoundsException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean tryDecidingToBuy(List<Series> generatedSeries, LocalDate currentDate) {
        LocalDate minimumDate = currentDate.minus(Period.ofDays(slopeMeansWindowSizeInDays));
        return averageSlopeIsGreaterThan(getSlopeSeries(generatedSeries), buySlopeThreshold.get(), minimumDate);
    }

    private boolean averageSlopeIsGreaterThan(Series slopeSeries, double minimumAverage, LocalDate minimumDate) {
        LinkedList<LocalDate> keys = new LinkedList<>(slopeSeries.keySet());
        while (keys.get(0).isBefore(minimumDate)) {
            keys.pop();
        }
        double total = 0;
        for (LocalDate date : keys) {
            double next = slopeSeries.get(date);
            total += next;
        }
        return (total / ((double) keys.size())) > minimumAverage;
    }

    private Series getSlopeSeries(List<Series> generatedSeries) {
        return generatedSeries.get(0);
    }

    @Override
    protected boolean shouldSell(List<Series> generatedSeries, LocalDate currentDate, String ticker) {
        try {
            return tryDecidingToSell(generatedSeries, currentDate);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean tryDecidingToSell(List<Series> generatedSeries, LocalDate currentDate) {
        LocalDate minimumDate = currentDate.minus(Period.ofDays(slopeMeansWindowSizeInDays));
        return !averageSlopeIsGreaterThan(getSlopeSeries(generatedSeries), sellSlopeThreshold.get(), minimumDate);
    }

    @Override
    public SimulationReport simulate() {
        validate();
        return super.simulate();
    }

    private void validate() {
        if (buySlopeThreshold.get() <= sellSlopeThreshold.get()) {
            throw new IllegalStateException("The buy slope threshold ("
                    + buySlopeThreshold.get()
                    + ") must be larger than the sell slope threshold ("
                    + sellSlopeThreshold.get() + ").");
        }
    }
}
