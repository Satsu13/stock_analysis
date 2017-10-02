package dox.slope;

import org.junit.Test;
import repository.stock_repository.StockRepository;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SlopeDOXTest {

    public static final LocalDate START_DATE = LocalDate.of(2017, 1, 1).minus(15, ChronoUnit.YEARS);

    @Test
    public void test() throws Exception {
        SlopeDOX slopeDOX = new SlopeDOX(new StockRepository("D:\\stocks\\stocks"));
        initMeansAxis(slopeDOX);
        initSlopeAxis(slopeDOX);
        initSlopeMeanAxis(slopeDOX);
        initBuyThresholdAxis(slopeDOX);
        initSellThresholdAxis(slopeDOX);
        slopeDOX.setStartDate(START_DATE);
        slopeDOX.simulate(new File("D:\\stocks\\reports"));
    }

    private void initMeansAxis(SlopeDOX slopeDOX) {
        slopeDOX.meansWindowSize.setMin(100);
        slopeDOX.meansWindowSize.setMax(200);
        slopeDOX.meansWindowSize.setResolution(4);
    }

    private void initSlopeAxis(SlopeDOX slopeDOX) {
        slopeDOX.slopeWindowSize.setMin(900);
        slopeDOX.slopeWindowSize.setMax(1000);
        slopeDOX.slopeWindowSize.setResolution(2);
    }

    private void initSlopeMeanAxis(SlopeDOX slopeDOX) {
        slopeDOX.slopeMeanWindowSize.setMin(365 * 9);
        slopeDOX.slopeMeanWindowSize.setMax(365 * 11);
        slopeDOX.slopeMeanWindowSize.setResolution(3);
    }

    private void initBuyThresholdAxis(SlopeDOX slopeDOX) {
        slopeDOX.meanSlopeBuyThreshold.setMin(-0.5);
        slopeDOX.meanSlopeBuyThreshold.setMax(0.5);
        slopeDOX.meanSlopeBuyThreshold.setResolution(5);
    }

    private void initSellThresholdAxis(SlopeDOX slopeDOX) {
        slopeDOX.meanSlopeSellThreshold.setMin(-0.0005);
        slopeDOX.meanSlopeSellThreshold.setMax(-0.0015);
        slopeDOX.meanSlopeSellThreshold.setResolution(3);
    }
}