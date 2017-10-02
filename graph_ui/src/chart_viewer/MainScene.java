package chart_viewer;

import javafx.scene.Scene;
import repository.stock_repository.StockRepository;

import java.io.IOException;

public class MainScene extends Scene {
    public MainScene(StockRepository repository) throws IOException, ClassNotFoundException {
        super(new MainLayout(repository), 1080, 760);
    }
}
