package dox.slope;

import dox.*;
import repository.stock_repository.StockRepository;

import java.io.File;
import java.time.LocalDate;

public class SlopeDOX extends DOX {
    public final DesignAxis meansWindowSize;
    public final DesignAxis slopeWindowSize;
    public final DesignAxis slopeMeanWindowSize;
    public final DesignAxis meanSlopeBuyThreshold;
    public final DesignAxis meanSlopeSellThreshold;

    public SlopeDOX(StockRepository repository) {
        super(repository);
        meansWindowSize = new DesignAxis("Means Window Size", "Days");
        slopeWindowSize = new DesignAxis("Slope Window Size", "Days");
        slopeMeanWindowSize = new DesignAxis("Slope Average Window Size", "Days");
        meanSlopeBuyThreshold = new DesignAxis("Mean Slope Buy Threshold", "Average Dollars per Day");
        meanSlopeSellThreshold = new DesignAxis("Mean Slope Sell Threshold", "Average Dollars per Day");
        init();
    }

    public SlopeDOX(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        super(repository, startDate, endDate);
        meansWindowSize = new DesignAxis("Means Window Size", "Days");
        slopeWindowSize = new DesignAxis("Slope Window Size", "Days");
        slopeMeanWindowSize = new DesignAxis("Slope Average Window Size", "Days");
        meanSlopeBuyThreshold = new DesignAxis("Mean Slope Buy Threshold", "Average Dollars per Day");
        meanSlopeSellThreshold = new DesignAxis("Mean Slope Sell Threshold", "Average Dollars per Day");
        init();
    }

    private void init() {
        addDesignAxis();
    }

    private void addDesignAxis() {
        getDesignAxis().add(meansWindowSize);
        getDesignAxis().add(slopeWindowSize);
        getDesignAxis().add(slopeMeanWindowSize);
        getDesignAxis().add(meanSlopeBuyThreshold);
        getDesignAxis().add(meanSlopeSellThreshold);
    }

    @Override
    protected DOXReport getDoxReport(File doxReportDirectory) {
        return new DefaultDOXReport(doxReportDirectory);
    }

    @Override
    protected Experiment getExperiment() {
        return new SlopeExperiment(repository);
    }
}
