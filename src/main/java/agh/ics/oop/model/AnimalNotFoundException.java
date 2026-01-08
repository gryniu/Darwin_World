package agh.ics.oop.model;

public class AnimalNotFoundException extends RuntimeException {
    public AnimalNotFoundException(Animal animal) {
        super("Animal %s not found on %s!".formatted(animal, animal.position()));
    }
}
