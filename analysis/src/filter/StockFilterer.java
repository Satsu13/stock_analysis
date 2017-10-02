package filter;

import immutable.ImmutableList;
import repository.stock_repository.StockRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class StockFilterer {
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    public final StockRepository repository;
    public final ImmutableList<String> originalTickers;

    private LinkedList<String> filteredTickers;
    private ExecutorService workerThreadPool;

    public StockFilterer(StockRepository repository) {
        this.repository = repository;
        originalTickers = new ImmutableList<>(getDefaultTickers(repository));
        initRuntimeMembers();
    }

    private List<String> getDefaultTickers(StockRepository repository) {
        return repository.getTradeHistoryDump().listAvailableFiles();
    }

    public StockFilterer(StockRepository repository, ImmutableList<String> tickersToFilter) {
        this.repository = repository;
        this.originalTickers = tickersToFilter;
        initRuntimeMembers();
    }

    private void initRuntimeMembers() {
        filteredTickers = new LinkedList<>();
        workerThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public ImmutableList<String> filterTickers() {
        submitTasks(originalTickers);
        executeTasks();
        Collections.sort(filteredTickers);
        return new ImmutableList<>(filteredTickers);
    }

    private void submitTasks(List<String> tickers) {
        for (String ticker : tickers) {
            Runnable filterTickerTask = () -> tryFilteringTicker(ticker);
            workerThreadPool.submit(filterTickerTask);
        }
    }

    private void executeTasks() {
        workerThreadPool.shutdown();
        int timeoutInMinutes = getTimeoutInMinutes();
        try {
            workerThreadPool.awaitTermination(timeoutInMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract int getTimeoutInMinutes();

    private void tryFilteringTicker(String ticker) {
        try {
            filterTicker(ticker);
        } catch (Exception e) {}
    }

    private void filterTicker(String ticker) {
        if (shouldAccept(ticker)) {
            synchronizedAddTicker(ticker);
        }
    }

    private synchronized void synchronizedAddTicker(String ticker) {
        filteredTickers.add(ticker);
    }

    public abstract boolean shouldAccept(String ticker);
}
