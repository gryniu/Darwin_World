package agh.ics.oop.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimalsMap<T extends WorldElement> {
    private final Map<Vector2d, List<T>> animals = new HashMap<>();
    private int animalsCount = 0;

    public AnimalsMap(){}

    public void addAnimal(T animal){
        animals.computeIfAbsent(animal.position(), k -> new LinkedList<>()).add(animal);
        animalsCount++;
    }

    public void removeAnimal(T animal){
        if(!animals.get(animal.position()).remove(animal))
            throw new AnimalNotFoundException(animal);
        if(animals.get(animal.position()).isEmpty())
            animals.remove(animal.position());
        animalsCount--;
    }

    public Optional<List<T>> getFrom(Vector2d position){
        return Optional.ofNullable(animals.get(position));
    }

    public List<T> getAll() {
        synchronized (animals) {
            return animals.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .toList();
        }
    }



    public Set<Vector2d> getPositions(){
        return animals.keySet();
    }

    public Map<Vector2d, List<T>> getAnimalsHashMap() {
        return animals;
    }

    public int getAnimalsCount() {
        return animalsCount;
    }
}
