package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationConfig;
import agh.ics.oop.WorldGUI;
import agh.ics.oop.model.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SimulationPresenter {
    @FXML
    private Canvas simulationCanvas;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;

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

    private Simulation simulation;
    private Thread simulationThread;
    private boolean paused = false;
    private WorldMap worldMap;

    @FXML
    public void initialize() {
        startButton.setOnAction(e -> resumeSimulation());
        pauseButton.setOnAction(e -> paused = true);
    }


    public void startSimulation(SimulationConfig config){
        MapOptions mapOptions = new MapOptions(
                config.mapWidth,
                config.mapHeight,
                config.startPlantCount,
                config.startPlantCount,
                config.plantsPerDay,
                config.energyFromPlant
        );

        EnergyOptions energyOptions = new EnergyOptions(
                config.energyFromPlant,
                config.energyLossPerDay,
                config.energyToReproduce,
                config.energyToKid);

        AnimalOptions animalOptions = new AnimalOptions(
                energyOptions,
                config.maxMutations,
                config.genomeLength
        );

        this.worldMap = new RealWorldMap(mapOptions,animalOptions);

        simulation = new Simulation(worldMap, 200);

        simulation.addMapChangeListener((worldMap, message) -> {
            javafx.application.Platform.runLater(() -> {
                updateLabels(worldMap);
                drawMap(worldMap);
                //todo : logi
            });
        });


        simulationThread = new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();
    }



    private void resumeSimulation() {
        paused = false;
    }

    private void updateLabels(WorldMap worldMap){
    }

    private void drawMap(WorldMap worldMap) {
    }


}