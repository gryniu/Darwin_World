package agh.ics.oop.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class CsvLogger implements Listener{
    private final static String DIR_PATH = "csv";
    private final File file;

    public CsvLogger(LivingWorldMap worldMap){
        File dir = new File(DIR_PATH);
        if (!dir.exists()) dir.mkdirs();

        file = new File("%s/%s.csv".formatted(DIR_PATH, worldMap.getId()));

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Day," + worldMap.getMapStats().getLabel());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void change(WorldMap worldMap, String message) {
        if (worldMap instanceof LivingWorldMap) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append(message + "," + worldMap.getMapStats().getRow());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
