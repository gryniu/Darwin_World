package agh.ics.oop.model;

public class AnimalFactory {
    private final AnimalOptions animalOptions;

    public AnimalFactory(AnimalOptions animalOptions) {
        this.animalOptions = animalOptions;
    }

    public Animal createInitial(Vector2d position, int startEnergy, int dayOfBirth){
        return new Animal(position, animalOptions, startEnergy, dayOfBirth);
    }

    public Animal createChild(Vector2d position, AnimalData animalData){
        return new Animal(position, animalOptions, animalData);
    }
}
