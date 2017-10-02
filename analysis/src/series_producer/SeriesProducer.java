package series_producer;

import immutable.ImmutableList;
import repository.stock_repository.StockRepository;

import java.util.List;

public abstract class SeriesProducer {
    public final StockRepository repository;

    public SeriesProducer(StockRepository repository) {
        this.repository = repository;
    }

    public List<Series> produceSeries(String ticker, List<Number> arguments) {
        Number[] argumentArray = arguments.toArray(new Number[arguments.size()]);
        return produceSeries(ticker, argumentArray);
    }

    public abstract List<Series> produceSeries(String ticker, Number... arguments);

    public abstract ImmutableList<ExpectedArgument> getExpectedArguments();

    public String getName() {
        return getClass().getSimpleName().replaceAll("SeriesProducer", "");
    }

    @Override
    public String toString() {
        return getName();
    }
}
