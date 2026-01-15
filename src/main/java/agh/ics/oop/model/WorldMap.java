package agh.ics.oop.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo, idzik
 */
public interface WorldMap{

    Optional<List<Animal>> getAnimals(Vector2d position);

    List<Plant> getPlants();
    List<Animal> getAllAnimals();

    Boundary getCurrentBounds();
    Optional<WorldElement> objectAt(Vector2d position);
    int getAnimalsCount();
}