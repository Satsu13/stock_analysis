package investment_simulator;

import investment_simulator.report.SimulationReport;
import org.junit.Ignore;
import repository.stock_repository.StockRepository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FileUtils.deleteDirectory;

@Ignore
public class InvestmentSimulatorTest {
    public static final String REPOSITORY_DIRECTORY = "D:\\stocks\\stocks";
    public static final String REPORT_DIRECTORY = "D:\\stocks\\reports";

    public StockRepository getRepository() {
        return new StockRepository(REPOSITORY_DIRECTORY);
    }

    public void writeReport(SimulationReport report) throws Exception {
        writeReport(report, String.valueOf(System.currentTimeMillis()));
    }

    public void writeReport(SimulationReport report, String targetDirectoryName) throws Exception {
        Path outputFilePath = Paths.get(REPORT_DIRECTORY, String.valueOf(targetDirectoryName));
        File outputFile = new File(outputFilePath.toString());
        deleteDirectory(outputFile);
        outputFile.mkdirs();
        report.write(outputFile);
    }
}
