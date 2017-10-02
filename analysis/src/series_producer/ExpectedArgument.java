package series_producer;

public class ExpectedArgument {
    public final String name;
    public final Limits limits;
    public final int defaultValue;

    public ExpectedArgument(String name, Limits limits, int defaultValue) {
        this.name = name;
        this.limits = limits;
        this.defaultValue = defaultValue;
    }
}
