package agh.ics.oop.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimulationLogger implements Listener {
    @Override
    public void change(WorldMap worldMap, String message) {
        if (worldMap instanceof LivingWorldMap worldMap1) {
            //zapisywanie animali
            HistoryFileHandler.writeToFile("/%s-%s-animals.txt".formatted(worldMap1.getId(), message), worldMap.getAllAnimals());

            //zapisywanie roslin
            HistoryFileHandler.writeToFile("/%s-%s-plants.txt".formatted(worldMap1.getId(), message), worldMap.getPlants());

            //zapisywanie statystyk
            HistoryFileHandler.writeToFile("/%s-%s-stats.txt".formatted(worldMap1.getId(), message), worldMap.getMapStats().toString());
        }
    }
}
