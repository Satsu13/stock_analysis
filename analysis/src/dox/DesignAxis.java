package dox;

import immutable.ImmutableList;

import java.util.List;

import static static_math.Linspace.linspace;

public class DesignAxis {
    public final String name;
    public final String units;
    private double min;
    private double max;
    private int resolution;

    public DesignAxis(String name, String units) {
        this.name = name;
        this.units = units;
        min = 0;
        max = 0;
        resolution = 0;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution > 0 ? resolution : 0;
    }

    public List<Double> calculateValues() {
        if (resolution == 0) {
            return new ImmutableList<>();
        } else if (resolution == 1) {
            return new ImmutableList<>(min);
        } else {
            return new ImmutableList<>(linspace(min, max, resolution));
        }
    }
}
