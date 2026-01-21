package agh.ics.oop.model.map;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.WorldElement;
import agh.ics.oop.model.animal.AbstractAnimal;

import java.util.List;
import java.util.Optional;

public interface WorldMap{

    <T extends AbstractAnimal>
    Optional<List<T>> getAnimals(Vector2d position);

    List<Plant> getPlants();

    <T extends AbstractAnimal>
    List<T> getAllAnimals();

    List<WorldElement> getAllMapElements();

    int getAnimalsCount();
    int getPlantsCount();

    int getWidth();
    int getHeight();

    int getDay();
}