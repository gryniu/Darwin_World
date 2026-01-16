package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationConfig;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SimulationPresenter implements Initializable {
    @FXML
    private Scene scene;
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

    // every cell is square
    private double cellSize = 40.0; // every cell is square
    private double borderWidth = 1.67;
    private double borderOffset = borderWidth /2.0;
    private final static double GRID_OFFSET = 50.0;
    private double coordsFontSize = 20.0;
    private double fontSize = cellSize*0.5;
    private double energyBarWidth = cellSize*0.8;
    private double energyBarHeight =  energyBarWidth *0.15;
    private GraphicsContext gc;


    @FXML
    private LineChart<Number, Number> statisticsChart;
    @FXML
    private NumberAxis dayAxis;
    @FXML
    private NumberAxis valueAxis;

    @FXML
    private CheckBox animalsChartCheckBox;
    @FXML
    private CheckBox plantsChartCheckBox;
    @FXML
    private CheckBox energyChartCheckBox;
    @FXML
    private CheckBox lifespanChartCheckBox;
    @FXML
    private CheckBox childrenChartCheckBox;
    private final XYChart.Series<Number, Number> animalsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> lifespanSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> childrenSeries = new XYChart.Series<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dayAxis.setAutoRanging(true);
        valueAxis.setAutoRanging(true);

        gc = mapCanvas.getGraphicsContext2D();

        startButton.setOnAction(e -> {
            if (simulation != null) simulation.setPausedSimulation(false);

        });
        pauseButton.setOnAction(e -> {
            if (simulation != null) simulation.setPausedSimulation(true);
        });


    }

    public void startSimulation(SimulationConfig config) {
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

        SeasonsOptions seasonsOptions = new SeasonsOptions(100, -5, 3);
        this.worldMap = new SeasonalWorldMap(mapOptions, animalOptions, seasonsOptions);

        Boundary boundary = worldMap.getCurrentBounds();
        gridWidth = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        gridHeight = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;

        simulation = new Simulation(worldMap, 200);
        mapCanvas.setWidth(gridWidth*cellSize + 2*GRID_OFFSET);
        mapCanvas.setHeight(gridHeight*cellSize + 2*GRID_OFFSET);
//        mapCanvas.widthProperty().addListener((obs, oldVal, newVal) -> updateFields());
//        mapCanvas.heightProperty().addListener((obs, oldVal, newVal) -> updateFields());

        Platform.runLater(this::updateCheckboxes);

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


    private void updateLabels(AbstractWorldMap worldMap) {
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

    private void drawWorldElements(AbstractWorldMap worldMap) {
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

        for (WorldElement worldElement : elements) { // todo: nie korzystac z objectAt
            if (!(worldElement instanceof Animal)) { //  w pierwszej kolejnosci wyswietlamy Animala
                Optional<WorldElement> elementAtPos = worldMap.objectAt(worldElement.position());

                if (elementAtPos.isPresent() && elementAtPos.get() instanceof Animal) {
                    continue;
                }
            }
            Vector2d pos = worldElement.position();

            double centerX = GRID_OFFSET + (pos.getX() - offsetX) * cellSize + cellSize / 2;

            // W JAVIEFX Y JEST NA GORZE!!11!11!
            int worldY = pos.getY() - offsetY;
            int flippedY = gridHeight - 1 - worldY;

            double centerY = GRID_OFFSET + flippedY * cellSize + cellSize / 2;

            gc.fillText(worldElement.toString(), centerX, centerY);

            // rysowanie energy Bara
            if (worldElement instanceof Animal) {
                drawEnergyBar(gc, (Animal) worldElement, centerX, centerY);
            }
        }
        gc.restore();
    }




    private void drawGrid(AbstractWorldMap worldMap) {
        gc.save();

        gc.setFill(Color.BLACK);
        gc.setLineWidth(borderWidth);
        // poziome
        for (int row = 0; row <= gridHeight; row++) {
            double y = GRID_OFFSET + row * cellSize;
            gc.strokeLine(GRID_OFFSET, y, GRID_OFFSET + gridWidth * cellSize, y);
        }

        // pionowe
        for (int col = 0; col <= gridWidth; col++) {
            double x = GRID_OFFSET + col * cellSize;
            gc.strokeLine(x, GRID_OFFSET, x, GRID_OFFSET + gridHeight * cellSize);
        }

        //coords
        gc.setFont(new Font("Arial", coordsFontSize));
        drawCoords();

        gc.restore();
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

    private void drawCoords() {
        double y = GRID_OFFSET;
        while (y < mapCanvas.getHeight() - GRID_OFFSET) {
            gc.fillText(String.valueOf((int) ((mapCanvas.getHeight() - GRID_OFFSET - y) / cellSize - 1)),
                    GRID_OFFSET / 2,
                    y + cellSize / 2 + borderWidth + fontSize / 4
            );
            y += cellSize;
        }

        double x = GRID_OFFSET;
        while (x < mapCanvas.getWidth() - GRID_OFFSET) {
            gc.fillText(
                    String.valueOf((int) ((x - GRID_OFFSET) / cellSize)),
                    x + cellSize / 2 + borderWidth - fontSize / 4,
                    mapCanvas.getHeight() - GRID_OFFSET / 2
            );
            x += cellSize;
        }
    }

    private void drawEnergyBar(GraphicsContext gc, Animal animal, double centerX, double centerY) {
        gc.save();
        int energyPercentile = worldMap.getEnergyPercentile(85);
        double ratio = animal.getEnergyRatio(energyPercentile);
        double width = energyBarWidth * ratio;

        double x = centerX - cellSize / 2 + (cellSize - energyBarWidth) / 2;
        double y = centerY + cellSize / 2 - energyBarHeight - 2.5;

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(x, y, energyBarWidth, energyBarHeight, energyBarHeight, energyBarHeight);

        gc.setFill(animal.getEnergyColor(energyPercentile));
        gc.fillRoundRect(x, y, width, energyBarHeight, energyBarHeight, energyBarHeight);

        gc.setStroke(Color.DARKGRAY);
        gc.strokeRoundRect(x, y, energyBarWidth, energyBarHeight, energyBarHeight, energyBarHeight);

        gc.restore();
    }

    private void configureFont(GraphicsContext graphics, double size, Color color) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font("Arial", size));
        graphics.setFill(color);
    }

    private void updateFields(){
        double cellWidth = (mapCanvas.getWidth() - 2 * GRID_OFFSET) / gridWidth;
        double cellHeight = (mapCanvas.getHeight() - 2 * GRID_OFFSET) / gridHeight;
        cellSize = Math.min(cellWidth, cellHeight);

        borderWidth = cellSize/24.0;
        borderOffset = borderWidth /2.0;
        coordsFontSize = cellSize/2.0;
        fontSize = cellSize*0.5;
        energyBarWidth = cellSize*0.8;
        energyBarHeight =  energyBarWidth *0.15;

        if (worldMap != null){
            javafx.application.Platform.runLater(() -> {
                drawMap(worldMap);
                //todo : logi
            });
        }
    }
}