package chart_viewer;

import chart_viewer.chart_configuration.ChartConfigurationPanel;
import column_constraint.GrowingColumnConstraint;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.GridPane;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.FastScatterPlot;
import repository.stock_repository.StockRepository;
import row_constraint.FittingRowConstraint;
import row_constraint.GrowingRowConstraint;

import java.io.IOException;

public class MainLayout extends GridPane {
    private ChartConfigurationPanel chartConfigurationPanel;

    private JFreeChart chart;
    private ChartViewer chartViewer;

    public MainLayout(StockRepository repository) throws IOException, ClassNotFoundException {
        initMembers(repository);
        initConstraints();
        addMembers();
        chartConfigurationPanel.chartProperty.addListener(buildChartUpdatedListener());
    }

    private void initConstraints() {
        GrowingColumnConstraint growingColumnConstraint = new GrowingColumnConstraint();
        getColumnConstraints().add(growingColumnConstraint);
        getRowConstraints().add(new FittingRowConstraint());
        getRowConstraints().add(new GrowingRowConstraint());
    }

    private void initMembers(StockRepository repository) throws IOException, ClassNotFoundException {
        chartConfigurationPanel = new ChartConfigurationPanel(repository);
        chart = new JFreeChart(new FastScatterPlot());
        chartViewer = new ChartViewer(chart);
    }

    private void addMembers() {
        add(chartConfigurationPanel, 0, 0);
        add(chartViewer, 0, 1);
    }

    private ChangeListener<? super JFreeChart> buildChartUpdatedListener() {
        return (observable, oldChart, newChart) -> changeChart(newChart);
    }

    private void changeChart(JFreeChart newChart) {
        chart = newChart;
        chartViewer.setChart(chart);
    }


}
