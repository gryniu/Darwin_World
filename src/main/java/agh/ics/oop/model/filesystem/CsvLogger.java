package agh.ics.oop.model.filesystem;

import agh.ics.oop.model.map.MapStats;
import agh.ics.oop.model.map.RealWorldMap;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.simulation.SimulationListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvLogger implements SimulationListener {
    private final File file;

    public CsvLogger(RealWorldMap worldMap){
        String homeDir = System.getProperty("user.home");

        file = new File(homeDir, "%s.csv".formatted(worldMap.getId()));

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Day,").append(MapStats.getLabel());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void change(WorldMap worldMap, MapStats mapStats, int day, boolean isLive) {
        if (isLive) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append(String.valueOf(day)).append(",").append(mapStats.getRow());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
