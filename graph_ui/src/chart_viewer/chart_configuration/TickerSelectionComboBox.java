package chart_viewer.chart_configuration;

import filter.DefaultStockFilterer;
import filter.StockFilterer;
import filter.liquid.GreaterThanDoubleReturnsFilterer;
import filter.liquid.LiquidStockFilter;
import filter.liquid.SlopeGreaterThanStockFilter;
import javafx.scene.control.ComboBox;
import repository.stock_repository.StockRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.YEARS;

public class TickerSelectionComboBox extends ComboBox<String> {

    public TickerSelectionComboBox(StockRepository repository) throws IOException, ClassNotFoundException {
        StockFilterer filterer = getStockFilterer(repository);
        List<String> tickers = filterer.filterTickers();
        getItems().addAll(tickers);
//        getItems().addAll(new HashSet<>(new ImmutableList<>(
//                "NTAP",
//                "JNPR",
//                "YHOO",
//                "AMZN",
//                "LVLT",
//                "AMCC",
//                "VRSN",
//                "INTC",
//                "BRCD",
//                "INFY",
//                "BA",
//                "NOK",
//                "MO",
//                "MCD",
//                "BA",
//                "KO",
//                "AMAT",
//                "MRK",
//                "DIS",
//                "EBAY",
//                "MSFT",
//                "MO",
//                "IGT",
//                "BBY",
//                "FDX",
//                "BEN",
//                "ADPT",
//                "BA",
//                "AMGN",
//                "GILD",
//                "NUE",
//                "GD",
//                "CSCO",
//                "BIDU",
//                "CELG",
//                "UNP",
//                "WMT",
//                "CTSH",
//                "WMT",
//                "JNJ",
//                "CNC",
//                "PG",
//                "PEP",
//                "BEN",
//                "HD",
//                "MDT",
//                "SAB",
//                "SBUX",
//                "MO",
//                "AZO",
//                "CSCO",
//                "DIS",
//                "MEI",
//                "NKE",
//                "FDX",
//                "LUV",
//                "CF",
//                "BUD",
//                "AMGN",
//                "ORCL",
//                "GD",
//                "IBM",
//                "JNJ",
//                "CERN",
//                "UNH",
//                "TXN",
//                "ESRX",
//                "KO",
//                "ORCL",
//                "NVO",
//                "MSFT",
//                "BA",
//                "MCD",
//                "AAPL",
//                "DIS",
//                "AMZN",
//                "HD",
//                "SAB"
//        )));
        Collections.sort(getItems());
        getItems().forEach(System.out::println);
    }

    private StockFilterer getStockFilterer(StockRepository repository) {
        return getLiquidTickers(repository);
    }

    private StockFilterer getAllTickers(StockRepository repository) {
        return new DefaultStockFilterer(repository);
    }

    private StockFilterer getLiquidTickers(StockRepository repository) {
        return new LiquidStockFilter(repository);
    }

    private StockFilterer getPositiveAverageTickers(StockRepository repository) {
        SlopeGreaterThanStockFilter filter = new SlopeGreaterThanStockFilter(repository, 0.5, 20, 100);
        filter.setStartDate(now().minus(10, YEARS));
        filter.setEndDate(now().minus(5, YEARS));
        return filter;
    }

    private StockFilterer getGreaterThanDoubleReturns(StockRepository repository) {
        return new GreaterThanDoubleReturnsFilterer(repository, now().minus(10, YEARS), now().minus(5, YEARS));
    }
}