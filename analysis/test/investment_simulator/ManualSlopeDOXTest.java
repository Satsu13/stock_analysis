package investment_simulator;

import investment_simulator.report.SimulationReport;
import org.junit.Test;

import static static_math.Linspace.linspace;

public class ManualSlopeDOXTest extends SlopeInvestmentSimulatorTest {
    private static final double MIN_MEANS_WINDOW_SIZE = 300;
    private static final double MAX_MEANS_WINDOW_SIZE = 1000;
    private static final double MIN_SLOPE_WINDOW_SIZE = 300;
    private static final double MAX_SLOPE_WINDOW_SIZE = 1000;
    private static final int DESIGN_AXIS_RESOLUTION = 6;

    @Test
    public void doxRun1() throws Exception {
        for (double meansAxisValue : linspace(MIN_MEANS_WINDOW_SIZE, MAX_MEANS_WINDOW_SIZE, DESIGN_AXIS_RESOLUTION)) {
            for (double slopeAxisValue : linspace(MIN_SLOPE_WINDOW_SIZE, MAX_SLOPE_WINDOW_SIZE, DESIGN_AXIS_RESOLUTION)) {
                System.out.println(meansAxisValue + " " + slopeAxisValue);
                SimulationReport report = simulate((int) meansAxisValue, (int) slopeAxisValue);
                writeReport(report, getReportFolderName((int) meansAxisValue, (int) slopeAxisValue));
            }
        }
    }

    private String getReportFolderName(long meansAxisValue, long slopeAxisValue) {
        return System.currentTimeMillis() + "_slope_dox_test_" + meansAxisValue + "_" + slopeAxisValue;
    }

    private double[] getDesignAxisCoefficients(double maxSize) {
        return new double[] {
                    0,
                maxSize / DESIGN_AXIS_RESOLUTION
            };
    }
}
