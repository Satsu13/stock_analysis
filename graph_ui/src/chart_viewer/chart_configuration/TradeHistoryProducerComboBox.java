package chart_viewer.chart_configuration;


import javafx.scene.control.ComboBox;
import repository.stock_repository.StockRepository;
import series_producer.SeriesProducer;
import series_producer.default_producer.DefaultSeriesProducer;
import series_producer.default_producer.VolumeSeriesProducer;
import series_producer.default_producer.summary.SummarySeriesProducer;
import series_producer.default_producer.summary.split_adjusted.KalmanSeriesProducer;
import series_producer.default_producer.summary.split_adjusted.KalmanSeriesProducertest;
import series_producer.default_producer.summary.split_adjusted.PolyfitSeriesProducer;
import series_producer.default_producer.summary.split_adjusted.SplitAdjustedSeriesProducer;
import series_producer.default_producer.summary.split_adjusted.list_statistics.SlopeSeriesProducer;
import series_producer.default_producer.summary.split_adjusted.list_statistics.default_list_statistics.*;

public class TradeHistoryProducerComboBox extends ComboBox<SeriesProducer> {
    public TradeHistoryProducerComboBox(StockRepository repository) {
        initItems(repository);
        setValue(getItems().get(0));
    }

    private void initItems(StockRepository repository) {
        getItems().add(new VolumeSeriesProducer(repository));
        getItems().add(new DefaultSeriesProducer(repository));
        getItems().add(new SummarySeriesProducer(repository));
        getItems().add(new SplitAdjustedSeriesProducer(repository));
        getItems().add(new QuadraticMeansSeriesProducer(repository));
        getItems().add(new MaxsSeriesProducer(repository));
        getItems().add(new MinsSeriesProducer(repository));
        getItems().add(new MaxsQuadraticMeansSeriesProducer(repository));
        getItems().add(new MinsQuadraticMeansSeriesProducer(repository));
        getItems().add(new SlopeSeriesProducer(repository));
        getItems().add(new QuadraticMeansSlopeSeriesProducer(repository));
        getItems().add(new PolyfitSeriesProducer(repository));
        getItems().add(new KalmanSeriesProducer(repository));
        getItems().add(new KalmanSeriesProducertest(repository));
    }
}
