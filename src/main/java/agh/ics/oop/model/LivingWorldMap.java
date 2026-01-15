package agh.ics.oop.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LivingWorldMap extends WorldMap{
    void removeDeadAnimals();

    void eatAllPossiblePlants();
    void moveAllAnimals();
    void reproducePopulation(int day);
    void createNewPlants();

    void addSubscriber(Listener observer);
    void removeSubscriber(Listener observer);
    void mapChanged(String message);

    void decreaseEnergyAllAnimals();

    String mapDataToString();

    Optional<List<Animal>> getAnimalsOrdered(Vector2d position);
    List<Animal> getAllAnimalsOrdered();


    UUID getId();
}
