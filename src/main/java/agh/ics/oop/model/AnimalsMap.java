package agh.ics.oop.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimalsMap {
    private final Map<Vector2d, List<Animal>> animals = new HashMap<>();

    public AnimalsMap(){}

    public void addAnimal(Animal animal){
        animals.computeIfAbsent(animal.position(), k -> new LinkedList<>()).add(animal);
    }

    public void removeAnimal(Animal animal){
        if(!animals.get(animal.position()).remove(animal))
            throw new AnimalNotFoundException(animal);
    }

    public Optional<List<Animal>> getFrom(Vector2d position){
        return Optional.ofNullable(animals.get(position));
    }

    public List<Animal> getAll(){
        return animals.values().stream().flatMap(List::stream).toList();
    }

    public Set<Vector2d> getPositions(){
        return animals.keySet();
    }
}
