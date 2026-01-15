package agh.ics.oop.model;

import java.util.*;
import java.util.stream.Collectors;

public interface LivingWorldMap extends WorldMap{

    void removeDeadAnimals(int day);
    void eatAllPossiblePlants();
    void moveAllAnimals();
    void reproducePopulation(int day);
    void createNewPlants();

    void addSubscriber(Listener observer);
    void removeSubscriber(Listener observer);
    void mapChanged(String message);

    void decreaseEnergyAllAnimals();

    Optional<List<Animal>> getAnimalsOrdered(Vector2d position);
    List<Animal> getAllAnimalsOrdered();

    UUID getId();
}
