package agh.ics.oop.model;

import java.util.*;

public abstract class AbstractWorldMap<T extends AbstractAnimal> implements WorldMap{
    protected final AnimalsMap<T> animals = new AnimalsMap<>();
    protected final HashMap<Vector2d, Plant> plants = new HashMap<>();

    protected final int width;
    protected final int height;;
    protected int day;

    public AbstractWorldMap(int width, int height){
        this.width = width;
        this.height = height;
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
    public int getAnimalsCount() {
        return animals.getAnimalsCount();
    }

    @Override
    public int getPlantsCount(){
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

    public int getDay(){
        return day;
    }
}
