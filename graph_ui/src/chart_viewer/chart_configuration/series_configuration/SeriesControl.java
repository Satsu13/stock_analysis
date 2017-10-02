package chart_viewer.chart_configuration.series_configuration;

import day_util.DayUtil;
import immutable.ImmutableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import series_producer.ExpectedArgument;
import series_producer.Series;
import series_producer.SeriesProducer;
import stream.StreamUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SeriesControl extends GridPane {
    public final SeriesConfigurationPanel owner;
    public final String ticker;
    public final SeriesProducer seriesProducer;
    public final SimpleObjectProperty<List<TimeSeries>> timeSeriesProperty;

    private Button removeButton;
    private String seriesName;
    private CheckBox enabledControl;
    private ImmutableList<ArgumentControl> argumentControls;

    public SeriesControl(SeriesConfigurationPanel owner, String ticker, SeriesProducer seriesProducer) {
        this.owner = owner;
        this.ticker = ticker;
        this.seriesProducer = seriesProducer;
        timeSeriesProperty = new SimpleObjectProperty<>();
        initMembers();
        addMembers();
        updateTimeSeries();
    }

    private void initMembers() {
        initRemoveButton();
        seriesName = ticker + " - " + seriesProducer.getName();
        initEnabledControl();
        initArgumentControls();
    }

    private void initRemoveButton() {
        removeButton = new Button("X");
        removeButton.addEventHandler(ActionEvent.ACTION, buildRemoveButtonPressedListener());
    }

    private EventHandler<ActionEvent> buildRemoveButtonPressedListener() {
        return event -> {
              owner.removeSeriesControl(this);
        };
    }

    private void initEnabledControl() {
        enabledControl = new CheckBox(seriesName);
        enabledControl.setSelected(true);
        enabledControl.selectedProperty().addListener(buildEnabledChangedListener());
    }

    private ChangeListener<? super Boolean> buildEnabledChangedListener() {
        return (observable, oldValue, newValue) -> {
            if (newValue) {
                updateTimeSeries();
            } else {
                clearTimeSeries();
            }
        };
    }

    private void updateTimeSeries() {
        ImmutableList<Number> currentConfiguration = getCurrentConfiguration();
        List<Series> series = seriesProducer.produceSeries(ticker, currentConfiguration);
        List<TimeSeries> convertedSeries = StreamUtil.mapToList(series, this::convertToTimeSeries);
        timeSeriesProperty.setValue(convertedSeries);
    }

    private TimeSeries convertToTimeSeries(Series series) {
        TimeSeries timeSeries = new TimeSeries(series.name);
        for (LocalDate localDate : series.keySet()) {
            Day day = DayUtil.buildDay(localDate);
            Double value = series.get(localDate);
            timeSeries.add(day, value);
        }
        return timeSeries;
    }

    private ImmutableList<Number> getCurrentConfiguration() {
        LinkedList<Number> arguments = new LinkedList<>();
        for (ArgumentControl argumentControl : argumentControls) {
            Number argument = argumentControl.numberProperty().getValue();
            arguments.add(argument);
        }
        return new ImmutableList<>(arguments);
    }

    private void clearTimeSeries() {
        timeSeriesProperty.setValue(Collections.emptyList());
    }

    private void initArgumentControls() {
        LinkedList<ArgumentControl> argumentControls = new LinkedList<>();
        for (ExpectedArgument expectedArgument : seriesProducer.getExpectedArguments()) {
            ArgumentControl argumentControl = new ArgumentControl(expectedArgument);
            argumentControl.numberProperty().addListener(buildArgumentChangedListener());
            argumentControls.add(argumentControl);
        }
        this.argumentControls = new ImmutableList<>(argumentControls);
    }

    private ChangeListener<? super Number> buildArgumentChangedListener() {
        return (observable, oldValue, newValue) -> updateTimeSeries();
    }

    private void addMembers() {
        add(removeButton, 0, 0);
        add(enabledControl, 1, 0);
        addArgumentControls();
    }

    private void addArgumentControls() {
        int rowOffset = 1;
        for (ArgumentControl argumentControl : argumentControls) {
            add(argumentControl, 0, rowOffset, 2, 1);
            rowOffset++;
        }
    }
}
