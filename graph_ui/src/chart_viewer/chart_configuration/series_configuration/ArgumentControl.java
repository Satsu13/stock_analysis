package chart_viewer.chart_configuration.series_configuration;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import series_producer.ExpectedArgument;
import ui_control.SpinnerSliderPane;

public class ArgumentControl extends GridPane {
    private Label name;
    private SpinnerSliderPane spinnerSliderPane;

    public ArgumentControl(ExpectedArgument expectedArgument) {
        initMembers(expectedArgument);
        addMembers();
    }

    private void initMembers(ExpectedArgument expectedArgument) {
        name = new Label(expectedArgument.name + ":  ");
        spinnerSliderPane = new SpinnerSliderPane(
                expectedArgument.limit.minimum,
                expectedArgument.limit.maximum,
                expectedArgument.defaultValue);
    }

    private void addMembers() {
        add(name, 0, 0);
        add(spinnerSliderPane, 1, 0);
    }

    public ObservableValue<Integer> numberProperty() {
        return spinnerSliderPane.numberProperty;
    }
}
