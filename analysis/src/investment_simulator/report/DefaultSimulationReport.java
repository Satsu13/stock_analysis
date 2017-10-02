package investment_simulator.report;

import investment_simulator.MatureInvestment;
import investment_simulator.report.report_writer.ChartReportWriter;
import investment_simulator.report.report_writer.ReportWriter;
import investment_simulator.report.report_writer.StatisticsReportWriter;
import series_producer.Series;

import java.util.LinkedList;
import java.util.List;

public class DefaultSimulationReport extends SimulationReport {
    public DefaultSimulationReport() {
        super();
    }

    public DefaultSimulationReport(List<MatureInvestment> investments, List<Series> seriesList) {
        super(investments, seriesList);
    }

    @Override
    protected List<ReportWriter> getReportWriters(List<MatureInvestment> investments, List<Series> seriesList) {
        LinkedList<ReportWriter> reportWriters = new LinkedList<>();
        reportWriters.add(new StatisticsReportWriter(investments));
        reportWriters.add(new ChartReportWriter(seriesList, investments));
        return reportWriters;
    }
}
