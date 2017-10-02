package investment_simulator;

import investment_simulator.report.SimulationReport;
import org.junit.Test;
import repository.stock_repository.StockRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SlopeInvestmentSimulatorTest extends InvestmentSimulatorTest {

    public static final LocalDate START_DATE = LocalDate.of(2017, 1, 1).minus(15, ChronoUnit.YEARS);

    @Test
    public void testSimulator() throws Exception {
        SimulationReport report = simulate(100, 600);
        writeReport(report);
    }

    public SimulationReport simulate(int meansWindowSize, int slopeWindowSize) {
        SlopeInvestmentSimulator simulator = getSlopeInvestmentSimulator(meansWindowSize, slopeWindowSize);
        simulator.setStartDate(START_DATE);
        return simulator.simulate();
    }

    public SlopeInvestmentSimulator getSlopeInvestmentSimulator(int meansWindowSize, int slopeWindowSize) {
        StockRepository repository = getRepository();
        return new SlopeInvestmentSimulator(repository, meansWindowSize, slopeWindowSize);
    }
}