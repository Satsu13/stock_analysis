package chart_viewer.chart_configuration.series_configuration;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.GridPane;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import series_producer.SeriesProducer;

import java.util.LinkedList;
import java.util.List;

public class SeriesConfigurationPanel extends GridPane implements ChangeListener<List<TimeSeries>> {
    public final SimpleObjectProperty<TimeSeriesCollection> timeSeriesCollectionProperty;

    private int rowCount;
    private LinkedList<SeriesControl> seriesControls;

    private TimeSeriesCollection currentTimeSeriesCollection;

    public SeriesConfigurationPanel() {
        timeSeriesCollectionProperty = new SimpleObjectProperty<>();
        rowCount = 0;
        seriesControls = new LinkedList<>();
    }

    public void addSeriesControl(String ticker, SeriesProducer seriesProducer) {
        SeriesControl seriesControl = new SeriesControl(this, ticker, seriesProducer);
        addSeriesControl(seriesControl);
    }

    private void addSeriesControl(SeriesControl seriesControl) {
        seriesControl.timeSeriesProperty.addListener(this);
        seriesControls.add(seriesControl);
        add(seriesControl, 0, rowCount);
        rowCount++;
        updateTimeSeriesCollection();
    }

    private void updateTimeSeriesCollection() {
        updateCurrentTimeSeriesCollection();
        timeSeriesCollectionProperty.setValue(currentTimeSeriesCollection);
    }

    private void updateCurrentTimeSeriesCollection() {
        currentTimeSeriesCollection = new TimeSeriesCollection();
        for (SeriesControl seriesControl : seriesControls) {
            for (TimeSeries timeSeries : seriesControl.timeSeriesProperty.getValue()) {
                currentTimeSeriesCollection.addSeries(timeSeries);
            }
        }
    }

    public void removeSeriesControl(SeriesControl seriesControl) {
        seriesControl.timeSeriesProperty.removeListener(this);
        seriesControls.remove(seriesControl);
        getChildren().remove(seriesControl);
        updateTimeSeriesCollection();
    }

    @Override
    public void changed(ObservableValue<? extends List<TimeSeries>> observable,
                        List<TimeSeries> oldTimeSeries,
                        List<TimeSeries> newTimeSeries) {
        updateTimeSeriesCollection();
    }
}
