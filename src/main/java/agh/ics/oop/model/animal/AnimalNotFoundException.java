package agh.ics.oop.model.animal;

import agh.ics.oop.model.WorldElement;

public class AnimalNotFoundException extends RuntimeException {
    public AnimalNotFoundException(WorldElement animal) {
        super("Animal %s not found on %s!".formatted(animal, animal.position()));
    }
}
