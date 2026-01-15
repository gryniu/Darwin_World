package agh.ics.oop.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorldMap{

    <T extends AbstractAnimal>
    Optional<List<T>> getAnimals(Vector2d position);

    List<Plant> getPlants();
    <T extends AbstractAnimal>
    List<T> getAllAnimals();
    List<WorldElement> getAllMapElements();

    Boundary getCurrentBounds();

    int getAnimalsCount();
    int getPlantsCount();

    int getWidth();
    int getHeight();

    MapStats getMapStats();
}