package dox;

import investment_simulator.report.SimulationReport;

import java.io.File;
import java.util.List;

public abstract class DOXReport {
    public final File reportDirectory;

    public DOXReport(File reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public abstract void processReport(SimulationReport report, List<Double> axisValues) throws Exception;

    public void processSimulationException(Exception exception) {
        exception.printStackTrace();
        //todo logging
    }
}
