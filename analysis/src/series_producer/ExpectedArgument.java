package series_producer;

public class ExpectedArgument {
    public final String name;
    public final Limit limit;
    public final int defaultValue;

    public ExpectedArgument(String name, Limit limit, int defaultValue) {
        this.name = name;
        this.limit = limit;
        this.defaultValue = defaultValue;
    }
}
