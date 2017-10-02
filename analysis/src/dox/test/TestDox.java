package dox.test;

import dox.DOX;
import dox.DOXReport;
import dox.DesignAxis;
import dox.Experiment;
import repository.stock_repository.StockRepository;

import java.io.File;
import java.util.LinkedList;

public class TestDox extends DOX {
    public TestDox(StockRepository repository) {
        super(repository);
    }

    @Override
    protected DOXReport getDoxReport(File doxReportDirectory) {
        return new TestDoxReport(doxReportDirectory);
    }

    @Override
    protected Experiment getExperiment() {
        return new TestExperiment(repository);
    }

    @Override
    public LinkedList<DesignAxis> getDesignAxis() {
        return super.getDesignAxis();
    }
}
