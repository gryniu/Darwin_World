package agh.ics.oop.model;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Animal implements WorldElement{
    private Vector2d position;
    private MapDirection orientation = MapDirection.NORTH;
    private double energy;
    private final Gen gen;
    private final Iterator<Integer> genIterator;
    private final int mutationNum;
    private final EnergyOptions energyOptions;
    public Animal(Vector2d position, AnimalOptions animalOptions) {
        this.position = position;
        this.gen = new Gen(animalOptions.lenOfGen());
        this.genIterator = gen.iterator();
        this.mutationNum = animalOptions.mutationNum();
        this.energyOptions = animalOptions.energyOptions();
        this.energy = this.energyOptions.energyStart();
    }

    public double getEnergy() {
        return energy;
    }

    public void eat(){
        energy += energyOptions.energyAfterPlant();
    }

    public boolean isFeed(){
        return energy>= energyOptions.energyFeed();
    }

    public Optional<Animal> sex(Animal partner){
        if (!(this.isFeed() && partner.isFeed())){
            Optional.empty();
        }

    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d position() {
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return switch (orientation) {
            case NORTH -> "↑";
            case EAST -> "→";
            case SOUTH -> "↓";
            case WEST -> "←";
            case EAST_NORTH -> "↗";
            case EAST_SOUTH -> "↘";
            case WEST_SOUTH -> "↙";
            case WEST_NORTH -> "↖";
        };
    }

    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void rotate(){
        orientation = orientation.next(genIterator.next());
    }

}
