package dox.test;

import dox.DesignAxis;
import org.junit.Test;
import repository.stock_repository.StockRepository;

import java.io.File;

public class TestDoxTest {

    @Test
    public void test1() throws Exception {
        TestDox testDox = new TestDox(new StockRepository("D:\\stocks\\stocks"));
        for (int i = 0; i < 5; i++) {
            testDox.getDesignAxis().add(buildDesignAxis());
        }
        testDox.simulate(new File(""));
    }

    private DesignAxis buildDesignAxis() {
        DesignAxis designAxis = new DesignAxis("", "");
        designAxis.setMin(1);
        designAxis.setMax(5);
        designAxis.setResolution(5);
        return designAxis;
    }
}