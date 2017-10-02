package series_producer;

import day_util.DayUtil;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class Series extends TreeMap<LocalDate, Double> {
    public final String ticker;
    public final String name;
    public final String yAxisUnits;

    public Series() {
        this("", "", "");
    }

    public Series(String ticker, String name, String yAxisUnits) {
        this.ticker = ticker;
        this.name = name;
        this.yAxisUnits = yAxisUnits;
    }

    public Series(Map<? extends LocalDate, ? extends Double> m, String ticker, String name, String yAxisUnits) {
        super(m);
        this.ticker = ticker;
        this.name = name;
        this.yAxisUnits = yAxisUnits;
    }

    public TimeSeries toTimeSeries() {
        TimeSeries timeSeries = new TimeSeries(name);
        for (LocalDate localDate : keySet()) {
            Day convertedDay = DayUtil.buildDay(localDate);
            timeSeries.add(convertedDay, get(localDate));
        }
        return timeSeries;
    }
}
