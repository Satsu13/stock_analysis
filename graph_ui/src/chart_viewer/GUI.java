package chart_viewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.stock_repository.StockRepository;

import java.io.IOException;

public class GUI extends Application {
    private StockRepository repository;

    public void launchTest(String sandBoxPath) {
        Application.launch(sandBoxPath);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            actuallyStart(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actuallyStart(Stage primaryStage) throws IOException, ClassNotFoundException {
        parseArguments();
        initPrimaryStage(primaryStage);
    }

    private void parseArguments() {
        Parameters parameters = getParameters();
        String repositoryPath = parameters.getRaw().get(0);
        repository = new StockRepository(repositoryPath);
    }

    private void initPrimaryStage(Stage primaryStage) throws IOException, ClassNotFoundException {
        primaryStage.setTitle("Stock Analysis");
        Scene scene = new MainScene(repository);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
