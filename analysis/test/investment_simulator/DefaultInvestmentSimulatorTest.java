package investment_simulator;

import investment_simulator.report.SimulationReport;
import org.junit.Test;
import repository.stock_repository.StockRepository;

public class DefaultInvestmentSimulatorTest extends InvestmentSimulatorTest {
    @Test
    public void testSimulator() throws Exception {
        StockRepository repository = getRepository();
        DefaultInvestmentSimulator defaultInvestmentSimulator = new DefaultInvestmentSimulator(repository);
        SimulationReport report = defaultInvestmentSimulator.simulate();
        writeReport(report);
    }
}