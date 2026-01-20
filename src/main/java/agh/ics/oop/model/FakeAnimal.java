package agh.ics.oop.model;

public class FakeAnimal extends AbstractAnimal{
    private final int energy;

    public FakeAnimal(Vector2d position, MapDirection orientation, int energy, Gen gen){
        this.energy = energy;
        this.position = position;
        this.orientation = orientation;
        this.gen = gen;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public String dataToString() {
        return position.dataToString() + "," + orientation.dataToString() + ","
                + energy + "," + gen;
    }

}
