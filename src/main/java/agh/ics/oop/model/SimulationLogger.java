package agh.ics.oop.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimulationLogger implements Listener {
    @Override
    public void change(AbstractWorldMap worldMap, String message) {
        String dirPath = "history"; // or wherever you want to save
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dirPath + "/%s-%s-animals.txt".formatted(worldMap.getId(), message)))) {
            for (var item : worldMap.getAllAnimals()) {
                bw.write(item.animalDataToString() + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dirPath + "/%s-%s-plants.txt".formatted(worldMap.getId(), message)))) {
            for (var item : worldMap.getPlants()) {
                bw.write(item.position().toString() + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(FileWriter fileWriter = new FileWriter(dirPath + "/%s-%s-map.txt".formatted(worldMap.getId(), message))){
            fileWriter.write(worldMap.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
