package dox.slope;

import dox.Experiment;
import investment_simulator.InvestmentSimulator;
import investment_simulator.SlopeInvestmentSimulator;
import repository.stock_repository.StockRepository;

import java.time.LocalDate;
import java.util.List;

public class SlopeExperiment extends Experiment {
    public SlopeExperiment(StockRepository repository) {
        super(repository);
    }

    public SlopeExperiment(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    public InvestmentSimulator getSimulator(List<Double> axisValues) {
        SlopeInvestmentSimulator simulator = new SlopeInvestmentSimulator(repository,
                getMeansWindowSize(axisValues).intValue(),
                getSlopeWindowSize(axisValues).intValue());
        simulator.setSlopeMeansWindowSizeInDays(getSlopeMeanWindowSize(axisValues).intValue());
        simulator.setBuySlopeThreshold(getMeanSlopeBuyThreshold(axisValues));
        simulator.setSellSlopeThreshold(getMeanSlopeSellThreshold(axisValues));
        return simulator;
    }

    private Double getMeansWindowSize(List<Double> axisValues) {
        return axisValues.get(0);
    }

    private Double getSlopeWindowSize(List<Double> axisValues) {
        return axisValues.get(1);
    }

    private Double getSlopeMeanWindowSize(List<Double> axisValues) {
        return axisValues.get(2);
    }

    private Double getMeanSlopeBuyThreshold(List<Double> axisValues) {
        return axisValues.get(3);
    }

    private Double getMeanSlopeSellThreshold(List<Double> axisValues) {
        return axisValues.get(4);
    }
}
