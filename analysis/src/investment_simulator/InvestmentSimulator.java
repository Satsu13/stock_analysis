package investment_simulator;

import filter.StockFilterer;
import immutable.ImmutableList;
import investment_simulator.report.DefaultSimulationReport;
import investment_simulator.report.SimulationReport;
import of_collection.hashtable.HashtableOfLinkedLists;
import repository.stock_repository.StockRepository;
import series_producer.Series;
import series_producer.default_producer.summary.split_adjusted.SplitAdjustedSeriesProducer;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;
import static java.time.LocalDate.MAX;
import static java.time.LocalDate.MIN;
import static java.util.concurrent.Executors.newFixedThreadPool;

public abstract class InvestmentSimulator {
    public static final int THREAD_COUNT = getRuntime().availableProcessors();

    protected StockRepository repository;
    private LocalDate startDate;
    private LocalDate endDate;

    private ExecutorService threadPool;
    private HashtableOfLinkedLists<String, MatureInvestment> investmentsByTicker;
    private Hashtable<String, Trade> holdingTable;
    private SimulationReport simulationReport;
    private HashSet<String> soldTickers;

    public InvestmentSimulator(StockRepository repository) {
        this(repository, MIN, MAX);
    }

    public InvestmentSimulator(StockRepository repository, LocalDate startDate, LocalDate endDate) {
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

    protected Hashtable<String, Trade> getHoldingTable() {
        return holdingTable;
    }

    public SimulationReport simulate() {
        initRuntimeMembers();
        submitJobs();
        runJobs();
        return simulationReport;
    }

    private void initRuntimeMembers() {
        threadPool = newFixedThreadPool(THREAD_COUNT);
        investmentsByTicker = new HashtableOfLinkedLists<>();
        holdingTable = new Hashtable<>();
        simulationReport = initSimulationReport();
        soldTickers = new HashSet<>();
    }

    protected SimulationReport initSimulationReport() {
        return new DefaultSimulationReport();
    }

    private void submitJobs() {
        List<String> tickers = getTickerList();
        for (String ticker : tickers) {
            threadPool.submit(() -> calculateInvestments(ticker));
        }
    }

    protected List<String> getTickerList() {
        StockFilterer filterer = getStockFilterer();
        return filterer.filterTickers();
    }

    protected abstract StockFilterer getStockFilterer();

    protected void calculateInvestments(String ticker) {
        Series prices = getPriceHistory(ticker);
        List<Series> preloadedSeries = preloadSeries(repository, ticker);

        decideToTrade(ticker, prices, preloadedSeries);
        decideToFinishTrade(ticker);
        decideToReport(ticker, preloadedSeries);
    }

    protected abstract List<Series> preloadSeries(StockRepository repository, String ticker);

    private Series getPriceHistory(String ticker) {
        SplitAdjustedSeriesProducer splitAdjustedSeriesProducer = new SplitAdjustedSeriesProducer(repository);
        return splitAdjustedSeriesProducer.produceSeries(ticker).get(0);
    }

    private void decideToTrade(String ticker, Series prices, List<Series> preloadedSeries) {
        List<Series> trimmedSeries = initTrimmedSeries(preloadedSeries);
        for (LocalDate date : prices.keySet()) {
            if (date.isAfter(startDate) && date.isBefore(endDate)) {
                incrementTrimmedSeries(trimmedSeries, preloadedSeries, date);
                tryDecidingToTrade(trimmedSeries, date, ticker, prices.get(date));
            }
        }
    }

    private List<Series> initTrimmedSeries(List<Series> preloadedSeries) {
        List<Series> trimmedSeries = new ArrayList<>(preloadedSeries.size());
        for (Series series : preloadedSeries) {
            Series newSeries = new Series(series.name, series.ticker, series.yAxisUnits);
            trimmedSeries.add(newSeries);
        }
        return trimmedSeries;
    }

    private void incrementTrimmedSeries(List<Series> trimmedSeries, List<Series> preloadedSeries, LocalDate date) {
        for (int i = 0; i < trimmedSeries.size(); i++) {
            if (preloadedSeries.get(i).containsKey(date)) {
                trimmedSeries.get(i).put(date, preloadedSeries.get(i).get(date));
            }
        }
    }

    private void tryDecidingToTrade(List<Series> preloadedSeries, LocalDate currentDate, String ticker, Double price) {
        try {
            decideToTrade(preloadedSeries, currentDate, ticker, price);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decideToTrade(List<Series> preloadedSeries, LocalDate currentDate, String ticker, Double price) {
//        List<Series> trimmedSeries = trimDatesAfterCurrentDate(preloadedSeries, currentDate);
        decideToBuy(preloadedSeries, currentDate, ticker, price);
        decideToSell(preloadedSeries, currentDate, ticker, price);
    }

    private List<Series> trimDatesAfterCurrentDate(List<Series> preloadedSeries, LocalDate currentDate) {
        List<Series> trimmedSeriesList = new LinkedList<>();
        for (Series series : preloadedSeries) {
            Series trimmedSeries = trimDatesAfterCurrentDate(series, currentDate);
            trimmedSeriesList.add(trimmedSeries);
        }
        return new ImmutableList<>(trimmedSeriesList);
    }

    private Series trimDatesAfterCurrentDate(Series originalSeries, LocalDate currentDate) {
        Series trimmedSeries = new Series(originalSeries.name, originalSeries.ticker, originalSeries.yAxisUnits);
        originalSeries.keySet().stream()
                .filter(date -> date.isBefore(currentDate) || date.isEqual(currentDate))
                .forEach(date -> trimmedSeries.put(date, originalSeries.get(date)));
        return trimmedSeries;
    }

    private void decideToBuy(List<Series> preLoadedSeries, LocalDate currentDate, String ticker, Double price) {
        if (!holdingTable.containsKey(ticker) && shouldBuy(preLoadedSeries, currentDate, ticker)) {
            buy(ticker, currentDate, price);
        }
    }

    protected abstract boolean shouldBuy(List<Series> preLoadedSeries, LocalDate currentDate, String ticker);

    private void buy(String ticker, LocalDate currentDate, Double price) {
        Trade trade = buildBuy(ticker, currentDate, price);
        holdingTable.put(ticker, trade);
    }

    private Trade buildBuy(String ticker, LocalDate currentDate, Double price) {
        return new Trade(ticker, currentDate, price, Trade.Type.BUY);
    }

    private void decideToSell(List<Series> preLoadedSeries, LocalDate currentDate, String ticker, Double price) {
        if (holdingTable.containsKey(ticker) && shouldSell(preLoadedSeries, currentDate, ticker)) {
            sell(ticker, currentDate, price);
        }
    }

    protected abstract boolean shouldSell(List<Series> preLoadedSeries, LocalDate currentDate, String ticker);

    private void sell(String ticker, LocalDate currentDate, Double price) {
        Trade sell = buildSell(ticker, currentDate, price);
        MatureInvestment matureInvestment = new MatureInvestment(holdingTable.get(ticker), sell);
        synchronizedAddMatureInvestment(matureInvestment);
        holdingTable.remove(ticker);
        soldTickers.add(ticker);
    }

    private Trade buildSell(String ticker, LocalDate currentDate, Double price) {
        return new Trade(ticker, currentDate, price, Trade.Type.SELL);
    }

    private synchronized void synchronizedAddMatureInvestment(MatureInvestment investment) {
        investmentsByTicker.add(investment.getTicker(), investment);
        simulationReport.addInvestment(investment);
    }

    private void decideToFinishTrade(String ticker) {
        if (holdingTable.containsKey(ticker)) {
            finishTradeToDate(ticker);
        }
    }

    private void finishTradeToDate(String ticker) {
        Series priceHistory = getPriceHistory(ticker);
        LocalDate lastDate = priceHistory.lastKey();
        double price = priceHistory.get(lastDate);
        sell(ticker, lastDate, price);
    }


    private void decideToReport(String ticker, List<Series> preloadedSeries) {
        if (soldTickers.contains(ticker)) {
            reportPriceSeries(ticker);
            simulationReport.addSeries(preloadedSeries);
        }
    }

    private void reportPriceSeries(String ticker) {
        SplitAdjustedSeriesProducer producer = new SplitAdjustedSeriesProducer(repository);
        List<Series> priceSeries = producer.produceSeries(ticker);
        simulationReport.addSeries(priceSeries);
    }

    private void runJobs() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
