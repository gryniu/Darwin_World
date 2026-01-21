package agh.ics.oop.presenter;

import agh.ics.oop.model.animal.Animal;
import agh.ics.oop.model.animal.AnimalOptions;
import agh.ics.oop.model.animal.EnergyOptions;
import agh.ics.oop.model.filesystem.CsvLogger;
import agh.ics.oop.model.filesystem.HistoryFileHandler;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.simulation.Simulation;
import agh.ics.oop.model.simulation.SimulationConfig;
import agh.ics.oop.model.*;
import agh.ics.oop.model.simulation.SimulationHistory;
import agh.ics.oop.view.MapRenderer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class SimulationPresenter implements Initializable {
    @FXML
    private Button forwardButton;

    @FXML
    private Button backButton;

    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;

    @FXML
    private Label dayLabel;
    @FXML
    private Label animalCountLabel;
    @FXML
    private Label plantCountLabel;
    @FXML
    private Label freeFieldsLabel;
    @FXML
    private Label popularGenotypeLabel;
    @FXML
    private Label averageEnergyLabel;
    @FXML
    private Label averageLifespanLabel;
    @FXML
    private Label averageChildrenLabel;

    @FXML CheckBox animalAddCheckBox;
    @FXML Label messageLabel;

    private Simulation simulation;
    private SimulationHistory simulationHistory;
    private RealWorldMap worldMap;

    public static final String RESET = "\u001B[0m";
    public static final String RED_BOLD = "\u001B[1;31m";


    BooleanProperty canRewind = new SimpleBooleanProperty(false);
    BooleanProperty canAddAnimal = new SimpleBooleanProperty(false);
    BooleanProperty simulationPaused = new SimpleBooleanProperty(false);



    @FXML private LineChart<Number, Number> statisticsChart;
    @FXML private NumberAxis dayAxis;
    @FXML private NumberAxis valueAxis;

    @FXML private CheckBox animalsChartCheckBox;
    @FXML private CheckBox plantsChartCheckBox;
    @FXML private CheckBox energyChartCheckBox;
    @FXML private CheckBox lifespanChartCheckBox;
    @FXML private CheckBox childrenChartCheckBox;
    @FXML private CheckBox freeFieldsChartCheckBox;

    @FXML
    private Canvas mapCanvas;

    private final XYChart.Series<Number, Number> animalsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> freeFieldsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> lifespanSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> childrenSeries = new XYChart.Series<>();
    private int visibleDay = 0;

    private AnimalOptions animalOptions;
    private MapOptions mapOptions;
    private EnergyOptions energyOptions;

    private MapStats latestMapStats;
    private MapRenderer mapRenderer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dayAxis.setAutoRanging(true);
        valueAxis.setAutoRanging(true);

        startButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.setPausedSimulation(false);
                canRewind.set(false);
                canAddAnimal.set(false);
                simulationPaused.set(false);
                animalAddCheckBox.setSelected(false);
            }
        });

        pauseButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.setPausedSimulation(true);
                canRewind.set(true);
                if (visibleDay == worldMap.getDay()) canAddAnimal.set(true);

                simulationPaused.set(true);
            }
        });

        backButton.disableProperty().bind(canRewind.not());
        forwardButton.disableProperty().bind(canRewind.not());
        animalAddCheckBox.disableProperty().bind(canAddAnimal.not());

        messageLabel.visibleProperty().bind(animalAddCheckBox.selectedProperty().and(simulationPaused));

        backButton.setOnAction(e -> {
            if (simulation != null) {
                simulationHistory.goBackTo(visibleDay-1);
                canAddAnimal.set(false);
                animalAddCheckBox.setSelected(false);
            }
        });

        forwardButton.setOnAction(e -> {
            if (simulation != null) {
                simulationHistory.goBackTo(visibleDay+1);
            }
        });
    }

    public void startSimulation(SimulationConfig config, boolean saveToCsv) {
        mapOptions = new MapOptions(
                config.mapHeight,
                config.mapWidth,
                config.startPlantCount,
                config.plantsPerDay,
                config.startAnimalCount,
                config.energyFromPlant
        );

        energyOptions = new EnergyOptions(
                config.energyFromPlant,
                config.energyLossPerDay,
                config.energyToReproduce,
                config.energyToKid);

        animalOptions = new AnimalOptions(
                energyOptions,
                config.minMutations,
                config.maxMutations,
                config.genomeLength
        );


        Function<Integer, Boolean> detectWinter;

        if(config.isSeasonal) {
            SeasonsOptions seasonsOptions = new SeasonsOptions(
                    config.seasonLength,
                    config.minTemperature,
                    config.distanceRequiredToHeat);
            this.worldMap = new SeasonalWorldMap(mapOptions, animalOptions, seasonsOptions);
            detectWinter = (day) -> (day / seasonsOptions.seasonLength()) % 2 == 1;
        }else{
            this.worldMap = new RealWorldMap(mapOptions, animalOptions);
            detectWinter = (day) -> Boolean.FALSE;
        }

        animalAddCheckBox.setVisible(config.isAnimalAdd);

        simulation = new Simulation(worldMap, 200);
        simulationHistory = new SimulationHistory(worldMap);
        simulationHistory.addSimulationChangeListener(this::handleSimulationChange);


        mapRenderer = new MapRenderer(mapCanvas, simulation, worldMap, detectWinter);
        mapRenderer.setMapFieldClickAction(this::handleMapFieldClick);

        // poczatkowe rysowanie mapy
        simulation.addSimulationChangeListener(simulationHistory::update);
        simulation.addSimulationChangeListener(this::handleSimulationChange);

        if (saveToCsv)
            simulation.addSimulationChangeListener(new CsvLogger(worldMap));

        simulation.startSimulation();

        freeFieldsSeries.setName("Ilość wolnych pól");
        animalsSeries.setName("Ilość zwierząt");
        plantsSeries.setName("Ilość roślin");
        energySeries.setName("Średnia Energia");
        lifespanSeries.setName("Średnia długość życia");
        childrenSeries.setName("Średnia liczba dzieci");

        Platform.runLater(this::updateCheckboxes);
    }

    public void handleSimulationChange(WorldMap worldMap, MapStats mapStats, int day, boolean isLive){
        visibleDay = day;
        javafx.application.Platform.runLater(() -> {
            mapRenderer.drawMap(worldMap, mapStats);
            updateLabels(mapStats, day);
            if(isLive) {
                updateLineChart(mapStats, day);
                latestMapStats = mapStats;
            }
        });
    }

    private void updateCheckboxes() {
        animalsChartCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!statisticsChart.getData().contains(animalsSeries))
                    statisticsChart.getData().add(animalsSeries);
            } else {
                statisticsChart.getData().remove(animalsSeries);
            }
        });

        plantsChartCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!statisticsChart.getData().contains(plantsSeries))
                    statisticsChart.getData().add(plantsSeries);
            } else {
                statisticsChart.getData().remove(plantsSeries);
            }
        });

        energyChartCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!statisticsChart.getData().contains(energySeries))
                    statisticsChart.getData().add(energySeries);
            } else {
                statisticsChart.getData().remove(energySeries);
            }
        });

        lifespanChartCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!statisticsChart.getData().contains(lifespanSeries))
                    statisticsChart.getData().add(lifespanSeries);
            } else {
                statisticsChart.getData().remove(lifespanSeries);
            }
        });

        childrenChartCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!statisticsChart.getData().contains(childrenSeries))
                    statisticsChart.getData().add(childrenSeries);
            } else {
                statisticsChart.getData().remove(childrenSeries);
            }
        });

        freeFieldsChartCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!statisticsChart.getData().contains(freeFieldsSeries))
                    statisticsChart.getData().add(freeFieldsSeries);
            } else {
                statisticsChart.getData().remove(freeFieldsSeries);
            }
        });
    }

    private void updateLineChart(MapStats mapStats, int day) {
        animalsSeries.getData().add(new XYChart.Data<>(day, mapStats.animalsCount()));

        plantsSeries.getData().add(new XYChart.Data<>(day, mapStats.plantsCount()));

        freeFieldsSeries.getData().add(new XYChart.Data<>(day, mapStats.freeFieldsCount()));

        energySeries.getData().add(new XYChart.Data<>(day, mapStats.averageEnergy()));

        lifespanSeries.getData().add(new XYChart.Data<>(day, mapStats.averageLifespan()));

        childrenSeries.getData().add(new XYChart.Data<>(day, mapStats.averageChildren()));

    }

    private void updateLabels(MapStats mapStats, int day){
        dayLabel.setText(String.valueOf(day));
        animalCountLabel.setText(mapStats.animalsCountStr());
        plantCountLabel.setText(mapStats.plantsCountStr());
        freeFieldsLabel.setText(mapStats.freeFieldsCountStr());
        averageEnergyLabel.setText(mapStats.averageEnergyStr());
        averageLifespanLabel.setText(mapStats.averageLifespanStr());
        averageChildrenLabel.setText(mapStats.averageChildrenStr());
        popularGenotypeLabel.setText(mapStats.mostPopularGenotype());
    }

    public void closeSimulation() {
        simulation.stopSimulation();
        HistoryFileHandler.deleteHistory(worldMap.getId());
    }

    private void handleMapFieldClick(Vector2d position) {
        if (visibleDay != worldMap.getDay()) return;
        if (animalAddCheckBox.isSelected())
            handleAnimalAdd(position);
        else
            handleShowAnimalStats(position);

    }

    private void handleAnimalAdd(Vector2d position){
        Animal animalToAdd = new Animal(position
                ,animalOptions
                ,mapOptions.energyStart()
                ,worldMap.getDay());
        worldMap.place(animalToAdd);
        mapRenderer.drawMap(worldMap, latestMapStats);
    }

    private void handleShowAnimalStats(Vector2d position){
        Optional<List<Animal>> animalsOnPosition = worldMap.getAnimalsOrdered(position);

        if (animalsOnPosition.isEmpty() || animalsOnPosition.get().isEmpty()) return; // nie ma żadnego Animala na pozycji
        Animal animal =  animalsOnPosition.get().getFirst();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AnimalStats.fxml"));
            BorderPane viewRoot = loader.load();
            AnimalStatsPresenter presenter = loader.getController();

            Stage stage = new Stage();
            stage.setOnCloseRequest(event -> {
                presenter.closeStatsWindow();
            });

            configureStage(stage,viewRoot, animal);
            stage.show();

            presenter.showAnimalStats(animal, worldMap, simulation);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot, Animal animal) {
        // stworzenie sceny (panelu do wyświetlania wraz zawartoscia z FXML)
        var scene = new Scene(viewRoot);

        // ustawienie sceny w oknie
        primaryStage.setScene(scene);

        // konfiguracja okna
        primaryStage.setTitle("Animal " + animal.getId() + " statistics");

    }
}