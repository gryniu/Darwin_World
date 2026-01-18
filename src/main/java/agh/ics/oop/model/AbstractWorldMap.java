package agh.ics.oop.model;

import javafx.scene.paint.Color;

import java.util.*;

public abstract class AbstractWorldMap<T extends AbstractAnimal> implements WorldMap{
    protected final AnimalsMap<T> animals = new AnimalsMap<>();
    protected final HashMap<Vector2d, Plant> plants = new HashMap<>();

    //liczniki częstotliwości
    protected final Map<String, Integer> genotypeCounter = new HashMap<>();
    protected final Map<Vector2d, Long> plantsFrequencyCounter = new HashMap<>();

    protected final int width;
    protected final int height;
    protected Long maxNumOfPlantsOnPosition = 7L;
    // ustawiam na poczatek na 7 zeby nie bylo sytuacji,
    // ze na poczatku wszystko jest na ciemno-zielono

    public AbstractWorldMap(int width, int height){
        this.width = width;
        this.height = height;
    }

    protected int getEnergyPercentile(int percentile){
        if (percentile < 0 || percentile > 100) throw new IllegalArgumentException("Percentile must be in [0,100]");
        if (animals.getAll().isEmpty()) return 0;

        List<Integer> energies = animals.getAll().stream()
                .map(T::getEnergy)
                .sorted()
                .toList();

        int index = (int) Math.ceil(percentile / 100.0 * energies.size()) - 1;
        index = Math.max(0, Math.min(index, energies.size() - 1));

        return energies.get(index);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<T> getAllAnimals() {
        return animals.getAll();
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width-1, height-1));
    }

    @Override
    public int getAnimalsCount() {
        return animals.getAnimalsCount();
    }

    @Override
    public int getPlantsCount(){
//        System.out.println(plants.values()); // po co
        return plants.size();
    }


    @Override
    public Optional<List<T>> getAnimals(Vector2d position) {
        return animals.getFrom(position);
    }

    @Override
    public List<Plant> getPlants() {
        return new ArrayList<>(plants.values());
    }

    @Override
    public List<WorldElement> getAllMapElements() {
        List<WorldElement> elements = new ArrayList<>();
        elements.addAll(getPlants());
        elements.addAll(getAllAnimals());
        return elements;
    }

    public Color getColorOfField(Vector2d fieldPosition){
        Long plantsFrequency = plantsFrequencyCounter.getOrDefault(fieldPosition,0L);
        if (plantsFrequency < maxNumOfPlantsOnPosition*.33) return Color.valueOf("#78D23D");
        if (plantsFrequency < maxNumOfPlantsOnPosition*.75) return Color.valueOf("#58BB43");
        return Color.valueOf("#3AA346");
    }
}
