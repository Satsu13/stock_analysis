package dox;

import investment_simulator.InvestmentSimulator;
import investment_simulator.report.SimulationReport;
import repository.stock_repository.StockRepository;

import java.time.LocalDate;
import java.util.List;

public abstract class Experiment {
    public final StockRepository repository;

    private LocalDate startDate;
    private LocalDate endDate;

    public Experiment(StockRepository repository) {
        this.repository = repository;
        startDate = LocalDate.MIN;
        endDate = LocalDate.MAX;
    }

    public Experiment(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        this.repository = repository;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SimulationReport simulate(List<Double> axisValues) {
        InvestmentSimulator simulator = getSimulator(axisValues);
        simulator.setStartDate(startDate);
        simulator.setEndDate(endDate);
        return simulator.simulate();
    }

    public abstract InvestmentSimulator getSimulator(List<Double> axisValues);
}
