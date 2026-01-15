package agh.ics.oop.model;

public class FakeAnimal extends AbstractAnimal {
    public FakeAnimal(Vector2d position, MapDirection orientation, int energy){
        this.energy = energy;
        this.position = position;
        this.orientation = orientation;
    }
}
