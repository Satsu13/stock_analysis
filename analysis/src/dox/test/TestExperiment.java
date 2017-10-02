package dox.test;

import dox.Experiment;
import investment_simulator.InvestmentSimulator;
import investment_simulator.report.DefaultSimulationReport;
import investment_simulator.report.SimulationReport;
import repository.stock_repository.StockRepository;

import java.util.List;

public class TestExperiment extends Experiment {
    public TestExperiment(StockRepository repository) {
        super(repository);
    }

    @Override
    public SimulationReport simulate(List<Double> axisValues) {
        return new DefaultSimulationReport();
    }

    @Override
    public InvestmentSimulator getSimulator(List<Double> axisValues) {
        throw new UnsupportedOperationException();
    }
}
