package agh.ics.oop.view;

import agh.ics.oop.model.*;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class MapRenderer {
    private final Simulation simulation;
    private final WorldMap initMap;
    private final Canvas canvas;
    private final Function<Integer, Boolean> detectWinter;

    // every cell is square
    private double cellSize = 40.0; // every cell is square
    private double borderWidth = 1.67;
    private double gridOffset = cellSize * 1.5;
    private double coordsFontSize = 20.0;
    private double fontSize = cellSize*0.3;
    private double energyBarWidth = cellSize*0.8;
    private double energyBarHeight =  energyBarWidth *0.15;
    private GraphicsContext gc;


    private List<Image> animalImages;
    private Image plantImage;

    private List<Color> summerColors = List.of(Color.valueOf("#78D23D"), Color.valueOf("#58BB43"), Color.valueOf("#3AA346"));
    private List<Color> winterColors = List.of(Color.valueOf("#9ECAE1"), Color.valueOf("#6BAED8"), Color.valueOf("#4292C6"));

    private Consumer<Vector2d> mapFieldClickAction;



    public MapRenderer(Canvas mapCanvas, Simulation simulation, WorldMap initMap, Function<Integer, Boolean> detectWinter){
        this.canvas = mapCanvas;
        this.simulation = simulation;
        this.initMap = initMap;
        this.detectWinter = detectWinter;
        animalImages = new ArrayList<>();
        gc = mapCanvas.getGraphicsContext2D();

        mapCanvas.setOnMouseClicked(event -> {
            if (simulation == null || !simulation.isPaused()) return;

            handleCanvasClick(event.getX(), event.getY());
        });

        loadImages();
        adjustCellSize(initMap);
    }

    private void loadImages(){
        for(int i = 0; i<8; i++){
            animalImages.add(new Image(
                    getClass().getResourceAsStream("/images/animal%d.png".formatted(i))
            ));
        }
        plantImage = new Image(getClass().getResourceAsStream("/images/plant.png"));

    }

    public void drawMap(WorldMap worldMap, MapStats mapStats) {
        clearGrid(worldMap);
        drawGrid(worldMap);
        drawWorldElements(worldMap, mapStats);
    }

    private void drawWorldElements(WorldMap worldMap, MapStats mapStats){
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
                if (Objects.equals(abstractAnimal.getGen().toString(), mapStats.mostPopularGenotype())){
                    gc.setFill(Color.rgb(255, 0, 255, 0.5));
                    gc.fillOval(posX, posY, cellSize, cellSize);
                }
                gc.drawImage(animalImages.get(abstractAnimal.getOrientation().ordinal()), posX, posY, cellSize, cellSize);
                gc.restore();
                drawEnergyBar(gc, abstractAnimal, mapStats, centerX, centerY);

            }else gc.drawImage(plantImage, posX, posY, cellSize, cellSize);
        }
        gc.restore();
    }

    private void drawGrid(WorldMap worldMap){
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
        drawCoords(worldMap);

        gc.restore();
    }

    private void clearGrid(WorldMap worldMap) {
        gc.save();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int canvasCol = 0; canvasCol < worldMap.getWidth(); canvasCol++) {
            for (int canvasRow = 0; canvasRow < worldMap.getHeight(); canvasRow++) {

                Vector2d worldPosition = new Vector2d(canvasCol, canvasRow);

                FieldCategory fieldCategory = simulation.getFieldCategory(worldPosition);
                boolean isWinter = detectWinter.apply(worldMap.getDay());

                gc.setFill(isWinter ? winterColors.get(fieldCategory.ordinal()) : summerColors.get(fieldCategory.ordinal()));
                gc.fillRect(gridOffset + canvasCol * cellSize,
                        gridOffset + canvasRow * cellSize,
                        cellSize, cellSize);
            }
        }
        gc.restore();
    }


    private void drawCoords(WorldMap worldMap) {
        double y = gridOffset;
        int i = worldMap.getHeight() - 1;
        while (y < canvas.getHeight() - gridOffset) {
            gc.fillText(String.valueOf(i--),
                    gridOffset / 2 - fontSize/2,
                    y + cellSize / 2 + borderWidth + fontSize / 4
            );
            y += cellSize;
        }

        double x = gridOffset;
        int j = 0;
        while (x < canvas.getWidth() - gridOffset) {
            gc.fillText(
                    String.valueOf(j++),
                    x + cellSize / 2 + borderWidth - fontSize / 2,
                    canvas.getHeight() - gridOffset / 2 + fontSize/4
            );
            x += cellSize;
        }
    }

    private void drawEnergyBar(GraphicsContext gc, AbstractAnimal animal, MapStats mapStats, double centerX, double centerY) {
        int p85 =  mapStats.simulationEnergyPercentiles().p85();
        int median = mapStats.simulationEnergyPercentiles().p50();

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

    private void adjustCellSize(WorldMap worldMap){
        double cellWidth = canvas.getWidth() / (worldMap.getWidth() + 3);
        double cellHeight = canvas.getHeight() / (worldMap.getHeight() + 3);
        cellSize = Math.min(cellWidth, cellHeight);

        borderWidth = cellSize/24.0;
        coordsFontSize = cellSize/2.0;
        gridOffset = cellSize * 1.5;
        fontSize = cellSize*0.3;
        energyBarWidth = cellSize*0.8;
        energyBarHeight =  energyBarWidth *0.15;

        canvas.setWidth(worldMap.getWidth() * cellSize + 2 * gridOffset);
        canvas.setHeight(worldMap.getHeight() * cellSize + 2 * gridOffset);
    }

    private void handleCanvasClick(double mouseX, double mouseY){
        if (mouseX < gridOffset || mouseY < gridOffset) return;
        if (mouseX > gridOffset + initMap.getWidth() * cellSize) return;
        if (mouseY > gridOffset + initMap.getHeight() * cellSize) return;

        int col = (int) ((mouseX-gridOffset)/cellSize);
        int rowFromTop = (int) ((mouseY - gridOffset) / cellSize);

        int row = initMap.getHeight() - 1 - rowFromTop;

        if(mapFieldClickAction != null)
            mapFieldClickAction.accept(new Vector2d(col, row));
    }

    public void setMapFieldClickAction(Consumer<Vector2d> mapFieldClickAction) {
        this.mapFieldClickAction = mapFieldClickAction;
    }
}
