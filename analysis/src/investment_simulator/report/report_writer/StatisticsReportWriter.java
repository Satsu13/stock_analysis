package investment_simulator.report.report_writer;

import investment_simulator.MatureInvestment;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import stream.StreamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import static java.lang.Double.compare;
import static java.util.stream.Collectors.toList;

public class StatisticsReportWriter implements ReportWriter {
    private List<MatureInvestment> investments;

    public StatisticsReportWriter(List<MatureInvestment> investments) {
        this.investments = investments;
        sortInvestmentsByReturn();
    }

    private void sortInvestmentsByReturn() {
        investments = investments.stream()
                .sorted((that, other) -> compare(that.calculateReturn(), other.calculateReturn()))
                .collect(toList());
    }

    @Override
    public void write(File reportDirectory) {
        PrintWriter reportWriter = tryBuildingReportWriter(reportDirectory);
        writeReturnStatistics(reportWriter);
        writeTimeStatistics(reportWriter);
        writeInvestments(reportWriter);
        reportWriter.flush();
        reportWriter.close();
    }

    private PrintWriter tryBuildingReportWriter(File directory) {
        try {
            return buildReportWriter(directory);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private PrintWriter buildReportWriter(File directory) throws FileNotFoundException {
        String reportPath = Paths.get(directory.toPath().toString(), "report.txt").toString();
        return new PrintWriter(reportPath);
    }

    private void writeReturnStatistics(PrintWriter reportWriter) {
        reportWriter.println("Return statistics (%ROI):");
        writeStatistics(this::calculateReturns, reportWriter);
        reportWriter.println();
    }

    private List<Number> calculateReturns(List<MatureInvestment> investments) {
        return StreamUtil.mapToList(investments, MatureInvestment::calculateReturn);
    }

    private void writeTimeStatistics(PrintWriter reportWriter) {
        reportWriter.println("Time statistics (Days):");
        writeStatistics(this::calculateLengths, reportWriter);
        reportWriter.println();
    }

    private List<Number> calculateLengths(List<MatureInvestment> investments) {
        return StreamUtil.mapToList(investments, MatureInvestment::calculateLengthInDays);
    }

    public void writeStatistics(Function<List<MatureInvestment>, List<Number>> calculator, PrintWriter printWriter) {
        List<Number> statistics = calculator.apply(investments);
        DescriptiveStatistics descriptiveStatistics = buildDescriptiveStatistics(statistics);
        writeStatistics(statistics, descriptiveStatistics, printWriter);
    }

    private DescriptiveStatistics buildDescriptiveStatistics(List<Number> statistics) {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        statistics.stream()
                .map(Number::doubleValue)
                .forEach(descriptiveStatistics::addValue);
        return descriptiveStatistics;
    }

    private void writeStatistics(List<Number> statistics,
                                 DescriptiveStatistics descriptiveStatistics,
                                 PrintWriter printWriter) {
        printWriter.println("Size: " + statistics.size());
        printWriter.println("Max: " + descriptiveStatistics.getMax());
        printWriter.println("Min: " + descriptiveStatistics.getMin());
        printWriter.println("Skewness: " + descriptiveStatistics.getSkewness());
        printWriter.println("Standard Deviation: " + descriptiveStatistics.getStandardDeviation());
        printWriter.println("Average: " + descriptiveStatistics.getMean());
        printWriter.println("% above 0.0: " + getPercentAboveZero(statistics));
        printWriter.println("Total: " + getTotal(statistics));
    }

    //TODO move these guys to math lib?
    private double getPercentAboveZero(List<Number> statistics) {
        double count = 0.0;
        for (Number statistic : statistics) {
            if (statistic.doubleValue() > 0.0) {
                count++;
            }
        }
        return 100.0 * count / statistics.size();
    }

    private Number getTotal(List<Number> statistics) {
        Number total = 0.0;
        for (Number statistic : statistics) {
            total = total.doubleValue() + statistic.doubleValue();
        }
        return total;
    }

    private void writeInvestments(PrintWriter reportWriter) {
        for (MatureInvestment investment : investments) {
            reportWriter.println(investment.buy.getTicker() + ": "
                    + investment.calculateReturn() + "% return, "
                    + investment.calculateLengthInDays() + " days: "
                    + investment.buy.getDate() + " - " + investment.sell.getDate());
        }
        reportWriter.println();
    }
}
