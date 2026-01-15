package agh.ics.oop.model;

import java.io.*;
import java.util.*;

public class HistoryFileHandler {
    private final static String DIR_PATH = "tmp";

    public static void writeToFile(String fileName, Iterable<? extends WorldElement> items){

        File dir = new File(DIR_PATH);
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DIR_PATH + fileName))) {
            for (var item : items) {
                bw.write(item.dataToString() + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<FakeAnimal> importAnimals(UUID id, int day){
        List<FakeAnimal>  animals = new ArrayList<>();

        String fileName = DIR_PATH + "/%s-%s-animals.txt".formatted(id, day);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] animalData = line.split(",");
                if (animalData.length != 3) throw new CorruptedFileException(fileName);

                int[] coords = Arrays.stream(animalData[0].split(";")).mapToInt(Integer::parseInt).toArray();
                Vector2d position = new Vector2d(coords[0], coords[1]);
                MapDirection orientation = MapDirection.values()[Integer.parseInt(animalData[1])];
                int energy = Integer.parseInt(animalData[2]);

                animals.add(new FakeAnimal(position, orientation, energy));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return animals;
    }

    public static List<Vector2d> importPlants(UUID id, int day){
        List<Vector2d> positions = new ArrayList<>();

        String fileName = DIR_PATH + "/%s-%s-plants.txt".formatted(id, day);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                int[] coords = Arrays.stream(line.split(";")).mapToInt(Integer::parseInt).toArray();
                if (coords.length != 2) throw new CorruptedFileException(fileName);
                positions.add(new Vector2d(coords[0], coords[1]));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return positions;
    }

    public static void deleteHistory(UUID id){
        String prefix = id.toString();

        File folder = new File(DIR_PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder nie istnieje!");
            return;
        }

        File[] filesToDelete = folder.listFiles((dir, name) -> name.startsWith(prefix));

        if (filesToDelete == null || filesToDelete.length == 0) {
            System.out.println("Nie znaleziono plików do usunięcia.");
            return;
        }

        for (File file : filesToDelete) {
            if (file.delete()) {
                System.out.println("Usunięto plik: " + file.getName());
            } else {
                System.out.println("Nie udało się usunąć pliku: " + file.getName());
            }
        }
    }

    public static void clearHistory(){
        File folder = new File(DIR_PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder nie istnieje!");
            return;
        }

        File[] filesToDelete = folder.listFiles();

        if (filesToDelete == null || filesToDelete.length == 0) {
            System.out.println("Nie znaleziono plików do usunięcia.");
            return;
        }

        for (File file : filesToDelete) {
            if (file.delete()) {
                System.out.println("Usunięto plik: " + file.getName());
            } else {
                System.out.println("Nie udało się usunąć pliku: " + file.getName());
            }
        }
    }
}
