package filter.liquid;

import investment_simulator.report.SimulationReport;
import repository.stock_repository.StockRepository;
import investment_simulator.DefaultInvestmentSimulator;
import investment_simulator.InvestmentSimulator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class GreaterThanDoubleReturnsFilterer extends LiquidStockFilter {
    public static final double RETURN_THRESHOLD = 100.0;
    private Set<String> acceptableTickers;

    public GreaterThanDoubleReturnsFilterer(StockRepository repository) {
        this(repository, LocalDate.MIN, LocalDate.MAX);
    }

    public GreaterThanDoubleReturnsFilterer(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository);
        Set<String> thresholdReturnTickers = calculateThresholdReturnTickers(repository, startDate, endDate);
        Set<String> positiveSlopeTickers = calculatePositiveSlopeTickers(repository);
        filterAcceptableTickers(thresholdReturnTickers, positiveSlopeTickers);
    }

    private Set<String> calculateThresholdReturnTickers(
            StockRepository repository, LocalDate startDate, LocalDate endDate) {
        InvestmentSimulator simulator = new DefaultInvestmentSimulator(repository, startDate, endDate);
        SimulationReport report = simulator.simulate();
        return report.getInvestments().stream()
                .filter(investment -> investment.calculateReturn() > RETURN_THRESHOLD)
                .map(investment -> investment.buy.getTicker())
                .collect(toSet());
    }

    private Set<String> calculatePositiveSlopeTickers(StockRepository repository) {
        SlopeGreaterThanStockFilter filter = new SlopeGreaterThanStockFilter(repository, 100, 20, 100);
        return new HashSet<>(filter.filterTickers());
    }

    private void filterAcceptableTickers(Set<String> thresholdReturnTickers, Set<String> positiveSlopeTickers) {
        acceptableTickers = thresholdReturnTickers.stream()
                .filter(positiveSlopeTickers::contains)
                .collect(toSet());
    }

    @Override
    protected int getTimeoutInMinutes() {
        return 5;
    }

    @Override
    public boolean shouldAccept(String ticker) {
        return super.shouldAccept(ticker) && acceptableTickers.contains(ticker);
    }
}
