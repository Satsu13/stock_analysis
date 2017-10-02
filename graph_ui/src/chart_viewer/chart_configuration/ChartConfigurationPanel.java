package chart_viewer.chart_configuration;


import chart_viewer.chart_configuration.series_configuration.SeriesConfigurationPanel;
import column_constraint.GrowingColumnConstraint;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import repository.stock_repository.StockRepository;
import series_producer.SeriesProducer;

import java.io.IOException;

public class ChartConfigurationPanel extends GridPane {
    public final SimpleObjectProperty<JFreeChart> chartProperty;
    public final StockRepository repository;

    private SeriesConfigurationPanel seriesConfigurationPanel;
    private TickerSelectionComboBox tickerSelectionComboBox;
    private TradeHistoryProducerComboBox tradeHistoryProducerComboBox;
    private Button addSeriesButton;

    public ChartConfigurationPanel(StockRepository repository) throws IOException, ClassNotFoundException {
        chartProperty = new SimpleObjectProperty<>();
        this.repository = repository;
        initMembers();
        initConstraints();
        addMembers();
        initListeners();
    }

    private void initMembers() throws IOException, ClassNotFoundException {
        seriesConfigurationPanel = new SeriesConfigurationPanel();
        tickerSelectionComboBox = new TickerSelectionComboBox(repository);
        tradeHistoryProducerComboBox = new TradeHistoryProducerComboBox(repository);
        addSeriesButton = new Button("Add Series");
    }

    private void initConstraints() {
        getColumnConstraints().add(new GrowingColumnConstraint());
    }

    private void addMembers() {
        add(seriesConfigurationPanel, 0, 0, 1, 4);
        add(tickerSelectionComboBox, 1, 0);
        add(tradeHistoryProducerComboBox, 1, 1);
        add(addSeriesButton, 1, 2);
    }

    private void initListeners() {
        addSeriesButton.setOnAction(buildAddSeriesAction());
        seriesConfigurationPanel.timeSeriesCollectionProperty.addListener(buildSeriesConfigurationChangedListener());
    }

    private EventHandler<ActionEvent> buildAddSeriesAction() {
        return event -> {
            String ticker = tickerSelectionComboBox.getValue();
            SeriesProducer seriesProducer = tradeHistoryProducerComboBox.getValue();
            seriesConfigurationPanel.addSeriesControl(ticker, seriesProducer);
        };
    }

    private ChangeListener<? super TimeSeriesCollection> buildSeriesConfigurationChangedListener() {
        return (observable, oldCollection, newCollection) -> {
            JFreeChart newChart = ChartFactory.createTimeSeriesChart("", "", "", newCollection);
//            ((XYPlot) newChart.getPlot()).setRangeAxis(new LogAxis(""));
            chartProperty.setValue(newChart);
        };
    }
}
