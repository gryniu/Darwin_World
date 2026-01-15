package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationConfig;
import agh.ics.oop.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SimulationPresenter implements Initializable {
    @FXML
    private Button forwardButton;

    @FXML
    private Button backButton;

    @FXML
    private Canvas mapCanvas;
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
    private AbstractWorldMap worldMap;
    private int gridWidth;
    private int gridHeight;
    private final static int CELL_SIZE = 40; // every cell is square
    private final static double BORDER_WIDTH = 1.67;
    private final static double BORDER_OFFSET = BORDER_WIDTH/2;
    private final static int GRID_OFFSET = 60;
    private final static int BOTTOM_MARGIN = 25;
    private final static int COORDS_FONT_SIZE = 20;
    private GraphicsContext gc;
    private int fontSize = (int)(CELL_SIZE*0.5);

    BooleanProperty canRewind = new SimpleBooleanProperty(false);
  
    @FXML private LineChart<Number, Number> statisticsChart;
    @FXML private NumberAxis dayAxis;
    @FXML private NumberAxis valueAxis;

    @FXML private CheckBox animalsChartCheckBox;
    @FXML private CheckBox plantsChartCheckBox;
    @FXML private CheckBox energyChartCheckBox;
    @FXML private CheckBox lifespanChartCheckBox;
    @FXML private CheckBox childrenChartCheckBox;
    private XYChart.Series<Number, Number> animalsSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> plantsSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> lifespanSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> childrenSeries = new XYChart.Series<>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dayAxis.setAutoRanging(true);
        valueAxis.setAutoRanging(true);
        gc = mapCanvas.getGraphicsContext2D();

        startButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.setPausedSimulation(false);
                canRewind.set(false);
            }
        });

        pauseButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.setPausedSimulation(true);
                canRewind.set(true);
            }
        });

        backButton.disableProperty().bind(canRewind.not());
        forwardButton.disableProperty().bind(canRewind.not());

        backButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.rewind(true);
            }
        });

        forwardButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.rewind(false);
            }
        });

    }
    public void startSimulation(SimulationConfig config){
        MapOptions mapOptions = new MapOptions(
                config.mapHeight,
                config.mapWidth,
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

        SeasonsOptions seasonsOptions = new SeasonsOptions(100,-5,3);
        this.worldMap = new SeasonalWorldMap(mapOptions,animalOptions,seasonsOptions);

        Boundary boundary = worldMap.getCurrentBounds();
        gridWidth = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        gridHeight = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;

        mapCanvas.setWidth(gridWidth*CELL_SIZE + 2*GRID_OFFSET);
        mapCanvas.setHeight(gridHeight*CELL_SIZE + 2*GRID_OFFSET);

        simulation = new Simulation(worldMap, 200);
      
        Platform.runLater(this::updateCheckboxes);;

        // poczatkowe rysowanie mapy
        javafx.application.Platform.runLater(() -> {
            updateLineChart();
            updateLabels(worldMap);
            drawMap(worldMap);
            //todo : logi
        });
        simulation.addMapChangeListener((worldMap, message) -> {
            javafx.application.Platform.runLater(() -> {
                updateLineChart();
                updateLabels(worldMap);
                drawMap(worldMap);
                //todo : logi
            });
        });
      
        simulation.addMapChangeListener(new SimulationLogger());
        simulation.setPausedSimulation(false);

        simulationThread = new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();


        animalsSeries.setName("Ilość zwierząt");
        plantsSeries.setName("Ilość roślin");
        energySeries.setName("Średnia Energia");
        lifespanSeries.setName("Średnia długość życia");
        childrenSeries.setName("Średnia liczba dzieci");




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
    }


    private void updateLineChart() {
        int day = simulation.getCurrentDay();

        animalsSeries.getData().add(new XYChart.Data<>(day, worldMap.getAnimalsCount()));

        plantsSeries.getData().add(new XYChart.Data<>(day, worldMap.getPlantsCount()));

        energySeries.getData().add(new XYChart.Data<>(day, worldMap.getAverageEnergy()));

        lifespanSeries.getData().add(new XYChart.Data<>(day, worldMap.getAverageLifespan()));

        childrenSeries.getData().add(new XYChart.Data<>(day, worldMap.getAverageChildren()));

    }


    private void updateLabels(AbstractWorldMap worldMap){
        animalCountLabel.setText(String.valueOf(worldMap.getAnimalsCount()));
        plantCountLabel.setText(String.valueOf(worldMap.getPlantsCount()));
        freeFieldsLabel.setText(String.valueOf(worldMap.getFreeFieldsCount()));
        popularGenotypeLabel.setText(String.valueOf(worldMap.getMostPopularGenotype()));
        averageEnergyLabel.setText(String.format("%.2f", worldMap.getAverageEnergy()));
        averageLifespanLabel.setText(String.format("%.2f", worldMap.getAverageLifespan()));
        averageChildrenLabel.setText(String.format("%.2f", worldMap.getAverageChildren()));
    }

    private void drawMap(AbstractWorldMap worldMap) {
        clearGrid();
        drawGrid(worldMap);
        drawWorldElements(worldMap);
    }

    private void drawWorldElements(WorldMap worldMap){
        gc.save();
        gc.setStroke(Color.BLACK);
        configureFont(gc, fontSize, Color.BLACK);

        Boundary boundary = worldMap.getCurrentBounds();
        int offsetX = boundary.lowerLeft().getX();
        int offsetY = boundary.lowerLeft().getY();

        // todo: w RealWorldMap dodac metodę getAllMapElements()
        List<WorldElement> elements = new ArrayList<>();
        elements.addAll(worldMap.getPlants());
        elements.addAll(worldMap.getAllAnimals());

        for (WorldElement worldElement: elements){
            Vector2d pos = worldElement.position();

            double centerX = GRID_OFFSET + (pos.getX() - offsetX) * CELL_SIZE + CELL_SIZE/2 ;

            // W JAVIEFX Y JEST NA GORZE!!11!11!
            int worldY = pos.getY() - offsetY;
            int flippedY = gridHeight - 1 - worldY;

            double centerY = GRID_OFFSET + flippedY * CELL_SIZE + CELL_SIZE/2 ;

            gc.fillText(worldElement.toString(), centerX, centerY);
        }
        gc.restore();
    }

    private void drawGrid(WorldMap worldMap){
        gc.save();

        gc.setFill(Color.BLACK);
        gc.setLineWidth(BORDER_WIDTH);
        // poziome
        for (int row = 0; row <= gridHeight; row++) {
            double y = GRID_OFFSET + row * CELL_SIZE;
            gc.strokeLine(GRID_OFFSET, y, GRID_OFFSET + gridWidth*CELL_SIZE, y);
        }

        // pionowe
        for (int col = 0; col <= gridWidth; col++) {
            double x = GRID_OFFSET + col * CELL_SIZE;
            gc.strokeLine(x, GRID_OFFSET, x, GRID_OFFSET + gridHeight*CELL_SIZE);
        }


        //coords
        gc.setFont(new Font("Arial", COORDS_FONT_SIZE));

        //yCoords
        for (int y = GRID_OFFSET;y<mapCanvas.getHeight() - GRID_OFFSET;y+=CELL_SIZE){
            gc.fillText(String.valueOf((int)(mapCanvas.getHeight()-GRID_OFFSET-y)/CELL_SIZE -1),
                    GRID_OFFSET / 2,
                    y + CELL_SIZE/2 + BORDER_WIDTH + fontSize/4
            );
        }

        //xCoords
        for (int x = GRID_OFFSET;x<mapCanvas.getWidth() - GRID_OFFSET;x+=CELL_SIZE){
            gc.fillText(
                    String.valueOf((int)(x-GRID_OFFSET)/CELL_SIZE),
                    x + CELL_SIZE/2 + BORDER_WIDTH - fontSize/4,
                    mapCanvas.getHeight() - GRID_OFFSET/2
            );
        }

        gc.restore();
    }


    private void configureFont(GraphicsContext graphics, int size, Color black) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font("Arial", size));
        graphics.setFill(black);
    }

    private void clearGrid() {
        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    public void stopSimulation() {
        if (simulation != null) {
            simulation.setPausedSimulation(true);
            if (simulationThread != null && simulationThread.isAlive()) {
                simulationThread.interrupt();
            }
        }
    }

}