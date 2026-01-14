package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationConfig;
import agh.ics.oop.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SimulationPresenter implements Initializable {
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
    private RealWorldMap worldMap;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = mapCanvas.getGraphicsContext2D();

        startButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.setPausedSimulation(false);
            }
        });

        pauseButton.setOnAction(e -> {
            if (simulation != null) {
                simulation.setPausedSimulation(true);
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

        this.worldMap = new RealWorldMap(mapOptions,animalOptions);

        Boundary boundary = worldMap.getCurrentBounds();
        gridWidth = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        gridHeight = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;

        mapCanvas.setWidth(gridWidth*CELL_SIZE + 2*GRID_OFFSET);
        mapCanvas.setHeight(gridHeight*CELL_SIZE + 2*GRID_OFFSET);

        simulation = new Simulation(worldMap, 200);

        simulation.addMapChangeListener((worldMap, message) -> {
            javafx.application.Platform.runLater(() -> {
                updateLabels((RealWorldMap) worldMap);
                drawMap((RealWorldMap) worldMap);
                //todo : logi
            });
        });
        simulation.setPausedSimulation(false);

        simulationThread = new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();
    }


    private void updateLabels(RealWorldMap worldMap){
    }

    private void drawMap(RealWorldMap worldMap) {

        clearGrid();
        drawGrid(worldMap);
        drawWorldElements(worldMap);
    }

    private void drawWorldElements(RealWorldMap worldMap){
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
            if (!(worldElement instanceof Animal)) { //  w pierwszej kolejnosci wyswietlamy Animala
                Optional<WorldElement> elementAtPos = worldMap.objectAt(worldElement.position());

                if (elementAtPos.isPresent() && elementAtPos.get() instanceof Animal) {
                    continue;
                }
            }
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

    private void drawGrid(RealWorldMap worldMap){
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

}