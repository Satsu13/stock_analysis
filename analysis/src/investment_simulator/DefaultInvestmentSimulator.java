package investment_simulator;

import filter.StockFilterer;
import filter.liquid.LiquidStockFilter;
import immutable.ImmutableList;
import repository.stock_repository.StockRepository;
import series_producer.Series;

import java.time.LocalDate;
import java.util.List;

public class DefaultInvestmentSimulator extends InvestmentSimulator {
    public DefaultInvestmentSimulator(StockRepository repository) {
        super(repository);
    }

    public DefaultInvestmentSimulator(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
    }

    @Override
    protected StockFilterer getStockFilterer() {
        return new LiquidStockFilter(repository);
    }

    @Override
    protected List<Series> preloadSeries(StockRepository repository, String ticker) {
        return new ImmutableList<>();
    }

    @Override
    protected boolean shouldBuy(List<Series> generatedSeries, LocalDate currentDate, String ticker) {
        return true;
    }

    @Override
    protected boolean shouldSell(List<Series> preLoadedSeries, LocalDate currentDate, String ticker) {
        return false;
    }
}
