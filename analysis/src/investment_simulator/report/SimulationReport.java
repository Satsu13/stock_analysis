package investment_simulator.report;

import investment_simulator.MatureInvestment;
import investment_simulator.report.report_writer.ReportWriter;
import series_producer.Series;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class SimulationReport {
    protected List<MatureInvestment> investments;
    protected List<Series> seriesList;

    public SimulationReport() {
        investments = new LinkedList<>();
        seriesList = new LinkedList<>();
    }

    public SimulationReport(List<MatureInvestment> investments, List<Series> seriesList) {
        this.investments = investments;
        this.seriesList = seriesList;
    }

    public List<MatureInvestment> getInvestments() {
        return investments;
    }

    public List<Series> getSeriesList() {
        return seriesList;
    }

    public void addInvestments(List<MatureInvestment> investments) {
        this.investments.addAll(investments);
    }

    public void addInvestment(MatureInvestment investment) {
        investments.add(investment);
    }

    public void addSeries(List<Series> series) {
        this.seriesList.addAll(series);
    }

    public void addSeries(Series series) {
        seriesList.add(series);
    }

    public void write(File directory) {
        List<ReportWriter> reportWriters = getReportWriters(investments, seriesList);
        for (ReportWriter reportWriter : reportWriters) {
            reportWriter.write(directory);
        }
    }

    protected abstract List<ReportWriter> getReportWriters(List<MatureInvestment> investments, List<Series> seriesList);
}
