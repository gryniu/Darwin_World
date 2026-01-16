package agh.ics.oop.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FakeWorldMap implements WorldMap{
    private final AnimalsMap<FakeAnimal> animals = new AnimalsMap<>();
    private final HashMap<Vector2d, Plant> plants = new HashMap<>();
    private final MapStats mapStats;
    private final int width;
    private final int heigh;

    public FakeWorldMap(UUID id, int day, int width, int height) {
        this.width = width;
        this.heigh = height;
        //importowanie animali
        for(var animal: HistoryFileHandler.importAnimals(id, day)){
            animals.addAnimal(animal);
        }

        //importowanie roslin
        for(var position: HistoryFileHandler.importPlants(id, day)){
            createPlant(position);
        }

        mapStats = HistoryFileHandler.importStats(id, day);
    }

    private  void createPlant(Vector2d position){
        plants.put(position, new Plant(position));
    }

    @Override
    public Optional<List<FakeAnimal>> getAnimals(Vector2d position) {
        return animals.getFrom(position);
    }

    @Override
    public List<Plant> getPlants() {
        return new ArrayList<>(plants.values());
    }

    @Override
    public List<WorldElement> getAllMapElements() {
        List<WorldElement> elements = new ArrayList<>();
        elements.addAll(getAllAnimals());
        elements.addAll(getPlants());
        return elements;
    }

    @Override
    public List<FakeAnimal> getAllAnimals() {
        return animals.getAll();
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width-1, heigh-1));
    }

    @Override
    public int getAnimalsCount() {
        return animals.getAnimalsCount();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return heigh;
    }

    @Override
    public MapStats getMapStats() {
        return mapStats;
    }

    public int getPlantsCount(){
        return plants.size();
    }

    @Override
    public int getEnergyPercentile(int percentile){
        if (percentile < 0 || percentile > 100) throw new IllegalArgumentException("Percentile must be in [0,100]");
        if (animals.getAll().isEmpty()) return 0;

        List<Integer> energies = animals.getAll().stream()
                .map(FakeAnimal::getEnergy)
                .sorted()
                .toList();

        int index = (int) Math.ceil(percentile / 100.0 * energies.size()) - 1;
        index = Math.max(0, Math.min(index, energies.size() - 1));

        return energies.get(index);
    }
}
