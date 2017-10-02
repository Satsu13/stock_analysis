package dox;

import investment_simulator.report.SimulationReport;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.commons.io.FileUtils.deleteDirectory;

public class DefaultDOXReport extends DOXReport {
    public DefaultDOXReport(File reportDirectory) {
        super(reportDirectory);
    }

    @Override
    public void processReport(SimulationReport report, List<Double> axisValues) throws Exception {
        Path outputFilePath = Paths.get(reportDirectory.toPath().toString(), getReportDirectoryName(axisValues));
        File outputFile = new File(outputFilePath.toString());
        deleteDirectory(outputFile);
        outputFile.mkdirs();
        report.write(outputFile);
    }

    private String getReportDirectoryName(List<Double> axisValues) {
        String name = String.valueOf(System.currentTimeMillis());
        for (Double axisValue : axisValues) {
            name += "_" + axisValue;
        }
        return name;
    }
}
