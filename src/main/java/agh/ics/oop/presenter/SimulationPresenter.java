package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationConfig;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
    private AbstractWorldMap worldMap;
    private int gridWidth;
    private int gridHeight;

    // every cell is square
    private double cellSize = 40.0; // every cell is square
    private double borderWidth = 1.67;
    private double borderOffset = borderWidth /2.0;
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

        mapCanvas.setOnMouseClicked(event -> {
            if (!(worldMap instanceof LivingWorldMap)) return;
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
                config.maxMutations,
                config.genomeLength
        );

        SeasonsOptions seasonsOptions = new SeasonsOptions(100, -5, 3);
        this.worldMap = new SeasonalWorldMap(mapOptions, animalOptions, seasonsOptions);

        Boundary boundary = worldMap.getCurrentBounds();
        gridWidth = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        gridHeight = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;
        cellSize = Math.min(gridWidth, gridHeight) * (440.0/11.0);
        updateFields();

        simulation = new Simulation(worldMap, 200);
        mapCanvas.setWidth(gridWidth * cellSize + 2 * gridOffset);
        mapCanvas.setHeight(gridHeight * cellSize + 2 * gridOffset);
        Platform.runLater(this::updateCheckboxes);

        // poczatkowe rysowanie mapy
        handleSimulationChange(this.worldMap, "starting state");
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

    public void handleSimulationChange(WorldMap worldMap, String message){
        javafx.application.Platform.runLater(() -> {
            MapStats mapStats = worldMap.getMapStats();
            if(worldMap instanceof LivingWorldMap)
                updateLineChart(mapStats, message);
            updateLabels(mapStats, message);
            drawMap(worldMap);
            //todo : logi
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


    private void updateLineChart(MapStats mapStats, String message) {
        int day = Integer.parseInt(message);

        animalsSeries.getData().add(new XYChart.Data<>(day, mapStats.animalsCount()));

        plantsSeries.getData().add(new XYChart.Data<>(day, mapStats.plantsCount()));

        freeFieldsSeries.getData().add(new XYChart.Data<>(day, mapStats.freeFieldsCount()));

        energySeries.getData().add(new XYChart.Data<>(day, mapStats.averageEnergy()));

        lifespanSeries.getData().add(new XYChart.Data<>(day, mapStats.averageLifespan()));

        childrenSeries.getData().add(new XYChart.Data<>(day, mapStats.averageChildren()));

    }


    private void updateLabels(MapStats mapStats, String day){
        dayLabel.setText(day);
        animalCountLabel.setText(mapStats.animalsCountStr());
        plantCountLabel.setText(mapStats.plantsCountStr());
        freeFieldsLabel.setText(mapStats.freeFieldsCountStr());
        averageEnergyLabel.setText(mapStats.averageEnergyStr());
        averageLifespanLabel.setText(mapStats.averageLifespanStr());
        averageChildrenLabel.setText(mapStats.averageChildrenStr());
        popularGenotypeLabel.setText(mapStats.mostPopularGenotype());
    }

    private void drawMap(WorldMap worldMap) {
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

        for (WorldElement worldElement: worldMap.getAllMapElements()){
            if (!(worldElement instanceof AbstractAnimal) && worldMap.getAnimals(worldElement.position()).isPresent())
                continue;
            Vector2d pos = worldElement.position();

            double centerX = gridOffset + (pos.getX() - offsetX) * cellSize + cellSize / 2;

            // W JAVIEFX Y JEST NA GORZE!!11!11!
            int worldY = pos.getY() - offsetY;
            int flippedY = gridHeight - 1 - worldY;

            double centerY = gridOffset + flippedY * cellSize + cellSize / 2;

            gc.fillText(worldElement.toString(), centerX, centerY);

            // rysowanie energy Bara
            if (worldElement instanceof AbstractAnimal ) {
                drawEnergyBar(gc, (AbstractAnimal) worldElement, centerX, centerY);
            }
        }
        gc.restore();
    }

    private void drawGrid(WorldMap worldMap){
        gc.save();

        gc.setFill(Color.BLACK);
        gc.setLineWidth(borderWidth);
        // poziome
        for (int row = 0; row <= gridHeight; row++) {
            double y = gridOffset + row * cellSize;
            gc.strokeLine(gridOffset, y, gridOffset + gridWidth * cellSize, y);
        }

        // pionowe
        for (int col = 0; col <= gridWidth; col++) {
            double x = gridOffset + col * cellSize;
            gc.strokeLine(x, gridOffset, x, gridOffset + gridHeight * cellSize);
        }


        //coords
        gc.setFont(new Font("Arial", coordsFontSize));
        drawCoords();

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

    public void closeSimulation() {
        simulation.stopSimulation();
    }

    private void drawCoords() {
        double y = gridOffset;
        while (y < mapCanvas.getHeight() - gridOffset) {
            gc.fillText(String.valueOf((int) ((mapCanvas.getHeight() - gridOffset - y) / cellSize - 1)),
                    gridOffset / 2 - fontSize/4,
                    y + cellSize / 2 + borderWidth + fontSize / 4
            );
            y += cellSize;
        }

        double x = gridOffset;
        while (x < mapCanvas.getWidth() - gridOffset) {
            gc.fillText(
                    String.valueOf((int) ((x - gridOffset) / cellSize)),
                    x + cellSize / 2 + borderWidth - fontSize / 4,
                    mapCanvas.getHeight() - gridOffset / 2 + fontSize/4
            );
            x += cellSize;
        }
    }

    private void drawEnergyBar(GraphicsContext gc, AbstractAnimal animal, double centerX, double centerY) {
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
        double cellWidth = (mapCanvas.getWidth() - 2 * gridOffset) / gridWidth;
        double cellHeight = (mapCanvas.getHeight() - 2 * gridOffset) / gridHeight;
        cellSize = Math.min(cellWidth, cellHeight);

        borderWidth = cellSize/24.0;
        borderOffset = borderWidth /2.0;
        coordsFontSize = cellSize/2.0;
        gridOffset = cellSize * 1.5;
        fontSize = cellSize*0.3;
        energyBarWidth = cellSize*0.8;
        energyBarHeight =  energyBarWidth *0.15;

        if (worldMap != null){
            javafx.application.Platform.runLater(() -> {
                drawMap(worldMap);
                //todo : logi
            });
        }
    }

    private void handleCanvasClick(double mouseX, double mouseY){
        System.out.println("poprawne klikniecie!!!");
        if (mouseX < gridOffset || mouseY < gridOffset) return;
        if (mouseX > gridOffset + gridWidth * cellSize) return;
        if (mouseY > gridOffset + gridHeight * cellSize) return;
        System.out.println("POPRAWNE KLIKNIECIE!!!");

        int col = (int) ((mouseX-gridOffset)/cellSize);
        int rowFromTop = (int) ((mouseY - gridOffset) / cellSize);

        int row = gridHeight - 1 - rowFromTop;

        Boundary boundary = worldMap.getCurrentBounds();
        int mapX = boundary.lowerLeft().getX() + col;
        int mapY = boundary.lowerLeft().getY() + row;

        handleMapFieldClick(mapX, mapY);
    }

    private void handleMapFieldClick(int x, int y) {
        Vector2d position = new Vector2d(x, y);
        Optional<List<Animal>> animalsOnPosition = worldMap.getAnimalsOrdered(position);

        if (animalsOnPosition.isEmpty() || animalsOnPosition.get().isEmpty()) return; // nie ma żadnego Animala na pozycji
        System.out.println("POPRAWNE KLIKNIECIE NA ANIMALA!!!");
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
        //todo: ustawic szerokosc i wysokosc okienka jako stałe
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
    }
}