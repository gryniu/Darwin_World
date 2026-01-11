package agh.ics.oop.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWorldMap implements WorldMap {
    protected final UUID id = UUID.randomUUID();
    protected final AnimalsMap animals = new AnimalsMap();
    private final ArrayList<MapChangeListener> subscribers = new ArrayList<>();
    protected final MapVisualizer mapVisualizer = new MapVisualizer(this);



    @Override
    public void place(Animal animal) {
        Vector2d position = animal.position();
        if (!inBounds(animal.position())){
            throw new IncorrectPositionException(position,getCurrentBounds());
        }
        animals.addAnimal(animal);
        mapChanged("animal placed on %s".formatted(position));
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
    public Optional<List<Animal>> getAnimalsOrdered(Vector2d position) {
        return getAnimals(position)
                .map(items -> items
                        .stream()
                        .sorted(Comparator
                                .comparingInt(Animal::getEnergy).reversed()
                                .thenComparingInt(Animal::getDayOfBirth)
                                 .thenComparingInt(Animal::getNumOfKids).reversed()
                                .thenComparingDouble(animal -> ThreadLocalRandom.current().nextDouble())
                        )
                        .toList());
    }

    @Override
    public List<Animal> getAllAnimals() {
        return new ArrayList<>(animals.getAll());
    }

    @Override
    public List<Animal> getAllAnimalsOrdered() {
        return getAllAnimals()
                .stream()
                .sorted(Comparator
                            .comparingInt(Animal::getEnergy).reversed()
                            .thenComparing(Animal::getDayOfBirth)
                            .thenComparing(Animal::getNumOfKids).reversed()
                            .thenComparingDouble(animal -> ThreadLocalRandom.current().nextDouble())
                    )
                    .toList();
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

    public boolean inBounds(Vector2d position){
        Boundary boundary = getCurrentBounds();
        return position.follows(boundary.lowerLeft()) && position.precedes(boundary.upperRight());
    }

    public AnimalsMap getAnimalsMap(){
        return animals;
    }
}
