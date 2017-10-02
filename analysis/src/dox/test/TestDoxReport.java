package dox.test;

import dox.DOXReport;
import investment_simulator.report.SimulationReport;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TestDoxReport extends DOXReport {
    public TestDoxReport(File reportDirectory) {
        super(reportDirectory);
    }

    @Override
    public void processReport(SimulationReport report, List<Double> axisValues) throws Exception {
        System.out.println(Arrays.asList(axisValues.toArray()));
    }
}
