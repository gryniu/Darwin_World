package agh.ics.oop.model;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    protected final UUID id = UUID.randomUUID();
    protected final AnimalsMap animals = new AnimalsMap();
    private final ArrayList<MapChangeListener> subscribers = new ArrayList<>();
    protected final MapVisualizer mapVisualizer = new MapVisualizer(this);


    @Override
    public void place(Animal animal) {
        Vector2d position = animal.position();
        animals.addAnimal(animal);
        mapChanged("animal placed on %s".formatted(position));
    }

    @Override
    public void move(Animal animal) {
        Vector2d oldPosition = animal.position();
        animals.removeAnimal(animal);
        Vector2d newPosition = oldPosition.add(animal.getOrientation().toUnitVector());
        animal.setPosition(newPosition);
        animals.addAnimal(animal);
        mapChanged("Animal moved from %s to %s".formatted(oldPosition, newPosition));
    }

    @Override
    public String toString(){
        Boundary bounds = getCurrentBounds();
        return mapVisualizer.draw(bounds.lowerLeft(), bounds.upperRight());
    }

    @Override
    public Optional<List<Animal>> getAnimals(Vector2d position){
        return animals.getFrom(position);
    }

    @Override
    public List<WorldElement> getAllAnimals() {
        return new ArrayList<>(animals.getAll());
    }

    @Override
    public void addSubscriber(MapChangeListener subscriber){
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(MapChangeListener subscriber){
        subscribers.remove(subscriber);
    }

    @Override
    public void mapChanged(String message){
        for(MapChangeListener subscriber: subscribers){
            subscriber.mapChanged(this, message);
        }
    }

    @Override
    public UUID getId(){
        return id;
    }
}
