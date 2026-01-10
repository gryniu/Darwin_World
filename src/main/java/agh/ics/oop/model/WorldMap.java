package agh.ics.oop.model;

import java.util.ArrayList;
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

    /**
     * Place a new animal on the map.
     *
     * @param animal The animal to be placed on the map.
     * @return True if the animal was placed. The rules for valid placement are the same as for movement.
     */
    void place(Animal animal);

    /**
     * Moves an animal (if it is present on the map) according to specified direction.
     * If the move is not possible, this method has no effect.
     */
    void move(Animal animal);

    Optional<List<Animal>> getAnimals(Vector2d position);
    Optional<List<Animal>> getAnimalsOrdered(Vector2d position);

    List<Animal> getAllAnimals();
    List<Animal> getAllAnimalsOrdered();

    void addSubscriber(MapChangeListener observer);
    void removeSubscriber(MapChangeListener observer);
    void mapChanged(String message);

    UUID getId();

    Boundary getCurrentBounds();
}