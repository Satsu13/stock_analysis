package filter;

import immutable.ImmutableList;
import repository.stock_repository.StockRepository;

public class DefaultStockFilterer extends StockFilterer {
    public DefaultStockFilterer(StockRepository repository) {
        super(repository);
    }

    public DefaultStockFilterer(StockRepository repository, ImmutableList<String> tickersToFilter) {
        super(repository, tickersToFilter);
    }

    @Override
    protected int getTimeoutInMinutes() {
        return 10;
    }

    @Override
    public boolean shouldAccept(String ticker) {
        return true;
    }
}
