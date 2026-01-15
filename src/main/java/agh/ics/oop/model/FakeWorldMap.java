package agh.ics.oop.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FakeWorldMap implements WorldMap{
    private final AnimalsMap animals = new AnimalsMap();
    private final HashMap<Vector2d, Plant> plants = new HashMap<>();
    private final int width;
    private final int heigh;

    public FakeWorldMap(UUID id, int day, int width, int height) {
        //importowanie animali
        String filename = "history/%s-%s-animals".formatted(id, day);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] animalData = line.split(",");
                if (animalData.length != 6) throw new CorruptedFileException(filename);

                Gen gen = new Gen(animalData[0]
                        .chars()
                        .map(c -> c - '0')
                        .boxed()
                        .toList());

                int[] coords = Arrays.stream(animalData[1].split(";")).mapToInt(Integer::parseInt).toArray();
                Vector2d position = new Vector2d(coords[0], coords[1]);
                MapDirection orientation = MapDirection.values()[Integer.parseInt(animalData[2])];
                int energy = Integer.parseInt(animalData[3]);
                int dayOfBirth = Integer.parseInt(animalData[3]);
                int numOfKids = Integer.parseInt(animalData[3]);
                Animal animal = new Animal(position, orientation, energy);
                animals.addAnimal(animal);
            }
        } catch (IOException e) {
            throw new CorruptedFileException(filename);
        }

        //importowanie roslin
        filename = "history/%s-%s-plants".formatted(id, day);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                int[] coords = Arrays.stream(line.split(";")).mapToInt(Integer::parseInt).toArray();
                if (coords.length != 2) throw new CorruptedFileException(filename);
                createPlant(new Vector2d(coords[0], coords[1]));
            }
        } catch (IOException e) {
            throw new CorruptedFileException(filename);
        }
    }

    @Override
    public Optional<List<Animal>> getAnimals(Vector2d position) {
        return animals.getFrom(position);
    }

    @Override
    public List<Plant> getPlants() {
        return new ArrayList<>(plants.values());
    }

    @Override
    public List<Animal> getAllAnimals() {
        return animals.getAll();
    }

    @Override
    public Boundary getCurrentBounds() {
        return null;
    }

    @Override
    public Optional<WorldElement> objectAt(Vector2d position) {
        return Optional.empty();
    }

    @Override
    public int getAnimalsCount() {
        return 0;
    }
}
