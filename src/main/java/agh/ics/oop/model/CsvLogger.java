package agh.ics.oop.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class CsvLogger implements SimulationListener{
    private final File file;

    public CsvLogger(RealWorldMap worldMap){
        String homeDir = System.getProperty("user.home");

        file = new File(homeDir, "%s.csv".formatted(worldMap.getId()));

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Day,").append(worldMap.getMapStats().getLabel());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void change(WorldMap worldMap, int day, boolean isLive) {
        if (isLive && worldMap instanceof RealWorldMap realWorldMap) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append(String.valueOf(day)).append(",").append(realWorldMap.getMapStats().getRow());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
