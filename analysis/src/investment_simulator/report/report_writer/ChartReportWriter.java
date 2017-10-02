package investment_simulator.report.report_writer;

import investment_simulator.MatureInvestment;
import of_collection.hashtable.HashtableOfLinkedLists;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import series_producer.Series;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ChartReportWriter implements ReportWriter {
    private LinkedList<Series> seriesList;
    protected HashtableOfLinkedLists<String, MatureInvestment> investmentsByTicker;

    public ChartReportWriter(List<Series> seriesList, List<MatureInvestment> investments) {
        this.seriesList = new LinkedList<>(seriesList);
        initInvestmentsByTicker(investments);
    }

    private void initInvestmentsByTicker(List<MatureInvestment> investments) {
        investmentsByTicker = new HashtableOfLinkedLists<>();
        for (MatureInvestment investment : investments) {
            investmentsByTicker.add(investment.getTicker(), investment);
        }
    }

    @Override
    public void write(File reportDirectory) {
        seriesList.parallelStream()
                .forEach(series -> printGraph(series, reportDirectory));
    }

    private void printGraph(Series series, File reportDirectory) {
        File outputDirectory = initChartDirectory(reportDirectory, series.ticker);
        JFreeChart chart = buildChart(series);
        File outputImageFile = getOutputImageFile(Paths.get(
                outputDirectory.toPath().toString(),
                series.name + ".jpg")
                .toString());
        tryPrintingGraph(chart, outputImageFile);
    }

    private File getOutputImageFile(String pathname) {
        return new File(pathname);
    }

    private File initChartDirectory(File reportDirectory, String ticker) {
        String chartDirectoryPath = Paths.get(reportDirectory.toPath().toString(), ticker).toString();
        File outputDirectory = new File(chartDirectoryPath);
        outputDirectory.mkdirs();
        return outputDirectory;
    }

    private JFreeChart buildChart(Series series) {
        TimeSeriesCollection timeSeriesCollection = buildTimeSeriesCollection(series);
        return buildChart(series, timeSeriesCollection);
    }


    private TimeSeriesCollection buildTimeSeriesCollection(Series series) {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries timeSeries = series.toTimeSeries();
        timeSeriesCollection.addSeries(timeSeries);
        return timeSeriesCollection;
    }

    private JFreeChart buildChart(Series series, TimeSeriesCollection timeSeriesCollection) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                series.name,
                "Time",
                series.yAxisUnits,
                timeSeriesCollection);
        addInvestmentMarkers(series, chart);
        return chart;
    }

    private void addInvestmentMarkers(Series series, JFreeChart chart) {
        for (MatureInvestment investment : investmentsByTicker.get(series.ticker)) {
            addValueMarker(chart, investment.buy.getDate(), Color.GREEN);
            addValueMarker(chart, investment.sell.getDate(), Color.RED);
        }
    }

    private void addValueMarker(JFreeChart chart, LocalDate date, Color color) {
        ValueMarker marker = new ValueMarker((double) date.toEpochDay() * 8.64e+7, color, new BasicStroke());
        ((XYPlot) chart.getPlot()).addDomainMarker(marker);
    }

    private void tryPrintingGraph(JFreeChart chart, File outputImageFile) {
        try {
            ChartUtilities.saveChartAsJPEG(outputImageFile, chart, 1024, 768);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
