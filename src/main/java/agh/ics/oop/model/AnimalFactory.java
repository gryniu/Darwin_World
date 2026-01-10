package agh.ics.oop.model;

public class AnimalFactory {
    private final AnimalOptions animalOptions;

    public AnimalFactory(AnimalOptions animalOptions) {
        this.animalOptions = animalOptions;
    }

    public Animal createInitial(Vector2d position, int startEnergy){
        return new Animal(position, animalOptions, startEnergy);
    }

    public Animal createChild(Vector2d position, int startEnergy, Gen gen){
        return new Animal(position, animalOptions, startEnergy,gen);
    }
}
