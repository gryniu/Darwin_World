package agh.ics.oop.presenter;

import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationConfig;
import agh.ics.oop.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    private Simulation simulation;
    private RealWorldMap worldMap;

    public static final String RESET = "\u001B[0m";
    public static final String RED_BOLD = "\u001B[1;31m";

    // every cell is square
    private double cellSize = 40.0; // every cell is square
    private double borderWidth = 1.67;
    private double gridOffset = cellSize * 1.5;
    private double coordsFontSize = 20.0;
    private double fontSize = cellSize*0.3;
    private double energyBarWidth = cellSize*0.8;
    private double energyBarHeight =  energyBarWidth *0.15;
    private GraphicsContext gc;

    BooleanProperty canRewind = new SimpleBooleanProperty(false);

    @FXML private LineChart<Number, Number> statisticsChart;
    @FXML private NumberAxis dayAxis;
    @FXML private NumberAxis valueAxis;

    @FXML private CheckBox animalsChartCheckBox;
    @FXML private CheckBox plantsChartCheckBox;
    @FXML private CheckBox energyChartCheckBox;
    @FXML private CheckBox lifespanChartCheckBox;
    @FXML private CheckBox childrenChartCheckBox;
    @FXML private CheckBox freeFieldsChartCheckBox;

    private final XYChart.Series<Number, Number> animalsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> freeFieldsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> lifespanSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> childrenSeries = new XYChart.Series<>();

    private List<Image> animalImages = new ArrayList<>();
    private Image plantImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i = 0; i<8; i++){
            animalImages.add(new Image(
                            getClass().getResourceAsStream("/images/animal%d.png".formatted(i))
                    ));
        }
        plantImage = new Image(getClass().getResourceAsStream("/images/plant.png"));
;
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

        mapCanvas.setOnMouseClicked(event -> {
            if (simulation == null || !simulation.isPaused()) return;

            handleCanvasClick(event.getX(), event.getY());
        });
    }

    public void startSimulation(SimulationConfig config, boolean saveToCsv) {
        MapOptions mapOptions = new MapOptions(
                config.mapHeight,
                config.mapWidth,
                config.startPlantCount,
                config.plantsPerDay,
                config.startAnimalCount,
                config.energyFromPlant
        );

        EnergyOptions energyOptions = new EnergyOptions(
                config.energyFromPlant,
                config.energyLossPerDay,
                config.energyToReproduce,
                config.energyToKid);

        AnimalOptions animalOptions = new AnimalOptions(
                energyOptions,
                config.minMutations,
                config.maxMutations,
                config.genomeLength
        );

        if(config.isSeasonal) {
            SeasonsOptions seasonsOptions = new SeasonsOptions(
                    config.seasonLength,
                    config.minTemperature,
                    config.distanceRequiredToHeat);
            this.worldMap = new SeasonalWorldMap(mapOptions, animalOptions, seasonsOptions);
        }else{
            this.worldMap = new RealWorldMap(mapOptions, animalOptions);
        }
        cellSize = Math.min(worldMap.getWidth(), worldMap.getHeight()) * (440.0/11.0);
        updateFields();

        simulation = new Simulation(worldMap, 200);
        mapCanvas.setWidth(worldMap.getWidth() * cellSize + 2 * gridOffset);
        mapCanvas.setHeight(worldMap.getHeight() * cellSize + 2 * gridOffset);
        Platform.runLater(this::updateCheckboxes);

        // poczatkowe rysowanie mapy
        handleSimulationChange(this.worldMap, 0, true);
        simulation.addMapChangeListener(this::handleSimulationChange);

        if (saveToCsv)
            simulation.addMapChangeListener(new CsvLogger(worldMap));

        simulation.startSimulation();

        freeFieldsSeries.setName("Ilość wolnych pól");
        animalsSeries.setName("Ilość zwierząt");
        plantsSeries.setName("Ilość roślin");
        energySeries.setName("Średnia Energia");
        lifespanSeries.setName("Średnia długość życia");
        childrenSeries.setName("Średnia liczba dzieci");
    }

    public void handleSimulationChange(WorldMap worldMap, int day, boolean isLive){
        javafx.application.Platform.runLater(() -> {
            drawMap(worldMap, day);

            MapStats mapStats = simulation.getStats(day);
            updateLabels(mapStats, day);
            if(isLive)
                updateLineChart(mapStats, day);
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

    private void drawMap(WorldMap worldMap, int day) {
        clearGrid();
        drawGrid();
        drawWorldElements(worldMap, day);
    }

    private void drawWorldElements(WorldMap worldMap, int day){
        gc.save();
        gc.setStroke(Color.BLACK);
        configureFont(gc, fontSize, Color.BLACK);

        for (WorldElement worldElement: worldMap.getAllMapElements()){
            Vector2d pos = worldElement.position();

            double centerX = gridOffset + pos.x() * cellSize + cellSize / 2;
            double posX = gridOffset + pos.x() * cellSize;

            // W JAVIEFX Y JEST NA GORZE!!11!11!
            int worldY = pos.y();
            int flippedY = worldMap.getHeight() - 1 - worldY;

            double centerY = gridOffset + flippedY * cellSize + cellSize / 2;
            double posY = gridOffset + flippedY * cellSize;

            if (worldElement instanceof AbstractAnimal abstractAnimal) {
                gc.save();
                if (Objects.equals(abstractAnimal.getGen().toString(), simulation.getStats(day).mostPopularGenotype())){
                    gc.setFill(Color.rgb(255, 0, 255, 0.5));
                    gc.fillOval(posX, posY, cellSize, cellSize);
                }
                gc.drawImage(animalImages.get(abstractAnimal.getOrientation().ordinal()), posX, posY, cellSize, cellSize);
                gc.restore();
                drawEnergyBar(gc, abstractAnimal, centerX, centerY, day);

            }else gc.drawImage(plantImage, posX, posY, cellSize, cellSize);
        }
        gc.restore();
    }

    private void drawGrid(){
        gc.save();

        gc.setFill(Color.BLACK);
        gc.setLineWidth(borderWidth);
        // poziome
        for (int row = 0; row <= worldMap.getHeight(); row++) {
            double y = gridOffset + row * cellSize;
            gc.strokeLine(gridOffset, y, gridOffset + worldMap.getWidth() * cellSize, y);
        }

        // pionowe
        for (int col = 0; col <= worldMap.getWidth(); col++) {
            double x = gridOffset + col * cellSize;
            gc.strokeLine(x, gridOffset, x, gridOffset + worldMap.getHeight() * cellSize);
        }


        //coords
        gc.setFont(new Font("Arial", coordsFontSize));
        drawCoords();

        gc.restore();
    }

    private void clearGrid() {
        gc.save();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        for (int canvasCol = 0; canvasCol < worldMap.getWidth(); canvasCol++) {
            for (int canvasRow = 0; canvasRow < worldMap.getHeight(); canvasRow++) {

                Vector2d worldPosition = new Vector2d(canvasCol, canvasRow);

                Color fieldColor;
                if (this.worldMap instanceof SeasonalWorldMap seasonalWorldMap) {
                    fieldColor = seasonalWorldMap.getColorOfField(worldPosition);
                } else {
                    fieldColor = worldMap.getColorOfField(worldPosition);
                }

                gc.setFill(fieldColor);
                gc.fillRect(gridOffset + canvasCol * cellSize,
                        gridOffset + canvasRow * cellSize,
                        cellSize, cellSize);
            }
        }
        gc.restore();
    }

    public void closeSimulation() {
        simulation.stopSimulation();
    }

    private void drawCoords() {
        double y = gridOffset;
        while (y < mapCanvas.getHeight() - gridOffset) {
            gc.fillText(String.valueOf((int) ((mapCanvas.getHeight() - gridOffset - y) / cellSize - 1)),
                    gridOffset / 2 - fontSize/2,
                    y + cellSize / 2 + borderWidth + fontSize / 4
            );
            y += cellSize;
        }

        double x = gridOffset;
        while (x < mapCanvas.getWidth() - gridOffset) {
            gc.fillText(
                    String.valueOf((int) ((x - gridOffset) / cellSize)),
                    x + cellSize / 2 + borderWidth - fontSize / 2,
                    mapCanvas.getHeight() - gridOffset / 2 + fontSize/4
            );
            x += cellSize;
        }
    }

    private void drawEnergyBar(GraphicsContext gc, AbstractAnimal animal, double centerX, double centerY, int day) {
        int p85 = simulation.getStats(day).p85();
        int median = simulation.getStats(day).p50();

        double ratio = animal.getEnergyRatio(median, p85);
        double width = energyBarWidth * ratio;

        double x = centerX - cellSize / 2 + (cellSize - energyBarWidth) / 2;
        double y = centerY + cellSize / 2 - energyBarHeight - 2.5;

        gc.save();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(x, y, energyBarWidth, energyBarHeight, energyBarHeight, energyBarHeight);

        gc.setFill(animal.getEnergyColor(p85));
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
        double cellWidth = (mapCanvas.getWidth() - 2 * gridOffset) / worldMap.getWidth();
        double cellHeight = (mapCanvas.getHeight() - 2 * gridOffset) / worldMap.getHeight();
        cellSize = Math.min(cellWidth, cellHeight);

        borderWidth = cellSize/24.0;
        coordsFontSize = cellSize/2.0;
        gridOffset = cellSize * 1.5;
        fontSize = cellSize*0.3;
        energyBarWidth = cellSize*0.8;
        energyBarHeight =  energyBarWidth *0.15;
    }

    private void handleCanvasClick(double mouseX, double mouseY){
        if (mouseX < gridOffset || mouseY < gridOffset) return;
        if (mouseX > gridOffset + worldMap.getWidth() * cellSize) return;
        if (mouseY > gridOffset + worldMap.getHeight() * cellSize) return;

        int col = (int) ((mouseX-gridOffset)/cellSize);
        int rowFromTop = (int) ((mouseY - gridOffset) / cellSize);

        int row = worldMap.getHeight() - 1 - rowFromTop;

        handleMapFieldClick(col, row);
    }

    private void handleMapFieldClick(int x, int y) {
        Vector2d position = new Vector2d(x, y);
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
            simulation.addMapChangeListener(presenter::updateTextFields);

        } catch (IOException e) {
            throw new RuntimeException(e);
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